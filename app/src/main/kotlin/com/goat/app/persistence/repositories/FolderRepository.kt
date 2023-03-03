package com.goat.app.persistence.repositories

import com.goat.app.persistence.db.DBConnection
import com.goat.app.persistence.interfaces.IFolderRepository
import com.goat.app.persistence.models.Folder
import com.goat.app.persistence.models.IFolder
import com.goat.app.persistence.models.INote
import com.goat.app.persistence.models.Note
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class FolderRepository: IFolderRepository {
    private lateinit var dbConnection: DBConnection

    override fun setSQLConnection(connection: DBConnection) {
        this.dbConnection = connection
    }

    // returns a mapping of folder id -> list of notes in the folder
    private fun getFolderNotesMap(): MutableMap<Int, MutableList<INote>> {
        val folderNotesMap = mutableMapOf<Int, MutableList<INote>>()
        try {
            val stmt: Statement? = dbConnection.connection?.createStatement()
            val resultSet: ResultSet? = stmt?.executeQuery("SELECT * FROM notes ORDER BY folder_id ASC")
            if (resultSet != null) {
                while (resultSet.next()) {
                    val id = resultSet.getInt("id")
                    val folderId = resultSet.getInt("folder_id") // if null, returns 0
                    val title = resultSet.getString("title")
                    val body = resultSet.getString("body")

                    // add note to map
                    val note = Note(title, body, folderId, id)
                    if (folderNotesMap.containsKey(folderId)) {
                        folderNotesMap[folderId]?.add(note)
                    }
                    else {
                        folderNotesMap[folderId] = mutableListOf(note)
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            println(e.message)
        }
        return folderNotesMap
    }

    override fun getFolderTree(): IFolder {
        val folderNotes = getFolderNotesMap()

        // maps folder id -> subfolder ids
        val folderAdjList = mutableMapOf<Int, MutableList<Int>>()

        // maps folder id -> folder object
        val folderMap = mutableMapOf<Int, Folder>()

        // fetch folders from table and construct data structures
        try {
            val stmt: Statement? = dbConnection.connection?.createStatement()
            val resultSet: ResultSet? = stmt?.executeQuery("SELECT * FROM folders ORDER BY parent_id ASC")
            if (resultSet != null) {
                while (resultSet.next()) {
                    val id = resultSet.getInt("id")
                    val parentId = resultSet.getInt("parent_id") // if null, returns 0
                    val name = resultSet.getString("name")

                    // create folder
                    val folder = Folder(id, parentId, name)
                    folderMap[id] = folder

                    // add folder to adj list
                    if (folderAdjList.containsKey(parentId)) {
                        folderAdjList[parentId]?.add(id)
                    }
                    else {
                        folderAdjList[parentId] = mutableListOf(id)
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            println(e.message)
        }

        // DFS to construct entire folder tree
        fun buildFolder(id: Int): IFolder {
            val folder = folderMap[id]!!
            folder.notes = folderNotes.getOrDefault(id, mutableListOf())
            folder.subfolders = mutableListOf()

            // recursively build subfolders
            if (folderAdjList.containsKey(id)) {
                for (child_id in folderAdjList[id]!!) {
                    folder.subfolders.add(buildFolder(child_id))
                }
            }
            return folder
        }
        return buildFolder(1) // root folder has id = 1
    }

    override fun getAllFolders(): List<IFolder> {
        val folders = mutableListOf<Folder>()
        try {
            val stmt: Statement? = dbConnection.connection?.createStatement()
            val resultSet: ResultSet? = stmt?.executeQuery("SELECT * FROM folders ORDER BY parent_id ASC")
            if (resultSet != null) {
                while (resultSet.next()) {
                    val id = resultSet.getInt("id")
                    val parentId = resultSet.getInt("parent_id") // if null, returns 0
                    val name = resultSet.getString("name")
                    folders.add(Folder(id, parentId, name))
//                    println("Folder {id: $id, parentId: $parentId, name: $name}")
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            println(e.message)
        }
        return folders
    }

    override fun getFolder(id: Int, recursive: Boolean): IFolder {
        TODO("Not yet implemented")
    }

    override fun createFolder(folder: IFolder) {
        try {
            val query = "INSERT INTO folders(name, parent_id, created_at) values(?, ?, datetime('now','localtime'))"
            val pstmt = dbConnection.connection?.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)
            pstmt?.setString(1, folder.name)
            // don't need to worry about parentId = 0 (root) case because we would never need to create a root folder again
            pstmt?.setInt(2, folder.parentId)
            pstmt?.executeUpdate()
            val resultSet = pstmt?.generatedKeys
            if (resultSet?.next() == true) {
                folder.id = resultSet.getInt(1)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            println(e.message)
        }
    }

    override fun updateFolder(newFolder: IFolder) {
        try {
            val query = "UPDATE folders set name = ?, parent_id = ?, updated_at = datetime('now','localtime') where id = ?"
            val pstmt = dbConnection.connection?.prepareStatement(query)
            pstmt?.setString(1, newFolder.name)
            // don't need to worry about parentId = 0 (root) case because we would never need to update the root folder
            pstmt?.setInt(2, newFolder.parentId)
            pstmt?.setInt(3, newFolder.id)
            pstmt?.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
            println(e.message)
        }
    }

    override fun saveFolders(folders: List<IFolder>) {
        try {
            val query = "INSERT INTO folders(name, parent_id, id) values(?, ?, ?)" +
                    "ON CONFLICT(id)" +
                    "DO UPDATE SET name = ?, parent_id = ?, updated_at = datetime('now','localtime')"
            val pstmt = dbConnection.connection?.prepareStatement(query)
            folders.forEach { folder ->
                pstmt?.setString(1, folder.name)
                // don't need to worry about parentId = 0 (root) case because we would never need to update the root folder
                pstmt?.setInt(2, folder.parentId)
                pstmt?.setInt(3, folder.id)
                pstmt?.setString(4, folder.name)
                pstmt?.setInt(5, folder.parentId)
                pstmt?.executeUpdate()
            }
            pstmt?.executeBatch()
        } catch (e: SQLException) {
            //e.printStackTrace()
            //println(e.message)
        }
    }

    override fun deleteFolder(id: Int) {
        try {
            val query = "DELETE FROM folders WHERE id = ?"
            val pstmt = dbConnection.connection?.prepareStatement(query)
            pstmt?.setInt(1, id)
            pstmt?.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
            println(e.message)
        }
    }

    override fun deleteAllFolders() {
        try {
            val query = "DELETE FROM folders"
            val pstmt = dbConnection.connection?.prepareStatement(query)
            pstmt?.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
            println(e.message)
        }
    }
}