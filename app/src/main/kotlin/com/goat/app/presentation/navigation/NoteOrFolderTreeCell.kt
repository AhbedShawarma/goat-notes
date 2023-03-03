package com.goat.app.presentation.navigation

import com.goat.app.business.Controller
import com.goat.app.persistence.Model
import com.goat.app.persistence.NavigationViewSelection
import com.goat.app.persistence.models.IFolder
import com.goat.app.persistence.models.INote
import javafx.beans.value.ChangeListener
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import java.io.File
import java.io.InputStream
import java.nio.file.Path


// Represents a single TreeCell in the NoteTreeView, either an INote or IFolder
// Adapted from: https://docs.oracle.com/javafx/2/ui_controls/tree-view.htm
class NoteOrFolderTreeCell(val model: Model, val controller: Controller): TreeCell<Any>() {

    private lateinit var textField: TextField
    private var menu = ContextMenu()
    private var selectedListener: ChangeListener<Boolean>? = null
    private var hoverListener: ChangeListener<Boolean>? = null
    private var foldersMap = HashMap<Int, String>()

    private val defaultStyle = "-fx-background-color: #333333;-fx-text-fill: white"
    private val hoverStyle = "-fx-background-color: #404040;-fx-text-fill: white"
    private val selectedStyle = "-fx-background-color: #595959;-fx-text-fill: white"

    private val folderIconPath: InputStream = File(Path.of("").toAbsolutePath().toString() + "/resources/images/folder-icon.png").inputStream()
    private val folderIcon = ImageView(Image(folderIconPath))

    init {
        if (model.navigationViewSelection != NavigationViewSelection.NOTES) foldersMap = controller.folderPathMap()
        createTextField()
        createContextMenu()
        style = defaultStyle

        selectedProperty().addListener { _, _, newValue ->
            style = if (newValue) {
                selectedStyle
            } else {
                defaultStyle
            }
        }

        folderIcon.fitWidth = 16.0
        folderIcon.fitHeight = 16.0
        folderIcon.isPreserveRatio = true
    }

    private fun removeListener() {
        if (selectedListener != null) {
            selectedProperty().removeListener(selectedListener)
            selectedListener = null
        }
        if (hoverListener != null) {
            hoverProperty().removeListener(hoverListener)
            hoverListener = null
        }
        style = defaultStyle
    }

    private fun createContextMenu() {

        val deleteItem = MenuItem("Delete")
        deleteItem.setOnAction {
            when (item) {
                is INote -> controller.deleteNoteById((item as INote).id)
                is IFolder -> controller.deleteFolderById((item as IFolder).id)
            }
        }

        if (model.navigationViewSelection == NavigationViewSelection.NOTES) {
            val createNoteItem = MenuItem("New Note")
            createNoteItem.setOnAction {
                when (item) {
                    is INote -> {
                        controller.createNote((item as INote).folderId)
                    }
                    is IFolder -> controller.createNote((item as IFolder).id)
                }
            }
            val createFolderItem = MenuItem("New Folder")
            createFolderItem.setOnAction {
                when (item) {
                    is INote -> controller.createFolder((item as INote).folderId)
                    is IFolder -> controller.createFolder((item as IFolder).id)
                }
            }
            menu.items.addAll(createNoteItem, createFolderItem)
        }

        menu.items.addAll(deleteItem)
    }

    override fun startEdit() {
        super.startEdit()

        text = null
        textField.text = getTitle()
        graphic = textField
        textField.selectAll()
        textField.requestFocus()
    }

    private fun createTextField() {
        textField = TextField(getTitle())
        textField.onKeyPressed = EventHandler { t ->
            if (t.code === KeyCode.ENTER) {
                commitEdit(textField.text)
                t.consume()
            } else if (t.code === KeyCode.ESCAPE) {
                cancelEdit()
            }
        }
    }

    override fun commitEdit(newValue: Any?) {
        when (item) {
            is INote -> {
                controller.renameNote((item as INote).id, newValue as String)
            }
            is IFolder -> {
                controller.renameFolder((item as IFolder).id, newValue as String)
            }
        }
        cancelEdit()
    }

    override fun cancelEdit() {
        super.cancelEdit()
        text = getLabel()
        graphic = treeItem.graphic
    }

    override fun updateItem(item: Any?, empty: Boolean) {
        super.updateItem(item, empty)
        if (empty) {
            // cell is either hidden or removed
            text = null
            graphic = null
            removeListener()
        } else {
            if (hoverListener == null) {
                hoverListener = ChangeListener<Boolean> { _, _, newValue ->
                    if (!isSelected) {
                        style = if (newValue) {
                            hoverStyle
                        } else {
                            defaultStyle
                        }
                    }
                }
                hoverProperty().addListener(hoverListener)
            }
            if (isEditing) {
                textField.text = getTitle()
                text = null
                graphic = textField
            } else {
                when (item) {
                    is INote -> {
                        val selectedItem = treeView.selectionModel.selectedItem

                        // this condition prevents a note from being re-selected when a folder is clicked
                        val notSelectingAFolder = selectedItem == null || selectedItem == treeView.root || selectedItem.value !is IFolder

                        // programatically select the cell that matches focusedNote
                        if (!isSelected && model.focusedNote.id == item.id && notSelectingAFolder) {
                            treeView.selectionModel.select(index)
                        }

                        // initialize listener for the isSelected property to focus the note when it's clicked
                        if (selectedListener == null) {
                            selectedListener = ChangeListener<Boolean> { _, oldValue, newValue ->
                                if (newValue && !oldValue) {
                                    controller.setFocusedNoteById(item.id)
                                }
                            }
                            selectedProperty().addListener(selectedListener)
                        }
                    }
                }
                text = getLabel()

                graphic = if (item is IFolder) {
                    folderIcon
                } else {
                    treeItem.graphic
                }
                contextMenu = menu
            }
        }
    }

    private fun getTitle(): String {
        return when (item) {
            is INote -> (item as INote).title
            is IFolder -> (item as IFolder).name
            else -> ""
        }
    }

    private fun getLabel(): String {
        var titleAndLabel = getTitle()
        if (model.navigationViewSelection != NavigationViewSelection.NOTES) {
            val note = item as INote
            titleAndLabel += "\n>${foldersMap[note.folderId]}${note.title}"
        }
        return titleAndLabel
    }
}