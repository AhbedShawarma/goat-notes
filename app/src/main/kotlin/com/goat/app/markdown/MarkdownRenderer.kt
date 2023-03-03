package com.goat.app.markdown

import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

class MarkdownRenderer: IMarkdownRenderer {
    override fun textToHtml(text: String): String {
        val flavour = CommonMarkFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(text)
        val html = HtmlGenerator(text, parsedTree, flavour).generateHtml()
        return html
    }
}