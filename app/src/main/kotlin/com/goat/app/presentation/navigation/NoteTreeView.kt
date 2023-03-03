package com.goat.app.presentation.navigation

import com.goat.app.business.Controller
import com.goat.app.persistence.Context
import com.goat.app.persistence.Model
import com.goat.app.persistence.models.Folder
import com.goat.app.persistence.models.IFolder
import com.goat.app.persistence.models.INote
import com.goat.app.presentation.IView
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView

class NoteTreeView(val model: Model, val controller: Controller) : IView, TreeView<Any>() {
    private var notes = mutableListOf<INote>()
    private var folders = mutableListOf<IFolder>()

    init {
        model.addView(this)
        isEditable = true
        cellFactory = NoteTreeCellFactory(model, controller)
        TreeViewKeyHandler.setHandler(this, controller)
        style = "-fx-background-color: #333333; -fx-font-size: 14"

        onMouseMoved = EventHandler { event ->
            val right = boundsInParent.width
            if (event.x < right - 10) {
                cursor = Cursor.DEFAULT
            }
        }

        update()
    }

    // constructs and returns a map of folder id -> list of notes
    private fun getFolderNotesMap(): MutableMap<Int, MutableList<INote>> {
        val folderNotesMap = mutableMapOf<Int, MutableList<INote>>()
        notes.forEach { note ->
            val folderId = note.folderId
            if (folderNotesMap.containsKey(folderId)) {
                folderNotesMap[folderId]?.add(note)
            }
            else {
                folderNotesMap[folderId] = mutableListOf(note)
            }
        }
        return folderNotesMap
    }

    override fun update(ctx: Context) {
        if (ctx.anyNoteRenamed || ctx.isFocusedNoteChanged || ctx.isFolderMoved ||
            ctx.anyFolderRenamed || notes != model.notes || folders != model.folders) {
            notes = model.notes.toMutableList()
            folders = model.folders.toMutableList()
            updateTree()
        }
    }

    private fun updateTree() {
        if (root == null) {
            root = TreeItem(Folder(id = 1, name = "All Notes"))
            root.isExpanded = true
        }
        root.children.clear()

        // construct maps used for constructing tree
        val folderNotes = getFolderNotesMap()
        val folderAdjList = mutableMapOf<Int, MutableList<Int>>()
        val folderMap = mutableMapOf<Int, IFolder>()

        for (folder in folders) {
            val id = folder.id
            val parentId = folder.parentId

            folderMap[folder.id] = folder

            // add folder to adj list
            if (folderAdjList.containsKey(parentId)) {
                folderAdjList[parentId]?.add(id)
            }
            else {
                folderAdjList[parentId] = mutableListOf(id)
            }
        }

        // use DFS to construct entire folder tree
        fun buildTreeNode(id: Int): TreeItem<Any> {
            val node: TreeItem<Any> = if (id == 1) root else TreeItem(folderMap[id])
            node.isExpanded = true

            // create subfolders
            val folderItems = folderAdjList[id]?.map { childId -> buildTreeNode(childId)}
            if (folderItems != null) {
                node.children.addAll(folderItems.sortedBy { (it.value as IFolder).name.toLowerCase() })
            }

            // add notes
            val noteItems = folderNotes[id]?.map<INote, TreeItem<Any>> { note -> TreeItem(note) }
            if (noteItems != null) {
                node.children.addAll(noteItems.sortedBy { (it.value as INote).title.toLowerCase() })
            }
            return node
        }
        buildTreeNode(1)
    }
}