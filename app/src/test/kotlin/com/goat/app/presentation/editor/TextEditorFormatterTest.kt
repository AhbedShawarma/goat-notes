package com.goat.app.presentation.editor

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class TextEditorFormatterTest {
    companion object {
        fun replaceText(replace: TextEditorReplace, text: String): String {
            return text.replaceRange(replace.start, replace.end, replace.text)
        }
    }
    @Test
    fun boldTextSelected() {
        val text = "example bold text selection"
        val replace = TextEditorFormatter.boldText(8,17, text)
        val newText = replaceText(replace, text)
        assertEquals("example **bold text** selection", newText)
        assertEquals(19, replace.caretPos)
    }

    @Test
    fun boldTextEnd() {
        val text = "example bold text"
        val replace = TextEditorFormatter.boldText(17,17, text)
        val newText = replaceText(replace, text)
        assertEquals("example bold text****", newText)
        assertEquals(19, replace.caretPos)
    }

    @Test
    fun italicizeTextSelected() {
        val text = "example italics text selection"
        val replace = TextEditorFormatter.italicizeText(8,20, text)
        val newText = replaceText(replace, text)
        assertEquals("example *italics text* selection", newText)
        assertEquals(21, replace.caretPos)
    }

    @Test
    fun italicizeTextEnd() {
        val text = "example italics text"
        val replace = TextEditorFormatter.italicizeText(20,20, text)
        val newText = replaceText(replace, text)
        assertEquals("example italics text**", newText)
        assertEquals(21, replace.caretPos)
    }

    @Test
    fun inlineCodeTextSelected() {
        val text = "example inline code text selection"
        val replace = TextEditorFormatter.inlineCodeText(8,24, text)
        val newText = replaceText(replace, text)
        assertEquals("example `inline code text` selection", newText)
        assertEquals(25, replace.caretPos)
    }

    @Test
    fun inlineCodeTextEnd() {
        val text = "example inline code text"
        val replace = TextEditorFormatter.inlineCodeText(24,24, text)
        val newText = replaceText(replace, text)
        assertEquals("example inline code text``", newText)
        assertEquals(25, replace.caretPos)
    }

    @Test
    fun hyperlinkTextSelected() {
        val text = "example hyperlink selection"
        val replace = TextEditorFormatter.hyperlinkText(8,17, text)
        val newText = replaceText(replace, text)
        assertEquals("example [hyperlink]() selection", newText)
        assertEquals(20, replace.caretPos)
    }

    @Test
    fun hyperlinkTextEnd() {
        val text = "example hyperlink"
        val replace = TextEditorFormatter.hyperlinkText(17,17, text)
        val newText = replaceText(replace, text)
        assertEquals("example hyperlink[]()", newText)
        assertEquals(20, replace.caretPos)
    }

    @Test
    fun headerOneBeginning() {
        val text = "Header 1 start"
        val replace = TextEditorFormatter.headerOneText(2,3, text)
        val newText = replaceText(replace, text)
        assertEquals("# Header 1 start", newText)
        assertEquals(2, replace.caretPos)
    }

    @Test
    fun headerOneEnd() {
        val text = "Line 1"
        val replace = TextEditorFormatter.headerOneText(2,2, text)
        val newText = replaceText(replace, text)
        assertEquals("Line 1\n# ", newText)
        assertEquals(9, replace.caretPos)
    }

    @Test
    fun headerOneNewLine() {
        val text = "Line 1\nLine 2"
        val replace = TextEditorFormatter.headerOneText(2,2, text)
        val newText = replaceText(replace, text)
        assertEquals("Line 1\n# \nLine 2", newText)
        assertEquals(9, replace.caretPos)
    }

    @Test
    fun headerOneCurrentLine() {
        val text = "Line 1\nHeader 1"
        val replace = TextEditorFormatter.headerOneText(11,14, text)
        val newText = replaceText(replace, text)
        assertEquals("Line 1\n# Header 1", newText)
        assertEquals(9, replace.caretPos)
    }

    @Test
    fun headerOneOverwriteH2() {
        val text = "Line 1\n## Header 1"
        val replace = TextEditorFormatter.headerOneText(11,14, text)
        val newText = replaceText(replace, text)
        assertEquals("Line 1\n# Header 1", newText)
        assertEquals(9, replace.caretPos)
    }

    @Test
    fun headerOneOverwriteH3() {
        val text = "Line 1\n### Header 1"
        val replace = TextEditorFormatter.headerOneText(11,14, text)
        val newText = replaceText(replace, text)
        assertEquals("Line 1\n# Header 1", newText)
        assertEquals(9, replace.caretPos)
    }

    @Test
    fun headerTwoBeginning() {
        val text = "Header 2 start"
        val replace = TextEditorFormatter.headerTwoText(2,3, text)
        val newText = replaceText(replace, text)
        assertEquals("## Header 2 start", newText)
        assertEquals(3, replace.caretPos)
    }

    @Test
    fun headerTwoEnd() {
        val text = "Line 1"
        val replace = TextEditorFormatter.headerTwoText(2,2, text)
        val newText = replaceText(replace, text)
        assertEquals("Line 1\n## ", newText)
        assertEquals(10, replace.caretPos)
    }

    @Test
    fun headerTwoNewLine() {
        val text = "Line 1\nLine 2"
        val replace = TextEditorFormatter.headerTwoText(2,2, text)
        val newText = replaceText(replace, text)
        assertEquals("Line 1\n## \nLine 2", newText)
        assertEquals(10, replace.caretPos)
    }

    @Test
    fun headerTwoCurrentLine() {
        val text = "Line 1\nHeader 2"
        val replace = TextEditorFormatter.headerTwoText(11,14, text)
        val newText = replaceText(replace, text)
        assertEquals("Line 1\n## Header 2", newText)
        assertEquals(10, replace.caretPos)
    }

    @Test
    fun headerTwoOverwriteH1() {
        val text = "Line 1\n# Header 2"
        val replace = TextEditorFormatter.headerTwoText(11,14, text)
        val newText = replaceText(replace, text)
        assertEquals("Line 1\n## Header 2", newText)
        assertEquals(10, replace.caretPos)
    }

    @Test
    fun headerTwoOverwriteH3() {
        val text = "Line 1\n### Header 2"
        val replace = TextEditorFormatter.headerTwoText(11,14, text)
        val newText = replaceText(replace, text)
        assertEquals("Line 1\n## Header 2", newText)
        assertEquals(10, replace.caretPos)
    }

    @Test
    fun headerThreeBeginning() {
        val text = "Header 3 start"
        val replace = TextEditorFormatter.headerThreeText(2,3, text)
        val newText = replaceText(replace, text)
        assertEquals("### Header 3 start", newText)
        assertEquals(4, replace.caretPos)
    }

    @Test
    fun headerThreeEnd() {
        val text = "Line 1"
        val replace = TextEditorFormatter.headerThreeText(2,2, text)
        val newText = replaceText(replace, text)
        assertEquals("Line 1\n### ", newText)
        assertEquals(11, replace.caretPos)
    }

    @Test
    fun headerThreeNewLine() {
        val text = "Line 1\nLine 2"
        val replace = TextEditorFormatter.headerThreeText(2,2, text)
        val newText = replaceText(replace, text)
        assertEquals("Line 1\n### \nLine 2", newText)
        assertEquals(11, replace.caretPos)
    }

    @Test
    fun headerThreeCurrentLine() {
        val text = "Line 1\nHeader 3"
        val replace = TextEditorFormatter.headerThreeText(11,14, text)
        val newText = replaceText(replace, text)
        assertEquals("Line 1\n### Header 3", newText)
        assertEquals(11, replace.caretPos)
    }

    @Test
    fun headerThreeOverwriteH1() {
        val text = "Line 1\n# Header 3"
        val replace = TextEditorFormatter.headerThreeText(11,14, text)
        val newText = replaceText(replace, text)
        assertEquals("Line 1\n### Header 3", newText)
        assertEquals(11, replace.caretPos)
    }

    @Test
    fun headerThreeOverwriteH2() {
        val text = "Line 1\n## Header 3"
        val replace = TextEditorFormatter.headerThreeText(11,14, text)
        val newText = replaceText(replace, text)
        assertEquals("Line 1\n### Header 3", newText)
        assertEquals(11, replace.caretPos)
    }

    @Test
    fun ruleInsertEnd() {
        val text = "Line 1"
        val replace = TextEditorFormatter.ruleInsert(2, 2, text)
        val newText = replaceText(replace, text)
        assertEquals("Line 1\n___\n", newText)
        assertEquals(11, replace.caretPos)
    }

    @Test
    fun ruleInsertMiddle() {
        val text = "Line 1\nLine 2"
        val replace = TextEditorFormatter.ruleInsert(2, 2, text)
        val newText = replaceText(replace, text)
        assertEquals("Line 1\n___\nLine 2", newText)
        assertEquals(11, replace.caretPos)
    }
}