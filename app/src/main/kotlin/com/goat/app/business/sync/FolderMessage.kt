package com.goat.app.business.sync

import kotlinx.serialization.Serializable

@Serializable
data class FolderMessage(
    val id: Int,
    val parent_id: Int,
    val name: String,
    val sync_id: String? = null,
)
