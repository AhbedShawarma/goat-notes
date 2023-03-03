package com.example.syncservice.folder

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

interface FolderRepository: CrudRepository<Folder, Int> {
    @Query("select * from folders")
    fun getFolders(): List<Folder>
}
