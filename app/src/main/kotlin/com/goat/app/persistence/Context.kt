package com.goat.app.persistence

import com.goat.app.persistence.models.INote
import com.goat.app.persistence.models.Note

class Context {
    var isFocusedNoteChanged = false
    var isNoteClosed = false
    var closedNote: INote = Note()
    var anyNoteRenamed = false
    var isFolderMoved = false
    var isTabReloadRequired = false
    var anyFolderRenamed = false

    // buttons
    var isBoldClicked = false
    var isItalicsClicked = false
    var isHeaderOneClicked = false
    var isHeaderTwoClicked = false
    var isHeaderThreeClicked = false
    var isInlineCodeClicked = false
    var isHyperlinkClicked = false
    var isRuleClicked = false
    var isUnorderedListClicked = false
    var isOrderedListClicked = false
    var isUndoPressed = false
    var isRedoPressed = false
}