package com.zelkatani.gui.view

import com.zelkatani.gui.app.APPLICATION_NAME
import com.zelkatani.gui.app.GAME_PATH
import com.zelkatani.gui.app.MOD_PATH
import com.zelkatani.gui.app.PREFERENCES_NAME
import com.zelkatani.gui.controller.DirectoryController
import com.zelkatani.model.GameLocation
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventTarget
import javafx.scene.control.ButtonBar
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import tornadofx.*

/**
 * The starting [View] for the application. Defines the game and mod path.
 */
class DirectoryView : View(APPLICATION_NAME) {
    /**
     * The [DirectoryController] for this view.
     */
    private val directoryController: DirectoryController by inject()

    /**
     * The [ViewModel] for defining the game and mod path.
     */
    private val model = object : ViewModel() {
        val gamePath = bind { SimpleStringProperty() }
        val modPath = bind { SimpleStringProperty() }
    }

    /**
     * Set the values of [model] if they already exist.
     */
    override fun onBeforeShow() {
        model.gamePath.value = GameLocation.gamePath.orEmpty()
        model.modPath.value = GameLocation.modPath.orEmpty()
    }

    /**
     * Attach a directory selector with a read-only indicator of value.
     *
     * @param name The field name.
     * @param message The directory selector message.
     * @param filePath The property to bind, and update the value of.
     */
    private fun EventTarget.selector(name: String, message: String, filePath: Property<String>) {
        hbox(10) {
            textfield {
                bind(filePath, true)
                isEditable = false

                HBox.setHgrow(this, Priority.ALWAYS)
            }

            button(name) {
                prefWidth = 150.0

                action {
                    val file = chooseDirectory(message)

                    file?.let {
                        filePath.value = file.path
                    }
                }
            }
        }

    }

    override val root = form {
        spacing = 7.5
        prefWidth = 650.0

        fieldset("Game Paths", FontAwesomeIconView(FontAwesomeIcon.FOLDER)) {
            vbox(10) {
                selector("Victoria 2 Path", "Select the Victoria 2 game location.", model.gamePath)
                selector("Mod Path", "Select the mod folder directory.", model.modPath)
            }
        }

        buttonbar {
            button("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE).action {
                this@DirectoryView.close()
            }

            button("Save", ButtonBar.ButtonData.OK_DONE) {
                isDefaultButton = true

                disableWhen {
                    Bindings.or(model.gamePath.isBlank(), model.modPath.isBlank())
                }

                action {
                    model.commit {
                        preferences(PREFERENCES_NAME) {
                            put(GAME_PATH, model.gamePath.value)
                            put(MOD_PATH, model.modPath.value)
                        }

                        GameLocation.gamePath = model.gamePath.value
                        GameLocation.modPath = model.modPath.value
                        directoryController.commitGameLocation()
                    }
                }
            }
        }

        this@DirectoryView.primaryStage.sizeToScene()
    }
}
