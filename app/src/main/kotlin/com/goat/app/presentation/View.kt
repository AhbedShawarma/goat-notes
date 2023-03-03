package com.goat.app.presentation

import com.goat.app.business.Controller
import com.goat.app.persistence.Model
import com.goat.app.presentation.editor.NoteEditorPane
import com.goat.app.presentation.navigation.NavigationPaneView
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import java.io.File
import java.io.InputStream
import java.nio.file.Path

class View(val controller: Controller, val model: Model, stage: Stage) : IView {
    val menu = WindowMenuBar(model, controller)
    val nav = NavigationPaneView(model, controller)
    val note = NoteEditorPane(model, controller)
    private val iconPath: InputStream = File(Path.of("").toAbsolutePath().toString() + "/resources/images/goat-final.png").inputStream()

    init {
        val root = BorderPane()
        root.top = menu
        root.left = nav
        root.center = note
        val scene = Scene(root, 1200.0, 700.0)
        val url = File("resources/style.css").toURI().toURL()
        scene.stylesheets.add(url.toString())
        stage.title = "Goat Notes"
        stage.scene = scene
        stage.icons.add(Image(iconPath))
        note.requestFocus()
        stage.show()
    }
}