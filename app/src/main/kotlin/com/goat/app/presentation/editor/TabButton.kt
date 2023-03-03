package com.goat.app.presentation.editor

import com.goat.app.business.Controller
import com.goat.app.persistence.models.INote
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.input.MouseButton

class TabButton(val note: INote, controller: Controller) : Button() {
    private var selected = false
    private val selectedStyle = "-fx-background-color: #FFFFFF;"
    private val unSelectedStyle = "-fx-background-color: #CCCCCC;"

    init {
        setOnAction { controller.setFocusedNoteById(note.id) }
        setOnMouseClicked { evt ->
            if (evt.button == MouseButton.MIDDLE) controller.closeNote(note)
            else if (evt.button == MouseButton.SECONDARY) {
                val menu = ContextMenu()
                val closeNoteItem = MenuItem("Close Tab")
                closeNoteItem.setOnAction { controller.closeNote(note) }
                menu.items.add(closeNoteItem)
                contextMenu = menu
            }
            evt.consume()
        }
        updateTitle()
        setStyleUnselected()

        onMouseEntered = EventHandler {
            if (!selected) {
                style = "-fx-background-color:#E5E5E5;"
            }
        }
        onMouseExited = EventHandler {
            if (selected) {
                setStyleSelected()
            } else {
                setStyleUnselected()
            }
        }
    }

    fun updateTitle() {
        text = note.title
    }

    fun setStyleSelected() {
        selected = true
        style = selectedStyle
    }

    fun setStyleUnselected() {
        selected = false
        style = unSelectedStyle
    }
}