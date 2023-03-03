package com.goat.app.persistence.db

import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement

class DBHelper(private val connection: Connection) {

    init {
        populateTables()
    }

    private fun populateTables() {
        try {
            val stmt: Statement = connection.createStatement()

            // insert data if folders table is empty
            stmt.executeUpdate("INSERT INTO folders (name, parent_id)\n" +
                    "SELECT t.name, t.parent_id\n" +
                    "FROM (\n" +
                    "SELECT 'root' as name, NULL as parent_id UNION ALL\n" +
                    "SELECT 'folder1' as name, 1 as parent_id UNION ALL\n" +
                    "SELECT 'folder2' as name, 1 as parent_id UNION ALL\n" +
                    "SELECT 'folder3' as name, 2 as parent_id\n" +
                    ") t\n" +
                    "WHERE NOT EXISTS (SELECT 1 FROM folders)")

            // insert data if notes table is empty
            stmt.executeUpdate("INSERT INTO notes (title, body, folder_id)\n" +
                    "SELECT t.title, t.body, t.folder_id\n" +
                    "FROM (\n" +
                    "SELECT 'Title 1' as title, 'Sample body 1' as body, 1 as folder_id UNION ALL\n" +
                    "SELECT 'Title 2' as title, 'Sample body 2' as body, 1 as folder_id UNION ALL\n" +
                    "SELECT 'Title 3' as title, 'Sample body 3' as body, 1 as folder_id UNION ALL \n" +
                    "SELECT 'Nested Note 1' as title, 'Sample body 4' as body, 2 as folder_id UNION ALL \n" +
                    "SELECT 'Nested Note 2' as title, 'Sample body 5' as body, 2 as folder_id UNION ALL \n" +
                    "SELECT 'Nested Note 3' as title, 'Sample body 6' as body, 3 as folder_id\n" +
                    ") t\n" +
                    "WHERE NOT EXISTS (SELECT 1 FROM notes)")
            println("Data inserted.")
        } catch (e: SQLException) {
            e.printStackTrace()
            println(e.message)
        }
    }
}