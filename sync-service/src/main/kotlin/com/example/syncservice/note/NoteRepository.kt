package com.example.syncservice.note

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

interface NoteRepository: CrudRepository<Note, Int> {
    @Query("select * from notes")
    fun getNotes(): List<Note>
}
