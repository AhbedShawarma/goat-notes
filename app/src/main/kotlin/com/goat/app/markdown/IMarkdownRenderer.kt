package com.goat.app.markdown

interface IMarkdownRenderer {
    fun textToHtml(text: String): String
}