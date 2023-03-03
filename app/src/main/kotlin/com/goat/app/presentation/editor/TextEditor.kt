package com.goat.app.presentation.editor

import com.goat.app.business.Controller
import com.goat.app.persistence.Context
import com.goat.app.persistence.Model
import com.goat.app.presentation.IView
import javafx.scene.control.TextArea
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent


class TextEditor(val model: Model, val controller: Controller) : TextArea(), IView {

    init {
        model.addView(this)
        isWrapText = true

        text = model.focusedNote.body
        textProperty().addListener { _, _, newValue ->
            controller.setFocusedNoteText(newValue)
        }
        addHotKeys()
    }

    private fun addHotKeys() {

        addEventFilter(KeyEvent.KEY_PRESSED) { evt ->
            if (evt.code == KeyCode.ENTER) {
                // handle enter presses for lists
                // this event occurs before the newline is placed
                val pos = caretPosition
                val appendStr = "\n" + onListEnterPressed(pos, text)
                insertText(pos, appendStr)
                positionCaret(pos + appendStr.length)
                evt.consume()
            }
        }
    }



    override fun update(ctx: Context) {
        if (ctx.isBoldClicked) {
            val replace = TextEditorFormatter.boldText(
                selection.start, selection.end, text)
            replaceText(replace.start, replace.end, replace.text)
            positionCaret(replace.caretPos)
        }
        else if (ctx.isItalicsClicked) {
            val replace = TextEditorFormatter.italicizeText(
                selection.start, selection.end, text)
            replaceText(replace.start, replace.end, replace.text)
            positionCaret(replace.caretPos)
        }
        else if (ctx.isHeaderOneClicked) {
            val replace = TextEditorFormatter.headerOneText(
                selection.start, selection.end, text)
            replaceText(replace.start, replace.end, replace.text)
            positionCaret(replace.caretPos)
        }
        else if (ctx.isHeaderTwoClicked) {
            val replace = TextEditorFormatter.headerTwoText(
                selection.start, selection.end, text)
            replaceText(replace.start, replace.end, replace.text)
            positionCaret(replace.caretPos)
        }
        else if (ctx.isHeaderThreeClicked) {
            val replace = TextEditorFormatter.headerThreeText(
                selection.start, selection.end, text)
            replaceText(replace.start, replace.end, replace.text)
            positionCaret(replace.caretPos)
        }
        else if (ctx.isInlineCodeClicked) {
            val replace = TextEditorFormatter.inlineCodeText(
                selection.start, selection.end, text)
            replaceText(replace.start, replace.end, replace.text)
            positionCaret(replace.caretPos)
        }
        else if (ctx.isHyperlinkClicked) {
            val replace = TextEditorFormatter.hyperlinkText(
                selection.start, selection.end, text)
            replaceText(replace.start, replace.end, replace.text)
            positionCaret(replace.caretPos)
        }
        else if (ctx.isRuleClicked) {
            val oldCaretPos = caretPosition
            val replace = TextEditorFormatter.ruleInsert(
                selection.start, selection.end, text)
            replaceText(replace.start, replace.end, replace.text)
            positionCaret(replace.caretPos)
        }
        else if (ctx.isUnorderedListClicked) {
            val replace = TextEditorFormatter.unorderedListText(
                selection.start, selection.end, text)
            replaceText(replace.start, replace.end, replace.text)
            positionCaret(replace.caretPos)
        }
        else if (ctx.isOrderedListClicked) {
            val replace = TextEditorFormatter.orderedListText(
                selection.start, selection.end, text)
            replaceText(replace.start, replace.end, replace.text)
            positionCaret(replace.caretPos)
        }
        else if (ctx.isUndoPressed) {
            undoTextEditor()
        }
        else if (ctx.isRedoPressed) {
            redo()
        }
        else if (ctx.isFocusedNoteChanged) {
            text = model.focusedNote.body
        }
    }

    private fun undoTextEditor() {
        undo()
        deselect() // since we replace the entire text area, deselect everything
    }

    private fun onListEnterPressed(caretPos: Int, str: String): String {
        // pos is set to 1 before the newline character
        var pos = caretPos - 1
        var endPos = caretPos
        val textLen = str.length

        // return value, the string to add after the newline character was entered
        var appendStr = ""

        // go to the beginning of the line before the newline was entered
        // the function looks at the previous line and mimics its
        while (pos != 0 && str[pos - 1] != '\n') {
            pos--
        }

        // append whitespace of previous line
        while (pos != textLen && str[pos] != '\n' && (str[pos] == ' ' || str[pos] == '\t')) {
            if (str[pos] == ' ') appendStr += " "
            else if (str[pos] == '\t') appendStr += "\t"
            endPos++
            pos++
        }

        // if the first char after the whitespace is a -, then the newline should be an unordered list
        if (str[pos] == '-') {
            appendStr += "- "
        }
        // if the first char is a digit, check if it is an ordered list, if it is then the newline should start with
        // the next digit in the list
        else if (str[pos].isDigit()) {
            var digString = ""
            while (pos != textLen && str[pos].isDigit()) {
                digString += str[pos]
                pos++
            }
            if (str[pos] == '.') {
                val digit = digString.toInt() + 1
                appendStr += digit
                appendStr += ". "
            }
        }
        return appendStr
    }
}