package com.goat.app.presentation.editor

import com.goat.app.business.Controller
import com.goat.app.persistence.Model
import com.goat.app.presentation.IView
import javafx.scene.layout.VBox
import javafx.scene.layout.Priority

class NoteEditorPane(val model: Model, controller: Controller) : VBox(), IView {
    private val tabBar = TabBar(model, controller)
    private val toolbar = NoteEditorToolbar(model, controller)
    private val title = TitleEditor(model, controller)
    private val notePane = NoteEditorAndPreview(model, controller)
    init {
        model.addView(this)
        setVgrow(notePane, Priority.ALWAYS)
        minWidth = 600.0
        children.addAll(tabBar, toolbar, title, notePane)
        style = "-fx-background-color: #d1d1d1;" // grey to illustrate zone for now
    }

    override fun requestFocus() {
        notePane.focusOnEditor()
    }
}