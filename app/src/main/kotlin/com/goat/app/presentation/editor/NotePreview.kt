package com.goat.app.presentation.editor

import com.goat.app.business.Controller
import com.goat.app.markdown.IMarkdownRenderer
import com.goat.app.markdown.MarkdownRenderer
import com.goat.app.persistence.Context
import com.goat.app.persistence.Model
import com.goat.app.presentation.IView
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.web.WebView


class NotePreview(val model: Model, controller: Controller) : VBox(), IView {
    private val webView = WebView()
    private val webEngine = webView.engine
    private val markdownRenderer: IMarkdownRenderer = MarkdownRenderer()

    init {
        model.addView(this)
        setVgrow(webView, Priority.ALWAYS)
        children.add(webView)
        update()
    }

    override fun update(ctx: Context) {
        renderMarkdown()
    }

    fun renderMarkdown() {
        val html = markdownRenderer.textToHtml(model.focusedNote.body)
        webEngine.loadContent(html)
    }
}