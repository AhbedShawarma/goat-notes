package com.goat.app.markdown

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MarkdownRendererTest {
    private val markdownRenderer = MarkdownRenderer()

    @Test
    fun textToHtmlBody() {
        val text = "text"
        val html = markdownRenderer.textToHtml(text)

        assert(html.isNotEmpty())
        assert(html.isNotBlank())

        // check if the html contains "text within matching paragraph tags and matching body tags
        // \\s* is used to match any whitespace
        val pattern = "^\\s*<body>\\s*<p>\\s*text\\s*</p>\\s*</body>\\s*$"
        val regex = Regex(pattern)
        assertTrue(
            regex.containsMatchIn(html),
            "html: \"$html\" should contain a match to pattern: \"$pattern\"")
    }

    @Test
    fun textToHtmlBold() {
        val text = "**text**"
        val html = markdownRenderer.textToHtml(text)

        assert(html.isNotEmpty())
        assert(html.isNotBlank())

        // check for matching bold tags within matching paragraph tags
        // \\s* is used to match any whitespace
        val pattern = "<p>\\s*<strong>text</strong>\\s*</p>"
        val regex = Regex(pattern)
        assertTrue(
            regex.containsMatchIn(html),
            "html: \"$html\" should contain a match to pattern: \"$pattern\"")
    }

    @Test
    fun textToHtmlItalics() {
        val text = "*text*"
        val html = markdownRenderer.textToHtml(text)

        assert(html.isNotEmpty())
        assert(html.isNotBlank())

        // check for matching italics tags within matching paragraph tags
        // \\s* is used to match any whitespace
        val pattern = "<p>\\s*<em>text</em>\\s*</p>"
        val regex = Regex(pattern)
        assertTrue(
            regex.containsMatchIn(html),
            "html: \"$html\" should contain a match to pattern: \"$pattern\"")
    }

    @Test
    fun textToHtmlHeading1() {
        val text = "# text"
        val html = markdownRenderer.textToHtml(text)

        assert(html.isNotEmpty())
        assert(html.isNotBlank())

        // check for matching heading 1 tags
        // \\s* is used to match any whitespace
        val pattern = "<h1>text</h1>"
        val regex = Regex(pattern)
        assertTrue(
            regex.containsMatchIn(html),
            "html: \"$html\" should contain a match to pattern: \"$pattern\"")
    }

    @Test
    fun textToHtmlHyperlink() {
        val text = "[text](link)"
        val html = markdownRenderer.textToHtml(text)

        assert(html.isNotEmpty())
        assert(html.isNotBlank())

        // check for matching hyperlink tags with the correct url
        // \\s* is used to match any whitespace
        val pattern = "<p>\\s*<a href=\"link\">text</a>\\s*</p>"
        val regex = Regex(pattern)
        assertTrue(
            regex.containsMatchIn(html),
            "html: \"$html\" should contain a match to pattern: \"$pattern\"")
    }

    @Test
    fun textToHtmlInlineCode() {
        val text = "`text`"
        val html = markdownRenderer.textToHtml(text)

        assert(html.isNotEmpty())
        assert(html.isNotBlank())

        // check for matching code tags
        // \\s* is used to match any whitespace
        val pattern = "<p>\\s*<code>text</code>\\s*</p>"
        val regex = Regex(pattern)
        assertTrue(
            regex.containsMatchIn(html),
            "html: \"$html\" should contain a match to pattern: \"$pattern\"")
    }
}