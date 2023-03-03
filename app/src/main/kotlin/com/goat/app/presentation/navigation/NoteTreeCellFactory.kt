package com.goat.app.presentation.navigation

import com.goat.app.business.Controller
import com.goat.app.persistence.Model
import com.goat.app.persistence.models.IFolder
import com.goat.app.persistence.models.INote
import javafx.event.EventHandler
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.*
import javafx.util.Callback

// Represents a cell factory to customize TreeCell behaviour
// Adapted from: https://github.com/cerebrosoft/treeview-dnd-example
class NoteTreeCellFactory(val model: Model, val controller: Controller): Callback<TreeView<Any>, TreeCell<Any>> {
    private var dropZone: TreeCell<Any>? = null
    private var draggedItem: TreeItem<Any>? = null
    private val defaultStyle = "-fx-background-color: #333333;-fx-text-fill: white"
    private val selectedStyle = "-fx-background-color: #595959;-fx-text-fill: white"

    override fun call(treeView: TreeView<Any>): TreeCell<Any> {
        val cell: TreeCell<Any> = NoteOrFolderTreeCell(model, controller)

        // drag and drop handlers
        cell.onDragDetected = EventHandler { event: MouseEvent? -> dragDetected(event!!, cell, treeView) }
        cell.onDragOver = EventHandler { event: DragEvent? -> dragOver(event!!, cell) }
        cell.onDragDropped = EventHandler { event: DragEvent? -> drop(event!!, cell) }
        cell.onDragDone = EventHandler { clearDropLocation() }
        return cell
    }

    private fun dragDetected(event: MouseEvent, treeCell: TreeCell<Any>, treeView: TreeView<Any>) {
        if (treeCell.treeItem == null) return
        draggedItem = treeCell.treeItem

        // handles edge case where the wrong dragged note is detected
        if (draggedItem!!.value is INote) {
            val selectedTreeItem = treeView.selectionModel.selectedItem
            if (selectedTreeItem != null && (draggedItem!!.value as INote).id != (selectedTreeItem.value as INote).id) {
                draggedItem = selectedTreeItem
            }
        }

        // root can't be dragged
        if (draggedItem?.parent == null) return
        val db = treeCell.startDragAndDrop(TransferMode.MOVE)
        val content = ClipboardContent()
        when (draggedItem!!.value) {
            is INote -> content[DATA_FORMAT] = (draggedItem!!.value as INote).title
            is IFolder -> content[DATA_FORMAT] = (draggedItem!!.value as IFolder).name
        }
        db.setContent(content)
        db.dragView = treeCell.snapshot(null, null)
        event.consume()
    }

    private fun dragOver(event: DragEvent, treeCell: TreeCell<Any>) {
        if (!event.dragboard.hasContent(DATA_FORMAT)) return
        val thisItem: TreeItem<Any>? = treeCell.treeItem

        // can't drop on itself
        if (draggedItem == null || thisItem == null || thisItem === draggedItem) return
        // ignore if this is the root
        if (draggedItem!!.parent == null) {
            clearDropLocation()
            return
        }
        event.acceptTransferModes(TransferMode.MOVE)
        if (dropZone != treeCell) {
            clearDropLocation()
            dropZone = treeCell
            dropZone!!.style = DROP_HINT_STYLE
        }
    }

    private fun drop(event: DragEvent, treeCell: TreeCell<Any>) {
        val db = event.dragboard
        val success = false
        if (!db.hasContent(DATA_FORMAT)) return
        val newLocation: TreeItem<Any> = treeCell.treeItem
        val newItemFolderId = when (newLocation.value) {
            is INote -> (newLocation.parent.value as IFolder).id
            else -> (newLocation.value as IFolder).id
        }

        // call controller to update note/folder
        when (draggedItem!!.value) {
            is INote -> {
                val id = (draggedItem!!.value as INote).id
                controller.moveNote(id, newItemFolderId)
            }
            is IFolder -> controller.moveFolder((draggedItem!!.value as IFolder).id, newItemFolderId)
        }

        event.isDropCompleted = success
    }

    private fun clearDropLocation() {
        if (dropZone != null) dropZone!!.style = defaultStyle
    }

    companion object {
        private val DATA_FORMAT = DataFormat.PLAIN_TEXT
        private const val DROP_HINT_STYLE = "-fx-background-color: #333333;-fx-text-fill: white; -fx-border-color: white; -fx-border-width: 0 0 2 0; -fx-padding: 3 3 1 3"
    }
}