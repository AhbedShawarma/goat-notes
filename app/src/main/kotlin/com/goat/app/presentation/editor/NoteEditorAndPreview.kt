package com.goat.app.presentation.editor

import com.goat.app.business.Controller
import com.goat.app.persistence.Model
import com.goat.app.presentation.IView
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority

class NoteEditorAndPreview(val model: Model, val controller: Controller) : HBox(), IView {
    private val editor = TextEditor(model, controller)
    private val preview = NotePreview(model, controller)
    init {
        model.addView(this)

        children.addAll(editor, preview)
        setHgrow(preview, Priority.ALWAYS)
        setHgrow(editor, Priority.ALWAYS)
        // We set the preferred widths to be an arbitrary but equal number so that they scale the same
        editor.prefWidth = 10.0
        preview.prefWidth = 10.0
    }

    fun focusOnEditor() {
        editor.requestFocus()
    }
}