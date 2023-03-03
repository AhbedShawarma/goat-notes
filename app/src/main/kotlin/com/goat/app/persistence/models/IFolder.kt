package com.goat.app.persistence.models

interface IFolder {
    var id: Int
    var parentId: Int
    var name: String
    var subfolders: MutableList<IFolder>
    var notes: MutableList<INote>
}