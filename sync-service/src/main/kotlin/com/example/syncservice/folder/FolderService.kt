package com.example.syncservice.folder

import org.springframework.stereotype.Service

@Service
class FolderService(val db: FolderRepository) {
    fun getFolders(): List<Folder> {
        return db.getFolders()
    }
    fun storeFolder(folder: Folder) {
        db.save(folder)
    }
    fun deleteAllFolders() {
        db.deleteAll()
    }
}
