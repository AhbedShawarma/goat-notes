package com.goat.app.presentation.utils

import com.goat.app.presentation.navigation.NavigationPaneView
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.input.MouseEvent


// Adapted from: https://github.com/varren/JavaFX-Resizable-Draggable-Node/blob/master/src/sample/DragResizeMod.java
class MakeResizable(private val node: Node) {

    interface OnDragResizeEventListener {
        fun onResize(node: Node?, h: Double, w: Double)
    }

    private var nodeX: Double = 0.0
    private var nodeY: Double = 0.0
    private var nodeH: Double = 0.0
    private var nodeW: Double = 0.0

    private val MARGIN = 8
    private val listener = object : OnDragResizeEventListener {
        override fun onResize(node: Node?, h: Double, w: Double) {
            when (node) {
                is NavigationPaneView -> {
                    var newWidth = w
                    if (w < node.minWidth) {
                        newWidth = node.minWidth
                    } else if (w > node.maxWidth) {
                        newWidth = node.maxWidth
                    }
                    node.prefWidth = newWidth
                }
            }
        }
    }

    init {
        setNewInitialEventCoordinates()
        node.onMouseMoved = EventHandler { event -> mouseOver(event) }
        node.onMousePressed = EventHandler { event -> mousePressed(event) }
        node.onMouseDragged = EventHandler { event -> mouseDragged(event) }
        node.onMouseReleased = EventHandler { event -> mouseReleased(event) }
    }

    private  fun mouseDragged(event: MouseEvent) {
        val mouseX: Double = parentX(event.x)

        // resizing
        if (node.cursor != Cursor.DEFAULT) {
            val newH = nodeH
            var newW = nodeW

            // Right Resize
            if (node.cursor == Cursor.W_RESIZE) {
                newW = mouseX - nodeX
            }

            listener.onResize(node, newH, newW)
        }
    }

    private fun mousePressed(event: MouseEvent) {
        if (isInResizeZone(event)) {
            setNewInitialEventCoordinates()
        }
    }

    private fun setNewInitialEventCoordinates() {
        nodeX = nodeX()
        nodeY = nodeY()
        nodeH = nodeH()
        nodeW = nodeW()
    }

    private fun isInResizeZone(event: MouseEvent): Boolean {
        return isRightResizeZone(event)
    }

    private fun mouseReleased(event: MouseEvent) {
        node.cursor = Cursor.DEFAULT
    }

    private fun mouseOver(event: MouseEvent) {
        val cursor: Cursor = getCursorFromEvent(event)
//        println("nav pane mouse moved: $cursor")
        node.cursor = cursor
    }

    private fun getCursorFromEvent(event: MouseEvent): Cursor {
        val isOnLeft = isLeftResizeZone(event)
        val isOnRight = isRightResizeZone(event)
        if (isOnLeft || isOnRight) {
            return Cursor.W_RESIZE
        }
        return Cursor.DEFAULT
    }

    private fun isLeftResizeZone(event: MouseEvent): Boolean {
        return intersect(0.0, event.x)
    }

    private fun isRightResizeZone(event: MouseEvent): Boolean {
        return intersect(nodeW(), event.x)
    }

    private fun intersect(side: Double, point: Double): Boolean {
        return side + MARGIN >= point && side - MARGIN <= point
    }

    private fun parentX(localX: Double): Double {
        return nodeX() + localX
    }

    private fun parentY(localY: Double): Double {
        return nodeY() + localY
    }

    private fun nodeX(): Double {
        return node.boundsInParent.minX
    }

    private fun nodeY(): Double {
        return node.boundsInParent.minY
    }

    private fun nodeH(): Double {
        return node.boundsInParent.height
    }

    private fun nodeW(): Double {
        return node.boundsInParent.width
    }

}