package com.goat.app.persistence.interfaces

import com.goat.app.persistence.db.DBConnection
import com.goat.app.persistence.models.IFolder

interface IFolderRepository {
    // this function sets the connection
    fun setSQLConnection(connection: DBConnection)

    // fetch entire folder tree
    fun getFolderTree(): IFolder

    // fetch all folders
    fun getAllFolders(): List<IFolder>

    // fetch a specific folder and its contents
    fun getFolder(id: Int, recursive: Boolean): IFolder

    // create a new folder
    fun createFolder(folder: IFolder)

    // update folder
    fun updateFolder(newFolder: IFolder)

    // deletes a folder and all its contents
    fun deleteFolder(id: Int)
    fun deleteAllFolders()
    fun saveFolders(folders: List<IFolder>)
}