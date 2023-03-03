package com.example.syncservice.note

import org.springframework.web.bind.annotation.*


@RestController
class NoteResource(val noteService: NoteService) {
    @GetMapping(path= ["/notes"])
    fun getNotes(): List<Note> = noteService.getNotes()

    @PostMapping(path= ["/notes"])
    fun post(@RequestBody note: Note) = noteService.storeNote(note)

    @DeleteMapping(path= ["/notes"])
    fun delete() = noteService.deleteAllNotes()
}