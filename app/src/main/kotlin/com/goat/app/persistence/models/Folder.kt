package com.goat.app.persistence.models

data class Folder(
        override var id: Int = 0, // root folder always has id = 1
        override var parentId: Int = 0, // root folder always has parentId = 0
        override var name: String = "",
        override var subfolders: MutableList<IFolder> = mutableListOf(),
        override var notes: MutableList<INote> = mutableListOf()
): IFolder {
}