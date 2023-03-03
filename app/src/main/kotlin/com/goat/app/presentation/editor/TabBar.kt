package com.goat.app.presentation.editor

import com.goat.app.business.Controller
import com.goat.app.persistence.Context
import com.goat.app.persistence.Model
import com.goat.app.persistence.models.INote
import com.goat.app.presentation.IView
import javafx.scene.layout.HBox

class TabBar(val model: Model, val controller: Controller) : HBox(), IView {
    init {
        model.addView(this)
        model.openNotes.forEach { createTab(it) }
    }

    fun refresh() {
        children.clear()
        model.openNotes.forEach { createTab(it) }
    }

    private fun createTab(note: INote) {
        val newTab = TabButton(note, controller)
        setTabStyling(newTab)
        children.add(newTab)
    }

    private fun setTabStyling(tab: TabButton) {
        if (model.focusedNote.id == tab.note.id) tab.setStyleSelected()
        else tab.setStyleUnselected()
    }

    override fun update(ctx: Context) {
        if (ctx.isFocusedNoteChanged) {
            if (children.none{(it as TabButton).note.id == model.focusedNote.id}) {
                createTab(model.focusedNote)
            }
            children.forEach {
                val tab = (it as TabButton)
                setTabStyling(tab)
            }
        }
        if (ctx.isNoteClosed) {
            children.removeIf{(it as TabButton).note.id == ctx.closedNote.id}
        }
        if (ctx.anyNoteRenamed) {
            children.forEach { (it as TabButton).updateTitle() }
        }
        if (ctx.isTabReloadRequired) {
            refresh()
        }
    }
}