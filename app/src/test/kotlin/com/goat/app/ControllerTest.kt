package com.goat.app

import com.goat.app.business.Controller
import com.goat.app.business.sync.SyncClient
import com.goat.app.persistence.Model
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.time.LocalDate

internal class ControllerTest {
    private val sync = SyncClient()
    @Test
    fun createNoteTest() {
        val model = mock<Model>()
        val controller = Controller(model, sync)
        controller.createNote()
        verify(model).createNote() // checks if model.createNote() was called exactly once
    }

    @Test
    fun createFolderTest() {
        val model = mock<Model>()
        val controller = Controller(model, sync)
        controller.createFolder()
        verify(model).createFolder() // checks if model.createFolder() was called exactly once
    }

    @Test
    fun renameNoteTest() {
        val model = mock<Model>()
        val controller = Controller(model, sync)
        controller.renameNote(1, "newTitle")
        verify(model).renameNote(1, "newTitle")
    }

    @Test
    fun renameFolderTest() {
        val model = mock<Model>()
        val controller = Controller(model, sync)
        controller.renameFolder(2, "newName")
        verify(model).renameFolder(2, "newName")
    }

    @Test
    fun moveNoteTest() {
        val model = mock<Model>()
        val controller = Controller(model, sync)
        controller.moveNote(1, 2)
        verify(model).moveNote(1, 2)
    }

    @Test
    fun moveFolderTest() {
        val model = mock<Model>()
        val controller = Controller(model, sync)
        controller.moveFolder(2, 1)
        verify(model).moveFolder(2, 1)
    }

    @Test
    fun changeFocusedNoteDateTest() {
        val model = mock<Model>()
        val controller = Controller(model, sync)
        val now = LocalDate.now()
        controller.changeFocusedNoteDate(now)
        verify(model).changeFocusedNoteDate(now)
    }

    @Test
    fun deleteNoteTest() {
        val model = mock<Model>()
        val controller = Controller(model, sync)
        controller.deleteNoteById(2,)
        verify(model).deleteNoteById(2)
    }

    @Test
    fun deleteFolderTest() {
        val model = mock<Model>()
        val controller = Controller(model, sync)
        controller.deleteFolderById(2)
        verify(model).deleteFolderById(2)
    }

    @Test
    fun saveNotesTest() {
        val model = mock<Model>()
        val controller = Controller(model, sync)
        controller.saveNotes()
        verify(model).saveNotes()
    }

    @Test
    fun setFocusedNoteTitleTest() {
        val model = mock<Model>()
        val controller = Controller(model, sync)
        controller.setFocusedNoteTitle("test")
        verify(model).setFocusedNoteTitle("test")
    }

    @Test
    fun setFocusedNoteTextTest() {
        val model = mock<Model>()
        val controller = Controller(model, sync)
        controller.setFocusedNoteText("text")
        verify(model).setFocusedNoteText("text")
    }

    @Test
    fun setFocusedNoteByIdTest() {
        val model = mock<Model>()
        val controller = Controller(model, sync)
        controller.setFocusedNoteById(1)
        verify(model).setFocusedNoteById(1)
    }

    @Test
    fun search() {
        val model = mock<Model>()
        val controller = Controller(model, sync)
        val searchTerm = "test search"
        controller.search(searchTerm)
        verify(model).searchTerm = searchTerm
    }

    @Test
    fun folderPathMap() {
        val model = mock<Model>()
        val controller = Controller(model, sync)
        controller.folderPathMap()
        verify(model).folderPathMap(1)
    }

    @Test
    fun closeNote() {
        val model = mock<Model>()
        val controller = Controller(model, sync)
        controller.closeNote()
        verify(model).closeNote()
    }

    @Test
    fun openNextTab() {
        val model = mock<Model>()
        val controller = Controller(model, sync)
        controller.openNextTab()
        verify(model).openNextTab()
    }

    @Test
    fun openPreviousTab() {
        val model = mock<Model>()
        val controller = Controller(model, sync)
        controller.openPreviousTab()
        verify(model).openPreviousTab()
    }
}