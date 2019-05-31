package com.zelkatani.gui.fragment

import javafx.beans.property.ObjectProperty
import javafx.beans.value.ChangeListener
import javafx.scene.Node
import javafx.scene.layout.StackPane
import tornadofx.*

class PositionScope(
    val nodes: ObjectProperty<MutableList<Node>>
) : Scope()

class PositionFragment : Fragment() {
    override val scope = super.scope as PositionScope

    private val nodesListener: ChangeListener<MutableList<Node>> = ChangeListener { _, _, newValue ->
        (root.center as StackPane).children.setAll(newValue)
    }

    override fun onUndock() {
        scope.nodes.removeListener(nodesListener)
    }

    override fun onDock() {
        scope.nodes.addListener(nodesListener)
    }

    override val root = borderpane {
        left = form {

        }

        center = stackpane {
            scope.nodes.value.forEach {
                add(it)
            }
        }
    }
}