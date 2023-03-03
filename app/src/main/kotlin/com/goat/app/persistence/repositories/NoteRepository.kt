package com.goat.app.persistence.repositories

import com.goat.app.persistence.db.DBConnection
import com.goat.app.persistence.interfaces.INoteRepository
import com.goat.app.persistence.models.INote
import com.goat.app.persistence.models.Note
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class NoteRepository: INoteRepository {
    private lateinit var dbConnection: DBConnection

    override fun setSQLConnection(connection: DBConnection) {
        this.dbConnection = connection
    }

    override fun getAllNotes(): List<INote> {
        val notes = mutableListOf<Note>()
        try {
            val stmt: Statement? = dbConnection.connection?.createStatement()
            val resultSet: ResultSet? = stmt?.executeQuery("SELECT * FROM notes")
            if (resultSet != null) {
                while (resultSet.next()) {
                    val id = resultSet.getInt("id")
                    val folderId = resultSet.getInt("folder_id")
                    val title = resultSet.getString("title")
                    val body = resultSet.getString("body")
                    val taggedDate = resultSet.getString("tagged_date")
                    val updatedAt = resultSet.getString("updated_at")
                    notes.add(Note(title, body, folderId, id, taggedDate))
                    println("Note {id: $id, folderId: $folderId, title: $title, body: $body, tagged_date: $taggedDate, updated_at: $updatedAt}")
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            println(e.message)
        }
        return notes
    }

    override fun getNote(id: Int): INote {
        TODO("Not yet implemented")
    }

    override fun insertNote(note: INote) {
        try {
            val query = "INSERT INTO notes(title, body) values(?, ?)"
            val pstmt = dbConnection.connection?.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)
            pstmt?.setString(1, note.title)
            pstmt?.setString(2, note.body)
            pstmt?.executeUpdate()
            val resultSet = pstmt?.generatedKeys
            if (resultSet?.next() == true) {
                note.id = resultSet.getInt(1)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            println(e.message)
        }
    }

    override fun updateNote(note: INote) {
        try {
            val query = "UPDATE notes set title = ?, body = ?, folder_id = ?, tagged_date = ?, updated_at = datetime('now','localtime') where id = ?"
            val pstmt = dbConnection.connection?.prepareStatement(query)
            pstmt?.setString(1, note.title)
            pstmt?.setString(2, note.body)
            pstmt?.setInt(3, note.folderId)
            pstmt?.setString(4, note.taggedDate)
            pstmt?.setInt(5, note.id)
            pstmt?.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
            println(e.message)
        }
    }

    override fun saveNotes(notes: List<INote>) {
        try {
            val query = "INSERT INTO notes(title, body, tagged_date, folder_id, id) values(?, ?, ?, ?, ?)" +
                    "ON CONFLICT(id)" +
                    "DO UPDATE SET title = ?, body = ?, tagged_date = ?, folder_id = ?, updated_at = datetime('now','localtime')"
            val pstmt = dbConnection.connection?.prepareStatement(query)
            notes.forEach { note ->
                pstmt?.setString(1, note.title)
                pstmt?.setString(2, note.body)
                pstmt?.setString(3, note.taggedDate)
                pstmt?.setInt(4, note.folderId)
                pstmt?.setInt(5, note.id)
                pstmt?.setString(6, note.title)
                pstmt?.setString(7, note.body)
                pstmt?.setString(8, note.taggedDate)
                pstmt?.setInt(9, note.folderId)
                pstmt?.addBatch()
            }
            pstmt?.executeBatch()
        } catch (e: SQLException) {
//            e.printStackTrace()
//            println(e.message)
        }
    }

    override fun deleteNote(note: INote) {
        try {
            val query = "DELETE FROM notes WHERE id == ?"
            val pstmt = dbConnection.connection?.prepareStatement(query)
            pstmt?.setInt(1, note.id)
            pstmt?.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
            println(e.message)
        }
    }

    override fun deleteAllNotes() {
        try {
            val query = "DELETE FROM notes"
            val pstmt = dbConnection.connection?.prepareStatement(query)
            pstmt?.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
            println(e.message)
        }
    }
}