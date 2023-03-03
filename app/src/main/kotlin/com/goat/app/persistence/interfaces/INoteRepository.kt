package com.goat.app.persistence.interfaces

import com.goat.app.persistence.db.DBConnection
import com.goat.app.persistence.models.INote

interface INoteRepository {
    // this function sets the connection
    fun setSQLConnection(connection: DBConnection)

    // fetch all notes
    fun getAllNotes(): List<INote>

    // fetch a specific note
    fun getNote(id: Int): INote

    // inserts a new note so we can keep the id
    fun insertNote(note: INote)

    // update a single note
    // can be used for updating a note's folder
    fun updateNote(note: INote)

    // update multiple notes at once
    fun saveNotes(notes: List<INote>)

    // delete a note
    fun deleteNote(note: INote)

    // delete all notes
    fun deleteAllNotes()
}