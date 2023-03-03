package com.example.syncservice.note

import com.example.syncservice.note.Note
import com.example.syncservice.note.NoteRepository
import org.springframework.stereotype.Service

@Service
class NoteService(val db: NoteRepository) {
    fun getNotes(): List<Note> {
        return db.getNotes()
    }
    fun storeNote(note: Note) {
        db.save(note)
    }
    fun deleteAllNotes() {
       db.deleteAll()
    }
}
