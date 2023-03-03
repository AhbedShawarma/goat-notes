package com.example.syncservice.folder

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("FOLDERS")
data class Folder(
        @Id val sync_id: String?,
        val id: Int,
        val parent_id: Int,
        val name: String,
)
