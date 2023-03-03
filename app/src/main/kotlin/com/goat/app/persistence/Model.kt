package com.goat.app.persistence

import com.goat.app.persistence.db.DBConnection
import com.goat.app.persistence.interfaces.IFolderRepository
import com.goat.app.persistence.interfaces.INoteRepository
import com.goat.app.persistence.models.Folder
import com.goat.app.persistence.models.IFolder
import com.goat.app.persistence.models.INote
import com.goat.app.persistence.models.Note
import com.goat.app.persistence.repositories.FolderRepository
import com.goat.app.persistence.repositories.NoteRepository
import com.goat.app.presentation.IView
import com.goat.app.presentation.utils.DateStringConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.max

class Model(dbConnection: DBConnection) {
    private val views = mutableListOf<IView>()
    val notes = mutableListOf<INote>()
    val filteredNotes = mutableListOf<INote>()
    val folders = mutableListOf<IFolder>()
    val noteRepository: INoteRepository = NoteRepository()
    val folderRepository: IFolderRepository = FolderRepository()
    var filteredDate: LocalDate? = LocalDate.now()
        set(value) {
            field = value
            val ctx = Context()
            update(ctx)
        }
    val datesWithNotes = mutableListOf<LocalDate>()
    var searchTerm = ""
        set(value) {
            field = value
            filterNotes()
            update(Context())
        }
    var navigationViewSelection = NavigationViewSelection.NOTES
        set(value) {
            if (value != NavigationViewSelection.NONE) {
                field = NavigationViewSelection.NONE
                update(Context())
            }
            field = value
            update(Context())
        }

    init {
        noteRepository.setSQLConnection(dbConnection)
        folderRepository.setSQLConnection(dbConnection)
        loadData()
    }

    // we initialize the focused note after the notes have been loaded from the database
    var focusedNote: INote = initFocusedNote()
        private set(value) {
            value.dirty = true
            field = value
            val ctx = Context()
            ctx.isFocusedNoteChanged = true
            if (openNotes != null && openNotes.none { it.id == value.id }) openNotes.add(value)
            update(ctx)
        }

    val openNotes = mutableListOf(focusedNote)

    fun addView(view: IView) {
        views.add(view)
        update(Context())
    }

    fun update(ctx: Context = Context()) {
        filterNotes()
        for (view in views) {
            view.update(ctx)
        }
    }

    private fun initFocusedNote(): INote {
        if (notes.isEmpty()) {
            createNote()
        }
        return notes.first()
    }

    fun createNote(folderId: Int = 1) {
        val newNote = Note("New Note", "", folderId=folderId)
        noteRepository.insertNote(newNote)
        notes.add(newNote)
        focusedNote = newNote
    }

    fun saveNotes() {
        val dirtyNotes = notes.filter { note -> note.dirty }
        noteRepository.saveNotes(dirtyNotes)
        dirtyNotes.forEach { note -> note.dirty = false }
    }

    fun setFocusedNoteById(id: Int) {
        val target = notes.findLast { note -> note.id == id }
        if (target != null) {
            focusedNote = target
        }
    }

    fun deleteNoteById(id: Int) {
        val target = notes.findLast { note -> note.id == id }
        if (target != null) {
            notes.remove(target)
            noteRepository.deleteNote(target)
            focusedNote = initFocusedNote()
            closeNote(target)
            update(Context())
        }
    }

    fun setFocusedNoteTitle(newTitle: String) {
        focusedNote.title = newTitle
        focusedNote.dirty = true
        val ctx = Context()
        ctx.anyNoteRenamed = true
        update(ctx)
    }

    fun setFocusedNoteText(newText: String) {
        focusedNote.body = newText
        focusedNote.dirty = true
        update(Context())
    }

    private fun loadData() {
        val dbNotes = noteRepository.getAllNotes()
        notes.clear()
        notes.addAll(dbNotes)

        val dbFolders = folderRepository.getAllFolders()
        folders.clear()
        folders.addAll(dbFolders)
        update(Context())
    }

    fun createFolder(parent_id: Int = 1) {
        val newFolder = Folder(name = "New Folder", parentId = parent_id)
        folderRepository.createFolder(newFolder)
        folders.add(newFolder)
        update(Context())
    }

    fun renameNote(id: Int, newTitle: String) {
        val note = notes.find { note -> note.id == id }
        if (note != null) {
            note.title = newTitle
            note.dirty = true
            noteRepository.updateNote(note)
            val ctx = Context()
            ctx.anyNoteRenamed = true
            ctx.isFocusedNoteChanged = note.id == focusedNote.id
            update(ctx)
        }
    }

    fun renameFolder(id: Int, newName: String) {
        val folder = folders.find { folder -> folder.id == id }
        if (folder != null) {
            folder.name = newName
            folderRepository.updateFolder(folder)
            val ctx = Context()
            ctx.anyFolderRenamed = true
            update(ctx)
        }
    }

    fun changeFocusedNoteDate(newDate: LocalDate) {
        val formatter = DateStringConverter()
        focusedNote.taggedDate = formatter.toString(newDate)
        focusedNote.dirty = true
        update()
    }

    fun deleteFolderById(id: Int) {
        val target = folders.findLast { folder -> folder.id == id }
        if (target != null) {
            folderRepository.deleteFolder(id)

            // reload notes and folders
            loadData()
            focusedNote = initFocusedNote()
            removeDeadNotes()

            // refresh tabs because the model data gets reset
            refreshTabs()

        }
    }

    fun moveNote(id: Int, newFolderId: Int) {
        val note = notes.find { note -> note.id == id }
        if (note != null) {
            note.folderId = newFolderId
            noteRepository.updateNote(note)
            setFocusedNoteById(note.id)
        }
    }

    fun moveFolder(id: Int, newParentId: Int) {
        val folder = folders.find { folder -> folder.id == id }
        if (folder != null) {
            folder.parentId = newParentId
            folderRepository.updateFolder(folder)
            val ctx = Context()
            ctx.isFolderMoved = true
            update(ctx)
        }
    }

    fun folderPathMap(parentId: Int = 1, map: MutableMap<Int, String> = hashMapOf(1 to "")): HashMap<Int, String> {
        folders.filter { it.parentId == parentId }.forEach {
            map[it.id] = map[parentId]!! + it.name + "/"
            folderPathMap(it.id, map)
        }
        return map as HashMap<Int, String>
    }

    fun closeNote(note: INote = focusedNote) {
        val index = openNotes.indexOfFirst { it.id == note.id }
        if (index < 0) return

        openNotes.removeIf { it.id == note.id }
        val ctx = Context()
        ctx.isNoteClosed = true
        ctx.closedNote = note
        update(ctx)
        if (focusedNote.id == note.id) {
            focusedNote = if (openNotes.isNotEmpty()) {
                val openNoteAtIndex = max(index - 1, 0)
                openNotes.elementAt(openNoteAtIndex)
            } else {
                initFocusedNote()
            }
        }
    }

    private fun refreshTabs() {
        // update openNotes with new notes
        val newOpenNotes = mutableListOf<INote>()
        for (openNote in openNotes) {
            newOpenNotes.add(notes.first { it.id == openNote.id })
        }
        openNotes.clear()
        openNotes.addAll(newOpenNotes)
        val ctx = Context()
        ctx.isTabReloadRequired = true
        update(ctx)
    }

    private fun removeDeadNotes() {
        val deadNotes = mutableListOf<Number>()
        for (openNote in openNotes) {
            // open note doesn't exist anymore
            if (notes.indexOfFirst { it.id == openNote.id } == -1) {
                deadNotes.add(openNote.id)
            }
        }

        // remove from openNotes
        for (deadNoteId in deadNotes) {
            val deadNote = openNotes.first { it.id == deadNoteId }
//            val index = openNotes.indexOfFirst { it.id == deadNoteId }
//            val deadNote = openNotes[index]
            openNotes.removeIf { it.id == deadNoteId }
            val ctx = Context()
            ctx.isNoteClosed = true
            ctx.closedNote = deadNote
            update(ctx)
        }
    }

    fun saveFolders() {
        folderRepository.saveFolders(folders)
    }

    fun openNextTab() {
        cycleTab(1)
    }

    fun openPreviousTab() {
        cycleTab(-1)
    }

    private fun cycleTab(shiftTabBy: Int) {
        val focussedTabIndex = openNotes.indexOfFirst { it.id == focusedNote.id }
        if (focussedTabIndex < 0) return

        val newTabIndex = (focussedTabIndex + shiftTabBy).mod(openNotes.size)
        focusedNote = openNotes.elementAt(newTabIndex)

    }

    private fun filterNotes() {
        when (navigationViewSelection) {
            NavigationViewSelection.SEARCH -> {
                filteredNotes.clear()
                if (searchTerm.isNotEmpty()) {
                    filteredNotes.addAll(
                        notes.filter {
                            it.title.contains(searchTerm, true) ||
                                    it.body.contains(searchTerm, true)
                        })
                }
            }
            NavigationViewSelection.CALENDAR -> {
                filteredNotes.clear()
                if (filteredDate != null) {
                    val dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
                    datesWithNotes.clear()
                    datesWithNotes.addAll(
                        notes.filter { it.taggedDate != null && it.taggedDate!!.isNotEmpty() }
                            .map { LocalDate.parse(it.taggedDate, dateFormatter) }
                            .distinct()
                    )
                    filteredNotes.addAll(
                        notes.filter {
                            it.taggedDate != null &&
                                    filteredDate == LocalDate.parse(it.taggedDate, dateFormatter)
                        })
                }
            }
            else -> {}
        }
    }
}