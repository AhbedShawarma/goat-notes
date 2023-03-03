package com.goat.app.business

import com.goat.app.business.sync.FolderMessage
import com.goat.app.business.sync.NoteMessage
import com.goat.app.business.sync.SyncClient
import com.goat.app.persistence.Context
import com.goat.app.persistence.Model
import com.goat.app.persistence.NavigationViewSelection
import com.goat.app.persistence.models.Folder
import java.time.LocalDate
import com.goat.app.persistence.models.INote
import com.goat.app.persistence.models.Note
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Timer
import java.util.TimerTask

class Controller(private val model: Model, private val sync: SyncClient) {

    private val autosave : Timer = Timer()

    init {
        autosave.scheduleAtFixedRate(object: TimerTask() {
            override fun run() {
                saveNotes()
            }
        }, 5000, 5000)
    }

    fun close() {
        saveNotes()
        autosave.cancel()
        autosave.purge()
    }

    fun createNote(folderId: Int = 1) {
        saveNotes()
        model.createNote(folderId)
    }

    fun createFolder(parentId: Int = 1) {
        model.createFolder(parentId)
    }

    fun setFocusedNoteTitle(newTitle: String) {
        model.setFocusedNoteTitle(newTitle)
    }

    fun setFocusedNoteText(newText: String) {
        model.setFocusedNoteText(newText)
    }

    fun setFocusedNoteById(id: Int) {
        model.setFocusedNoteById(id)
    }

    fun deleteNoteById(id: Int) {
        model.deleteNoteById(id)
    }

    fun deleteFolderById(id: Int) {
        model.deleteFolderById(id)
    }

    fun moveNote(id: Int, newFolderId: Int) {
        model.moveNote(id, newFolderId)
    }

    fun moveFolder(id: Int, newParentId: Int) {
        model.moveFolder(id, newParentId)
    }

    fun renameNote(id: Int, newTitle: String) {
        model.renameNote(id, newTitle)
    }

    fun renameFolder(id: Int, newName: String) {
        model.renameFolder(id, newName)
    }

    fun saveNotes() {
        model.saveNotes()
    }

    fun boldText() {
        val ctx = Context()
        ctx.isBoldClicked = true
        model.update(ctx)
    }

    fun italicizeText() {
        val ctx = Context()
        ctx.isItalicsClicked = true
        model.update(ctx)
    }

    fun headerOneText() {
        val ctx = Context()
        ctx.isHeaderOneClicked = true
        model.update(ctx)
    }

    fun headerTwoText() {
        val ctx = Context()
        ctx.isHeaderTwoClicked = true
        model.update(ctx)
    }

    fun headerThreeText() {
        val ctx = Context()
        ctx.isHeaderThreeClicked = true
        model.update(ctx)
    }

    fun inlineCodeText() {
        val ctx = Context()
        ctx.isInlineCodeClicked = true
        model.update(ctx)
    }

    fun hyperlinkText() {
        val ctx = Context()
        ctx.isHyperlinkClicked = true
        model.update(ctx)
    }

    fun ruleInsert() {
        val ctx = Context()
        ctx.isRuleClicked = true
        model.update(ctx)
    }

    fun unorderedListText() {
        val ctx = Context()
        ctx.isUnorderedListClicked = true
        model.update(ctx)
    }

    fun orderedListText() {
        val ctx = Context()
        ctx.isOrderedListClicked = true
        model.update(ctx)
    }

    fun setNavigationViewSelection(nvs: NavigationViewSelection) {
        model.navigationViewSelection = nvs
    }

    fun search(searchTerm: String) {
        model.searchTerm = searchTerm
    }

    fun folderPathMap(parentId: Int = 1): HashMap<Int, String> {
        return model.folderPathMap(parentId)
    }

    fun undoNoteBody() {
        val ctx = Context()
        ctx.isUndoPressed = true
        model.update(ctx)
    }

    fun redoNoteBody() {
        val ctx = Context()
        ctx.isRedoPressed = true
        model.update(ctx)
    }

    fun changeFocusedNoteDate(newDate: LocalDate) {
        model.changeFocusedNoteDate(newDate)
    }

    fun closeNote(note: INote? = null) {
        if (note != null) model.closeNote(note)
        else model.closeNote()
    }

    fun openNextTab() {
        model.openNextTab()
    }

    fun openPreviousTab() {
        model.openPreviousTab()
    }

    fun setFilteredDate(date: LocalDate) {
        model.filteredDate = date
    }

    fun pullNotes() {
        val pulledNotes = sync.getNotes()
        println(pulledNotes)
    }

    fun pullFromRemote() {
        if (!sync.serverExists()) {
            return
        }

        val focusedNoteId = model.focusedNote.id

        // get notes and folders from remote
        var pulledNoteMsgs: Deferred<List<NoteMessage>>
        var pulledFolderMsgs: Deferred<List<FolderMessage>>
        var pulledNotes: List<Note>
        var pulledFolders: List<Folder>
        runBlocking {
            pulledNoteMsgs = async { sync.getNotes() }
            pulledFolderMsgs = async { sync.getFolders() }

            pulledNotes = pulledNoteMsgs.await().map { noteMsg ->
                Note(
                    noteMsg.title, noteMsg.body, noteMsg.folder_id, noteMsg.id, noteMsg.tagged_date, dirty = true)
            }
            pulledFolders = pulledFolderMsgs.await().map { folderMsg ->
                Folder(
                    folderMsg.id, folderMsg.parent_id, folderMsg.name)
            }
        }

        model.folders.clear()
        model.folders.addAll(pulledFolders.sortedBy { folder -> folder.id })
        model.saveFolders()

        model.notes.clear()
        model.notes.addAll(pulledNotes)
        model.saveNotes()

        model.setFocusedNoteById(focusedNoteId)
        val ctx = Context()
        ctx.isFocusedNoteChanged = true // update the focused note text
        model.update(ctx)
    }

    fun pushToRemote() {
        if (!sync.serverExists()) {
            return
        }

        // delete all remote notes and folders
        runBlocking {
            launch { sync.deleteAllNotes() }
            launch { sync.deleteAllFolders() }
        }

        // post all notes and folders to remote
        runBlocking{
            model.notes.forEach { note ->
                launch {
                    val noteMsg = NoteMessage(note.id, note.title, note.body, note.folderId, note.taggedDate)
                    val returnCode = sync.postNote(noteMsg)
                    println(returnCode)
                }
            }
            model.folders.forEach{ folder ->
                launch {
                    val folderMsg = FolderMessage(folder.id, folder.parentId, folder.name)
                    val returnCode = sync.postFolder(folderMsg)
                    println(returnCode)
                }
            }
        }
    }
}