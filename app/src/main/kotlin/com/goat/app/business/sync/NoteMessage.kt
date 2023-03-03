package com.goat.app.business.sync

import kotlinx.serialization.Serializable

@Serializable
data class NoteMessage(
    val id: Int,
    val title: String,
    val body: String,
    val folder_id: Int,
    val tagged_date: String?,
    val sync_id: String? = null,
)
