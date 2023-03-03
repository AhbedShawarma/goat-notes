package com.goat.app.presentation

import com.goat.app.business.Controller
import com.goat.app.persistence.Context
import com.goat.app.persistence.Model
import com.goat.app.presentation.utils.DateStringConverter
import javafx.event.EventHandler
import javafx.scene.control.DatePicker

class NoteDatePicker(val model: Model, val controller: Controller): DatePicker(), IView {

    init {
        model.addView(this)

        // set string converter
        this.converter = DateStringConverter()

        // user cannot type into the text box
        this.isEditable = false

        this.onAction = EventHandler {
            val date = this.value
            val currentDateStr = model.focusedNote.taggedDate

            // only update if a new date is selected
            if (date != null && (currentDateStr == null || this.converter.fromString(currentDateStr) != date)) {
                controller.changeFocusedNoteDate(date)
            }

        }

    }

    override fun update(ctx: Context) {
        // render selected date
        if (model.focusedNote.taggedDate != null) {
            // need this condition to prevent exception
            if (this.converter is DateStringConverter) {
                this.value = this.converter.fromString(model.focusedNote.taggedDate)
            }
        }
        else {
            this.value = null
            this.promptText = "mm-dd-yyyy"
        }
    }

}