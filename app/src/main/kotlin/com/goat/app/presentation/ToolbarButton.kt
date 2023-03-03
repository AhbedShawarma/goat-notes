package com.goat.app.presentation

import com.goat.app.business.Controller
import com.goat.app.persistence.Model
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.text.Font
import java.io.File
import java.io.InputStream
import java.nio.file.Path


class ToolbarButton(val type: ToolbarButtonType, model: Model, val controller: Controller) : Button(), IView {

    init {
        model.addView(this)

        val path = Path.of("").toAbsolutePath().toString() + "/resources/fa-solid.otf"
        val inputStream: InputStream = File(path).inputStream()
        font = Font.loadFont(inputStream, 16.0)

        val defaultStyle = "-fx-background-radius: 0; -fx-background-color: transparent; -fx-border: none; -fx-text-fill: #333333;"
        val hoverStyle = "-fx-background-radius: 0; -fx-background-color: #B3B3B3; -fx-border: none; -fx-text-fill: #333333;"

        style = defaultStyle
        hoverProperty().addListener { _, _, newValue ->
            style = if (newValue) {
                hoverStyle
            } else {
                defaultStyle
            }
        }

        when(type) {
            ToolbarButtonType.UNDO -> {
                text = "\uf2ea"
            }
            ToolbarButtonType.REDO -> {
                text = "\uf2f9"
            }
            ToolbarButtonType.BOLD -> {
                text = "\uf032"
            }
            ToolbarButtonType.ITALIC -> {
                text = "\uf033"
            }
            ToolbarButtonType.H1 -> {
                text = "\uf1dc" + "1"
            }
            ToolbarButtonType.H2 -> {
                text = "\uF1DC" + "2"
            }
            ToolbarButtonType.H3 -> {
                text = "\uF1DC" + "3"
            }
            ToolbarButtonType.CODE -> {
                text = "\uf121"
            }
            ToolbarButtonType.UNORDERED -> {
                text = "\uf0ca"
            }
            ToolbarButtonType.ORDERED -> {
                text = "\uf0cb"
            }
            ToolbarButtonType.HYPERLINK -> {
                text = "\uf0c1"
            }
            ToolbarButtonType.RULE -> {
                text = "\uf2d1"
            }
            else -> TODO()
        }
        height = 30.0
        minWidth = 50.0
        onAction = EventHandler {
            parent.requestFocus()
            doAction()
        }
    }

    private fun doAction() {
        when(type) {
            ToolbarButtonType.UNDO -> {
                controller.undoNoteBody()
            }
            ToolbarButtonType.REDO -> {
                controller.redoNoteBody()
            }
            ToolbarButtonType.BOLD -> {
                controller.boldText()
            }
            ToolbarButtonType.ITALIC -> {
                controller.italicizeText()
            }
            ToolbarButtonType.H1 -> {
                controller.headerOneText()
            }
            ToolbarButtonType.H2 -> {
                controller.headerTwoText()
            }
            ToolbarButtonType.H3 -> {
                controller.headerThreeText()
            }
            ToolbarButtonType.CODE -> {
                controller.inlineCodeText()
            }
            ToolbarButtonType.UNORDERED -> {
                controller.unorderedListText()
            }
            ToolbarButtonType.ORDERED -> {
                controller.orderedListText()
            }
            ToolbarButtonType.HYPERLINK -> {
                controller.hyperlinkText()
            }
            ToolbarButtonType.RULE -> {
                controller.ruleInsert()
            }
            else -> TODO()
        }
    }
}