package com.goat.app.presentation.navigation

import com.goat.app.business.Controller
import com.goat.app.persistence.Context
import com.goat.app.persistence.Model
import com.goat.app.persistence.NavigationViewSelection
import com.goat.app.presentation.IView
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.text.Font
import java.io.File
import java.io.InputStream
import java.nio.file.Path

class NavigationSwitchButton(val model: Model,
                             val controller: Controller,
                             displaySymbol: String,
                             val nvs: NavigationViewSelection): IView, Button(displaySymbol) {
    private val defaultStyle = "-fx-background-color: #444444; -fx-text-fill: white;"
    private val selectedStyle = "-fx-background-color: #666666; -fx-text-fill: white;"

    init {
        model.addView(this)
        setOnAction { controller.setNavigationViewSelection(nvs) }
        val path = Path.of("").toAbsolutePath().toString() + "/resources/fa-solid.otf"
        val inputStream: InputStream = File(path).inputStream()
        font = Font.loadFont(inputStream, 20.0)
        style = defaultStyle
        prefWidth = 50.0
        prefHeight = 50.0
        padding = Insets.EMPTY
    }

    override fun update(ctx: Context) {
        style = when(model.navigationViewSelection) {
            nvs -> selectedStyle
            else -> defaultStyle
        }
    }
}