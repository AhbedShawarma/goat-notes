package com.goat.app.presentation.navigation

import com.goat.app.business.Controller
import com.goat.app.persistence.Context
import com.goat.app.persistence.Model
import com.goat.app.presentation.IView
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView

class FilteredNotesList(val model: Model, val controller: Controller) : IView, TreeView<Any>() {

    init {
        model.addView(this)
        style = "-fx-background-color: #333333;"
        root = TreeItem()
        isShowRoot = false
        isEditable = true
        setCellFactory { NoteOrFolderTreeCell(model, controller) }
        TreeViewKeyHandler.setHandler(this, controller)

        onMouseMoved = EventHandler { event ->
            val right = boundsInParent.width
            if (event.x < right - 10) {
                cursor = Cursor.DEFAULT
            }
        }

        update()
    }

    override fun update(ctx: Context) {
        if (root != null) root.children.clear()
        model.filteredNotes.forEach{ root.children.add(TreeItem(it)) }
    }
}