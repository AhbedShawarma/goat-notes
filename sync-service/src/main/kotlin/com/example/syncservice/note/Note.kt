package com.example.syncservice.note

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("NOTES")
data class Note(
        @Id val sync_id: String?,
        val id: Int,
        val title: String,
        val body: String,
        val folder_id: Int,
        val tagged_date: String?,
)
