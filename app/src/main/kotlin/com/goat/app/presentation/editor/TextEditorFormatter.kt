package com.goat.app.presentation.editor

data class TextEditorReplace(
    val text: String,
    val start: Int,
    val end: Int,
    // by default, place caret at the end of the replaced text
    val caretPos: Int = text.length - (start - end) + end,
)

object TextEditorFormatter {
    // Functions that return replacement text start and end index of original text to replace

    fun boldText(startPos: Int, endPos: Int, text: String): TextEditorReplace {
        val replaceText = StringBuilder(text.substring(startPos, endPos))
            .insert(0, "**")
            .append("**").toString()
        return TextEditorReplace(replaceText, startPos, endPos, endPos + 2)
    }

    fun italicizeText(startPos: Int, endPos: Int, text: String): TextEditorReplace {
        val replaceText = StringBuilder(text.substring(startPos, endPos))
            .insert(0, "*")
            .append("*").toString()
        return TextEditorReplace(replaceText, startPos, endPos, endPos + 1)
    }

    fun inlineCodeText(startPos: Int, endPos: Int, text: String): TextEditorReplace {
        val replaceText = StringBuilder(text.substring(startPos, endPos))
            .insert(0, "`")
            .append("`").toString()
        return TextEditorReplace(replaceText, startPos, endPos, endPos + 1)
    }

    fun hyperlinkText(startPos: Int, endPos: Int, text: String): TextEditorReplace {
        val replaceText = StringBuilder(text.substring(startPos, endPos))
            .insert(0, "[")
            .append("]()").toString()
        return TextEditorReplace(replaceText, startPos, endPos, endPos + 3)
    }

    fun headerOneText(startPos: Int, endPos: Int, text: String): TextEditorReplace {
        var pos = startPos
        if (startPos == endPos) { // not highlighting
            while (pos != text.length && text[pos] != '\n') {
                pos++
            }
            if (pos == 0 || (pos == startPos && text[pos-1] == '\n')) {
                return TextEditorReplace("# ", pos, pos)
            } else {
                return TextEditorReplace("\n# ", pos, pos)
            }
        } else { // highlighting
            while (pos != 0 && text[pos - 1] != '\n') {
                pos--
            }
            if (text.substring(pos, pos + 4) == "### ") { // if header 3
                return TextEditorReplace("# ", pos, pos+4, pos + 2)
            } else if (text.substring(pos, pos + 3) == "## ") { // if header 2
                return TextEditorReplace("# ", pos, pos+3, pos + 2)
            } else { // no existing header
                return TextEditorReplace("# ", pos, pos, pos + 2)
            }
        }
    }

    fun headerTwoText(startPos: Int, endPos: Int, text: String): TextEditorReplace {
        var pos = startPos
        if (startPos == endPos) { // not highlighting
            while (pos != text.length && text[pos] != '\n') {
                pos++
            }
            if (pos == 0 || (pos == startPos && text[pos-1] == '\n')) {
                return TextEditorReplace("## ", pos, pos)
            } else {
                return TextEditorReplace("\n## ", pos, pos)
            }
        } else { // highlighting
            while (pos != 0 && text[pos - 1] != '\n') {
                pos--
            }
            if (text.substring(pos, pos + 4) == "### ") { // if header 3
                return TextEditorReplace("## ", pos, pos+4, pos + 3)
            } else if (text.substring(pos, pos + 2) == "# ") { // if header 1
                return TextEditorReplace("## ", pos, pos+2, pos + 3)
            } else { // no existing header
                return TextEditorReplace("## ", pos, pos)
            }
        }
    }

    fun headerThreeText(startPos: Int, endPos: Int, text: String): TextEditorReplace {
        var pos = startPos
        if (startPos == endPos) { // not highlighting
            while (pos != text.length && text[pos] != '\n') {
                pos++
            }
            if (pos == 0 || (pos == startPos && text[pos-1] == '\n')) {
                return TextEditorReplace("### ", pos, pos)
            } else {
                return TextEditorReplace("\n### ", pos, pos)
            }
        } else { // highlighting
            while (pos != 0 && text[pos - 1] != '\n') {
                pos--
            }
            if (text.substring(pos, pos + 3) == "## ") { // if header 2
                return TextEditorReplace("### ", pos, pos + 3, pos + 4)
            } else if (text.substring(pos, pos + 2) == "# ") { // if header 1
                return TextEditorReplace("### ", pos, pos + 2, pos + 4)
            } else { // no existing header
                return TextEditorReplace("### ", pos, pos, pos + 4)
            }
        }
    }

    fun ruleInsert (startPos: Int, endPos:Int, text: String): TextEditorReplace {
        var pos = endPos
        while (pos != text.length && text[pos] != '\n') {
            pos++
        }
        return if (pos == text.length) {
            TextEditorReplace("\n___\n", pos, pos, pos + 5)
        } else {
            TextEditorReplace("\n___", pos, pos, pos + 5)
        }
    }

    fun unorderedListText(startPos: Int, endPos: Int, text: String): TextEditorReplace {
        var pos = startPos
        if (startPos == endPos) { // not highlighting
            while (pos != text.length && text[pos] != '\n') {
                pos++
            }
            if (pos == 0 || (pos == startPos && text[pos - 1] == '\n')) {
                return TextEditorReplace("- ", pos, pos)
            } else {
                return TextEditorReplace("\n- ", pos, pos)
            }
        } else { // highlighting
            while (pos != 0 && text[pos - 1] != '\n') {
                pos--
            }
            return TextEditorReplace("- ", pos, pos)
        }
    }

    fun orderedListText(startPos: Int, endPos: Int, text: String): TextEditorReplace {
        var pos = startPos
        if (startPos == endPos) { // not highlighting
            while (pos != text.length && text[pos] != '\n') {
                pos++
            }
            if (pos == 0 || (pos == startPos && text[pos - 1] == '\n')) {
                return TextEditorReplace("1. ", pos, pos)
            } else {
                return TextEditorReplace("\n1. ", pos, pos)
            }
        } else { // highlighting
            while (pos != 0 && text[pos - 1] != '\n') {
                pos--
            }
            return TextEditorReplace("1. ", pos, pos)
        }
    }
}