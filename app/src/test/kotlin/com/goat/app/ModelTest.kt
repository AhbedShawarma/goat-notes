package com.goat.app

import com.goat.app.persistence.Model
import com.goat.app.persistence.NavigationViewSelection
import com.goat.app.persistence.db.DBConnection
import com.goat.app.persistence.models.Folder
import com.goat.app.persistence.repositories.NoteRepository
import com.goat.app.persistence.models.INote
import com.goat.app.persistence.models.Note
import com.goat.app.persistence.repositories.FolderRepository
import com.goat.app.presentation.utils.DateStringConverter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.time.LocalDate

internal class ModelTest {
    @Test
    fun setFocusedNoteTitleTest() {
        val db = mock<DBConnection>()
        val model = Model(db)
        model.setFocusedNoteTitle("test title")
        assertEquals(model.focusedNote.title, "test title")
        assert(model.focusedNote.dirty)
    }

    @Test
    fun setFocusedNoteTextTest() {
        val db = mock<DBConnection>()
        val model = Model(db)
        model.setFocusedNoteText("test text")
        assertEquals(model.focusedNote.body, "test text")
        assert(model.focusedNote.dirty)
    }

    @Test
    fun initFocusedNodeTest() {
        Mockito.mockConstruction(
            NoteRepository::class.java).use {
            val db = mock<DBConnection>()
            val model = Model(db)
            assertEquals(model.focusedNote.body, "")
            assertEquals(model.focusedNote.title, "New Note")
            verify(model.noteRepository).insertNote(model.focusedNote)
        }
    }

    @Test
    fun createNoteTest() {
        Mockito.mockConstruction(
            NoteRepository::class.java).use {
            val db = mock<DBConnection>()
            val model = Model(db)
            model.createNote()
            assertEquals(model.focusedNote.folderId, 1) // note is under root folder by default
            assertEquals(model.focusedNote.body, "")
            assertEquals(model.focusedNote.title, "New Note")
            model.createNote(2)
            assertEquals(model.focusedNote.folderId, 2)
            verify(model.noteRepository, times(1)).insertNote(model.focusedNote)
        }
    }

    @Test
    fun createFolderTest() {
        Mockito.mockConstruction(
                FolderRepository::class.java).use {
            val db = mock<DBConnection>()
            val model = Model(db)
            model.createFolder()
            model.createFolder(2)
            assertEquals(model.folders[0].parentId, 1)
            assertEquals(model.folders[1].parentId, 2)
            verify(model.folderRepository, times(1)).createFolder(model.folders[0])
            verify(model.folderRepository, times(1)).createFolder(model.folders[1])
        }
    }

    @Test
    fun renameNoteTest() {
        Mockito.mockConstruction(
                NoteRepository::class.java).use {
            val db = mock<DBConnection>()
            val model = Model(db)
            model.createNote()
            assertEquals(model.focusedNote.id, 0)
            model.renameNote(0, "New Title")
            val note = Note("New Title", "", folderId = 1, id = 0, dirty = true)
            verify(model.noteRepository).updateNote(note)
        }
    }

    @Test
    fun renameFolderTest() {
        Mockito.mockConstruction(
                FolderRepository::class.java).use {
            val db = mock<DBConnection>()
            val model = Model(db)
            model.createFolder()
            assertEquals(model.folders[0].id, 0)
            assertEquals(model.folders[0].name, "New Folder")
            model.renameFolder(0, "Renamed Folder")
            val renamedFolder = Folder(id = 0, name = "Renamed Folder", parentId = 1)
            verify(model.folderRepository).createFolder(renamedFolder)
        }
    }

    @Test
    fun changeFocusedNoteDateTest() {
        Mockito.mockConstruction(
                NoteRepository::class.java).use {
            val db = mock<DBConnection>()
            val model = Model(db)
            val now = LocalDate.now()
            model.changeFocusedNoteDate(now)
            assertEquals(model.focusedNote.taggedDate, DateStringConverter().toString(now))
            assert(model.focusedNote.dirty)
        }
    }

    @Test
    fun moveNoteTest() {
        Mockito.mockConstruction(
                NoteRepository::class.java).use {
            val db = mock<DBConnection>()
            val model = Model(db)
            model.createNote()
            assertEquals(model.focusedNote.id, 0)
            assertEquals(model.focusedNote.folderId, 1)

            // move note to a different folder
            model.moveNote(0, 2)
            val newNote = model.focusedNote
            newNote.folderId = 2
            verify(model.noteRepository).updateNote(newNote)
        }
    }

    @Test
    fun moveFolderTest() {
        Mockito.mockConstruction(
                FolderRepository::class.java).use {
            val db = mock<DBConnection>()
            val model = Model(db)
            model.createFolder(1)
            assertEquals(model.folders[0].id, 0)
            model.moveFolder(0, 2)
            val newFolder = model.folders[0]
            newFolder.parentId = 2
            verify(model.folderRepository).updateFolder(newFolder)
        }
    }

    @Test
    fun deleteFolderTest() {
        Mockito.mockConstruction(
                FolderRepository::class.java).use {
            val db = mock<DBConnection>()
            val model = Model(db)
            model.createFolder()
            assertEquals(model.folders[0].id, 0)
            model.createFolder(parent_id = 0) // subfolder

            // delete first folder
            model.deleteFolderById(0)
            assertEquals(model.folders.size, 0)
            verify(model.folderRepository).deleteFolder(0)
        }
    }

    @Test
    fun deleteRootFolderTest() {
        Mockito.mockConstruction(
                FolderRepository::class.java).use {
            val db = mock<DBConnection>()
            val model = Model(db)
            assertEquals(model.folders.size, 0)
            model.deleteFolderById(1) // try deleting root folder
            verify(model.folderRepository, times(0)).deleteFolder(1) // never called
        }
    }

    @Test
    // testing that the note that is created on initialization of model is saved
    fun saveNotesInitNoteTest() {
        Mockito.mockConstruction(
            NoteRepository::class.java).use {
            val db = mock<DBConnection>()
            val model = Model(db)
            val notes = mutableListOf<INote>()
            notes.add(model.focusedNote)
            model.saveNotes()
            verify(model.noteRepository).saveNotes(notes)
        }
    }

    @Test
    fun saveNotesManyDirtyTest() {
        Mockito.mockConstruction(
            NoteRepository::class.java).use {
            val db = mock<DBConnection>()
            val model = Model(db)
            val notes = mutableListOf<INote>()
            model.setFocusedNoteText("1")
            notes.add(model.focusedNote)
            model.createNote()
            model.setFocusedNoteTitle("2")
            notes.add(model.focusedNote)
            model.createNote()
            model.setFocusedNoteText("3")
            notes.add(model.focusedNote)
            model.createNote()
            notes.add(model.focusedNote)
            model.saveNotes()
            verify(model.noteRepository).saveNotes(notes)
        }
    }

    @Test
    // tests that if an invalid id is entered, the focused note  does not change
    fun setFocusedNoteByInvalidIdTest() {
        val db = mock<DBConnection>()
        val model = Model(db)
        val currFocused = model.focusedNote
        val note1 = Note(title = "1", id = 1)
        model.notes.add(note1)
        val note2 = Note(title = "2", id = 2)
        model.notes.add(note2)
        val note3 = Note(title = "3", id = 3)
        model.notes.add(note3)
        val note4 = Note(title = "4", id = 4)
        model.notes.add(note4)
        val note5 = Note(title = "5", id = 5)
        model.notes.add(note5)
        val note6 = Note(title = "6", id = 6)
        model.notes.add(note6)
        model.setFocusedNoteById(20)
        assertEquals(currFocused, model.focusedNote)
    }

    @Test
    fun setFocusedNoteByIdTest() {
        val db = mock<DBConnection>()
        val model = Model(db)
        val note1 = Note(title = "1", id = 1)
        model.notes.add(note1)
        val note2 = Note(title = "2", id = 2)
        model.notes.add(note2)
        val note3 = Note(title = "3", id = 3)
        model.notes.add(note3)
        val note4 = Note(title = "4", id = 4)
        model.notes.add(note4)
        val note5 = Note(title = "5", id = 5)
        model.notes.add(note5)
        val note6 = Note(title = "6", id = 6)
        model.notes.add(note6)
        model.setFocusedNoteById(4)
        assertEquals(note4, model.focusedNote)
    }

    @Test
    fun deleteNoteByIdTest() {
        Mockito.mockConstruction(
            NoteRepository::class.java).use {
                val db = mock<DBConnection>()
                val model = Model(db)
                val note1 = Note(title = "1", id = 1)
                model.notes.add(note1)
                val note2 = Note(title = "2", id = 2)
                model.notes.add(note2)
                val note3 = Note(title = "3", id = 3)
                model.notes.add(note3)
                val note4 = Note(title = "4", id = 4)
                model.notes.add(note4)
                val note5 = Note(title = "5", id = 5)
                model.notes.add(note5)
                val note6 = Note(title = "6", id = 6)
                model.notes.add(note6)
                val oldCount = model.notes.size
                model.setFocusedNoteById(4)
                model.deleteNoteById(4)
                assertEquals(null, model.notes.find { note -> note.id == 4 },
                        "Note was not removed from model.notes after deletion")
                assertEquals(oldCount - 1, model.notes.size,
                        "model.notes size did not decrease after deletion")
                assertTrue(model.focusedNote.id != 4,
                        "focused note did not change after it was deleted")
                verify(model.noteRepository, atLeastOnce()).deleteNote(note4)
        }
    }

    @Test
    fun deleteLastNoteCreatesNewNote() {
        Mockito.mockConstruction(
                NoteRepository::class.java).use {
            val db = mock<DBConnection>()
            val model = Model(db)
            val note1 = Note(title = "1", id = 1)
            model.notes.add(note1)
            model.deleteNoteById(1)
            assertEquals(1, model.notes.size,
                    "When only note is deleted, model.notes should only have one note")
            assertEquals(model.focusedNote.body, "",
                    "When only note is deleted, focused note text should be the default text")
            assertEquals(model.focusedNote.title, "New Note",
                    "When only note is deleted, focused note title should be the default title")
        }
    }

    @Test
    fun search() {
        val db = mock<DBConnection>()
        val model = Model(db)
        val note1 = Note(title = "1", id = 1)
        model.notes.add(note1)
        val note2 = Note(title = "2", id = 2)
        model.notes.add(note2)
        val note3 = Note(title = "3", id = 3)
        model.notes.add(note3)
        val note4 = Note(title = "4", id = 4)
        model.notes.add(note4)
        val note5 = Note(title = "5", id = 5)
        model.notes.add(note5)
        val note6 = Note(title = "4", id = 6)
        model.notes.add(note6)
        model.navigationViewSelection = NavigationViewSelection.SEARCH
        model.searchTerm = "4"
        assertEquals(2, model.filteredNotes.count())
        assertTrue(model.filteredNotes.all { it.id == 4 || it.id == 6 })
    }

    @Test
    fun emptySearch() {
        val db = mock<DBConnection>()
        val model = Model(db)
        val note1 = Note(title = "1", id = 1)
        model.notes.add(note1)
        val note2 = Note(title = "2", id = 2)
        model.notes.add(note2)
        val note3 = Note(title = "3", id = 3)
        model.notes.add(note3)
        val note4 = Note(title = "4", id = 4)
        model.notes.add(note4)
        val note5 = Note(title = "5", id = 5)
        model.notes.add(note5)
        val note6 = Note(title = "4", id = 6)
        model.notes.add(note6)
        model.navigationViewSelection = NavigationViewSelection.SEARCH
        model.searchTerm = ""
        assertEquals(0, model.filteredNotes.count())
    }

    @Test
    fun folderPathMap() {
        val db = mock<DBConnection>()
        val model = Model(db)
        val folder1 = Folder(id = 2, name = "2", parentId = 1)
        val folder2 = Folder(id = 3, name = "3", parentId = 2)
        val folder3 = Folder(id = 4, name = "4", parentId = 3)
        val folder4 = Folder(id = 5, name = "5", parentId = 3)
        val folder5 = Folder(id = 6, name = "6", parentId = 2)
        val folder6 = Folder(id = 7, name = "7", parentId = 6)
        model.folders.addAll(listOf(folder1, folder2, folder3, folder4, folder5, folder6))
        val expectedMap = hashMapOf(
                1 to "",
                2 to "2/",
                3 to "2/3/",
                4 to "2/3/4/",
                5 to "2/3/5/",
                6 to "2/6/",
                7 to "2/6/7/")
        val actualMap = model.folderPathMap()
        assertEquals(expectedMap, actualMap)
    }

    @Test
    fun openNotesUniqueById() {
        val db = mock<DBConnection>()
        val model = Model(db)
        assertEquals(1, model.openNotes.size, "model should init with 1 open note")
        val note1 = Note(title = "1", id = 1)
        model.notes.add(note1)
        model.setFocusedNoteById(note1.id)
        assertEquals(2, model.openNotes.size, "model should have added 1 open note")
        model.setFocusedNoteById(note1.id)
        assertEquals(2, model.openNotes.size, "model should have not opened another note")
    }

    @Test
    fun closeNote() {
        val db = mock<DBConnection>()
        val model = Model(db)
        assertEquals(1, model.openNotes.size, "model should init with 1 open note")
        val note1 = Note(title = "1", id = 1)
        model.notes.add(note1)
        model.setFocusedNoteById(note1.id)
        val note2 = Note(title = "2", id = 2)
        model.notes.add(note2)
        model.setFocusedNoteById(note2.id)
        assertEquals(3, model.openNotes.size, "model should have added 2 open notes")
        model.closeNote(note2)
        assertEquals(2, model.openNotes.size, "model should have removed 1 note from open notes")
        assertTrue(model.openNotes.none{ it.id == note2.id }, "note2 should have been removed")
    }

    @Test
    fun closeLastNote() {
        val db = mock<DBConnection>()
        val model = Model(db)
        assertEquals(1, model.openNotes.size, "model should init with 1 open note")
        model.closeNote(model.focusedNote)
        assertEquals(1, model.openNotes.size, "closing final note opens another one")
    }

    @Test
    fun nextTab() {
        val db = mock<DBConnection>()
        val model = Model(db)
        assertEquals(1, model.openNotes.size, "model should init with 1 open note")
        val note1 = Note(title = "1", id = 1)
        model.notes.add(note1)
        model.setFocusedNoteById(note1.id)
        val note2 = Note(title = "2", id = 2)
        model.notes.add(note2)
        model.setFocusedNoteById(note2.id)
        model.setFocusedNoteById(note1.id)
        assertEquals(3, model.openNotes.size, "model should have added 2 open notes")
        model.openNextTab()
        assertEquals(note2.id, model.focusedNote.id, "model should have selected the next note")
    }

    @Test
    fun nextTabOnLastTab() {
        val db = mock<DBConnection>()
        val model = Model(db)
        assertEquals(1, model.openNotes.size, "model should init with 1 open note")
        val note1 = Note(title = "1", id = 1)
        model.notes.add(note1)
        model.setFocusedNoteById(note1.id)
        val note2 = Note(title = "2", id = 2)
        model.notes.add(note2)
        model.setFocusedNoteById(note2.id)
        model.setFocusedNoteById(model.openNotes.last().id)
        assertEquals(3, model.openNotes.size, "model should have added 2 open notes")
        model.openNextTab()
        assertEquals(model.openNotes.first().id, model.focusedNote.id, "model should have selected the first note")
    }

    @Test
    fun previousTab() {
        val db = mock<DBConnection>()
        val model = Model(db)
        assertEquals(1, model.openNotes.size, "model should init with 1 open note")
        val note1 = Note(title = "1", id = 1)
        model.notes.add(note1)
        model.setFocusedNoteById(note1.id)
        val note2 = Note(title = "2", id = 2)
        model.notes.add(note2)
        model.setFocusedNoteById(note2.id)
        assertEquals(3, model.openNotes.size, "model should have added 2 open notes")
        model.openPreviousTab()
        assertEquals(note1.id, model.focusedNote.id, "model should have selected the previous note")
    }

    @Test
    fun previousTabOnFirstTab() {
        val db = mock<DBConnection>()
        val model = Model(db)
        assertEquals(1, model.openNotes.size, "model should init with 1 open note")
        val note1 = Note(title = "1", id = 1)
        model.notes.add(note1)
        model.setFocusedNoteById(note1.id)
        val note2 = Note(title = "2", id = 2)
        model.notes.add(note2)
        model.setFocusedNoteById(note2.id)
        model.setFocusedNoteById(model.openNotes.first().id)
        assertEquals(3, model.openNotes.size, "model should have added 2 open notes")
        model.openPreviousTab()
        assertEquals(model.openNotes.last().id, model.focusedNote.id, "model should have selected the last note")
    }
}