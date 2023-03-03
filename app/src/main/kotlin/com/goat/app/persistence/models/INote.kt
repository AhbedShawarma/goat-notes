package com.goat.app.persistence.models

interface INote {
    var title: String
    var body: String
    var folderId: Int
    var id: Int
    var taggedDate: String?
    var dirty: Boolean
}