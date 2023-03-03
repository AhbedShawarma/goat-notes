package com.goat.app.persistence.db

import java.nio.file.Path
import java.sql.*
import java.sql.Connection
import org.sqlite.SQLiteConfig;

class DBConnection {
    var connection: Connection? = null

    init {
        connect()
//        reset()
        createTables()
    }

    private fun connect() {
        try {
            val config = SQLiteConfig()
            config.enforceForeignKeys(true)
            val path = Path.of("").toAbsolutePath().toString() + "/resources/goatnotes.db"
            val url = "jdbc:sqlite:$path"
            connection = DriverManager.getConnection(url, config.toProperties())
            println("Database connection established.")
        } catch (e: SQLException) {
            println(e.message)
        }
    }


    fun disconnect() {
        try {
            if (connection != null) {
                connection!!.close()
                println("Database connection closed.")
            }
        } catch (ex: SQLException) {
            println(ex.message)
        }
    }

    private fun createTables() {
        val createFoldersQuery = "CREATE TABLE IF NOT EXISTS folders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "parent_id INTEGER," +
                "name TEXT, " +
                "created_at DATE DEFAULT (datetime('now','localtime'))," +
                "updated_at DATE DEFAULT (datetime('now','localtime'))," +
                "FOREIGN KEY (parent_id) REFERENCES folders (id) ON DELETE CASCADE" +
                ")"
        val createNotesQuery = "CREATE TABLE IF NOT EXISTS notes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "folder_id INTEGER DEFAULT 1, " +
                "title TEXT, " +
                "body TEXT, " +
                "tagged_date DATE, " +
                "created_at DATE DEFAULT (datetime('now','localtime')), " +
                "updated_at DATE DEFAULT (datetime('now','localtime')), " +
                "FOREIGN KEY (folder_id) REFERENCES folders (id) ON DELETE CASCADE" +
                ")"
        try {
            val stmt: Statement = connection!!.createStatement()
            println("Creating folders table")
            stmt.executeUpdate(createFoldersQuery)
            println("Creating notes table")
            stmt.executeUpdate(createNotesQuery)
            println("Tables created.")
        } catch (e: SQLException) {
            e.printStackTrace()
            println(e.message)
        }
    }

    private fun reset() {
        try {
            val stmt: Statement = connection!!.createStatement()
            stmt.executeUpdate("DROP TABLE IF EXISTS notes")
            stmt.executeUpdate("DROP TABLE IF EXISTS folders")
            println("Tables dropped.")
        } catch (e: SQLException) {
            e.printStackTrace()
            println(e.message)
        }
    }
}

