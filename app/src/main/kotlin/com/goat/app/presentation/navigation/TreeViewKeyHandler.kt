package com.goat.app.presentation.navigation

import com.goat.app.business.Controller
import com.goat.app.persistence.models.IFolder
import com.goat.app.persistence.models.INote
import javafx.event.EventHandler
import javafx.scene.control.TreeView
import javafx.scene.input.KeyCode

class TreeViewKeyHandler {
    companion object {
        fun setHandler(treeView: TreeView<Any>, controller: Controller) {
            treeView.onKeyPressed = EventHandler { t ->
                val item = treeView.selectionModel.selectedItem
                if (item != null) {
                    val value = item.value
                    if (t.code == KeyCode.DELETE) {
                        when (value) {
                            is INote -> {
                                controller.deleteNoteById(value.id)
                            }
                            is IFolder -> {
                                controller.deleteFolderById(value.id)
                            }
                        }
                    }
                }
            }
        }
    }
}