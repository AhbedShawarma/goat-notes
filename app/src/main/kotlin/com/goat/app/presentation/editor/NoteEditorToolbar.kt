package com.goat.app.presentation.editor

import com.goat.app.business.Controller
import com.goat.app.persistence.Model
import com.goat.app.presentation.IView
import com.goat.app.presentation.NoteDatePicker
import com.goat.app.presentation.ToolbarButton
import com.goat.app.presentation.ToolbarButtonType
import javafx.scene.layout.HBox

class NoteEditorToolbar(val model: Model, controller: Controller) : HBox(), IView {
    init {
        model.addView(this)
        ToolbarButtonType.values().forEach { type ->
            children.add(ToolbarButton(type, model, controller))
        }
        style = "-fx-background-color: #DCDCDC;"
        val datePicker = NoteDatePicker(model, controller)
        datePicker.minWidth = 150.0
        children.add(datePicker)
    }

    override fun requestFocus() {
        parent.requestFocus()
    }
}