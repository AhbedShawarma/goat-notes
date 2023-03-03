package com.goat.app.persistence.models

data class Note(
        override var title: String = "",
        override var body: String = "",
        override var folderId: Int = 1,
        override var id: Int = 0,
        override var taggedDate: String? = null,
        override var dirty: Boolean = false) : INote {
}
