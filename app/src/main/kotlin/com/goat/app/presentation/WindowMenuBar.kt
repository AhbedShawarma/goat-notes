package com.goat.app.presentation

import com.goat.app.business.Controller
import com.goat.app.persistence.Model
import com.goat.app.persistence.NavigationViewSelection
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination

class WindowMenuBar(val model: Model, controller: Controller) : MenuBar(), IView {

    init {
        model.addView(this)

        val file = Menu("File")
        val newNote = MenuItem("New Note")
        val closeTab = MenuItem("Close Tab")
        val nextTab = MenuItem("Open Next Tab")
        val prevTab = MenuItem("Open Previous Tab")
        val pullNotes = MenuItem("Pull Remote Notes")
        val pushNotes = MenuItem("Push Remote Notes")
        file.items.addAll(newNote, closeTab, nextTab, prevTab, pullNotes, pushNotes)

        val edit = Menu("Edit")
        val undo = MenuItem("Undo Text Edit")
        val redo = MenuItem("Redo Text Edit")
        val bold = MenuItem("Bold")
        val italicize = MenuItem("Italicize")
        edit.items.addAll(undo, redo, SeparatorMenuItem(), bold, italicize)

        val view = Menu("View")
        val noteExplorer = MenuItem("Note Explorer")
        val search = MenuItem("Search Notes")
        val calendar = MenuItem("Calendar View")
        view.items.addAll(noteExplorer, search, calendar)

        menus.addAll(file, edit, view)

        newNote.setOnAction { controller.createNote() }
        newNote.accelerator = KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN)

        closeTab.setOnAction { controller.closeNote() }
        closeTab.accelerator = KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN)

        nextTab.setOnAction { controller.openNextTab() }
        nextTab.accelerator = KeyCodeCombination(KeyCode.TAB, KeyCombination.SHORTCUT_DOWN)

        prevTab.setOnAction { controller.openPreviousTab() }
        prevTab.accelerator = KeyCodeCombination(KeyCode.TAB, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN)

        undo.setOnAction { controller.undoNoteBody() }
        undo.accelerator = KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN)

        redo.setOnAction { controller.redoNoteBody() }
        redo.accelerator = KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN)

        bold.setOnAction { controller.boldText() }
        bold.accelerator = KeyCodeCombination(KeyCode.B, KeyCombination.SHORTCUT_DOWN)

        italicize.setOnAction { controller.italicizeText() }
        italicize.accelerator = KeyCodeCombination(KeyCode.I, KeyCombination.SHORTCUT_DOWN)

        noteExplorer.setOnAction { controller.setNavigationViewSelection(NavigationViewSelection.NOTES) }
        noteExplorer.accelerator = KeyCodeCombination(KeyCode.E, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN)

        search.setOnAction { controller.setNavigationViewSelection(NavigationViewSelection.SEARCH) }
        search.accelerator = KeyCodeCombination(KeyCode.F, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN)

        calendar.setOnAction { controller.setNavigationViewSelection(NavigationViewSelection.CALENDAR) }
        calendar.accelerator = KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN)

        pullNotes.setOnAction { controller.pullFromRemote() }
        pushNotes.setOnAction { controller.pushToRemote() }
    }
}