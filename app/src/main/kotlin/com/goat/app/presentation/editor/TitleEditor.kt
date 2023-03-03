package com.goat.app.presentation.editor

import com.goat.app.business.Controller
import com.goat.app.persistence.Context
import com.goat.app.persistence.Model
import com.goat.app.presentation.IView
import javafx.scene.control.TextField
import javafx.scene.text.Font

class TitleEditor(val model: Model, controller: Controller) : TextField(), IView {

    init {
        model.addView(this)
        font = Font(30.0)
        text = model.focusedNote.title
        textProperty().addListener { _, _, newValue ->
            controller.setFocusedNoteTitle(newValue)
        }
    }

    override fun update(ctx: Context) {
        if (ctx.isFocusedNoteChanged) {
            text = model.focusedNote.title
        }
    }
}