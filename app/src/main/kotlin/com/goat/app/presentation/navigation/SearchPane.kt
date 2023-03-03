package com.goat.app.presentation.navigation

import com.goat.app.business.Controller
import com.goat.app.persistence.Context
import com.goat.app.persistence.Model
import com.goat.app.presentation.IView
import javafx.geometry.Insets
import javafx.scene.control.TextField
import javafx.scene.layout.*
import javafx.scene.paint.Color


class SearchPane(val model: Model, val controller: Controller) : IView, VBox() {
    private val searchBar = TextField()

    init {
        model.addView(this)

        searchBar.promptText = "Search"
        searchBar.background = Background(BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY))
        searchBar.style = "-fx-text-fill: white;"
        searchBar.textProperty().addListener { _, _, newValue -> controller.search(newValue) }

        val searchResults = FilteredNotesList(model, controller)
        setVgrow(searchResults, Priority.ALWAYS)

        children.addAll(searchBar, searchResults)
    }

    override fun requestFocus() {
        searchBar.requestFocus()
    }
}