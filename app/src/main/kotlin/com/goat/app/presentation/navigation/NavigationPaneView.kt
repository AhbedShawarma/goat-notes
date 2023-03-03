package com.goat.app.presentation.navigation

import com.goat.app.business.Controller
import com.goat.app.persistence.Context
import com.goat.app.persistence.Model
import com.goat.app.persistence.NavigationViewSelection
import com.goat.app.presentation.IView
import com.goat.app.presentation.utils.MakeResizable
import javafx.geometry.Insets
import javafx.scene.layout.*


class NavigationPaneView(val model: Model, val controller: Controller) : BorderPane(), IView {
    private var oldNVS = model.navigationViewSelection
    private val noteTreeView = NoteTreeView(model, controller)
    private val searchPane = SearchPane(model, controller)
    private val calendarPane = CalendarPane(model, controller)
    private val notesPane = object: VBox() {
        override fun requestFocus() {
            noteTreeView.requestFocus()
        }
    }
    private val searchPaneView = object: VBox() {
        override fun requestFocus() {
            searchPane.requestFocus()
        }
    }
    private val calendarWrapper = object: VBox() {}

    init {
        model.addView(this)
        prefWidth = 300.0
        style = "-fx-background-color: #333333;"

        VBox.setVgrow(noteTreeView, Priority.ALWAYS)

        MakeResizable(this)

        notesPane.children.add(noteTreeView)
        notesPane.padding = Insets(10.0)
        notesPane.spacing = 10.0
        notesPane.style = "-fx-background-color: #333333;"

        VBox.setVgrow(searchPane, Priority.ALWAYS)
        searchPaneView.children.add(searchPane)
        searchPaneView.padding = Insets(10.0)
        searchPaneView.spacing = 10.0

        VBox.setVgrow(calendarWrapper, Priority.ALWAYS)
        calendarWrapper.padding = Insets(10.0)
        calendarWrapper.spacing = 10.0
        calendarWrapper.style = "-fx-background-color: #333333;"
        calendarWrapper.children.add(calendarPane)
        VBox.setVgrow(calendarPane, Priority.ALWAYS)

        val notesButton = NavigationSwitchButton(model, controller, "\uf0c5", NavigationViewSelection.NOTES)
        val searchButton = NavigationSwitchButton(model, controller, "\uf002", NavigationViewSelection.SEARCH)
        val calendarButton = NavigationSwitchButton(model, controller, "\uf073", NavigationViewSelection.CALENDAR)
        val navigationSelector = VBox()
        navigationSelector.style = "-fx-background-color: #444444;"
        navigationSelector.children.addAll(notesButton, searchButton, calendarButton)
        navigationSelector.minWidth = 50.0
        minWidth = navigationSelector.minWidth + 212.0
        maxWidth = 500.0
        left = navigationSelector

        update()
    }

    override fun update(ctx: Context) {
        center = when(model.navigationViewSelection) {
            NavigationViewSelection.NOTES -> notesPane
            NavigationViewSelection.SEARCH -> searchPaneView
            NavigationViewSelection.CALENDAR -> calendarWrapper
            NavigationViewSelection.NONE -> null
            else -> TODO("NOT IMPLEMENTED")
        }
        if (oldNVS != model.navigationViewSelection
            && model.navigationViewSelection != NavigationViewSelection.NONE) {
            center.requestFocus()
        }
        oldNVS = model.navigationViewSelection
    }
}