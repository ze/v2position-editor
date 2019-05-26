package com.zelkatani.gui.view

import com.zelkatani.gui.applicationName
import com.zelkatani.gui.controller.DirectoryController
import com.zelkatani.gui.preferencesName
import com.zelkatani.model.GameLocation
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.ButtonBar
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import tornadofx.*

class DirectoryView : View(applicationName) {
    private val directoryController: DirectoryController by inject()

    private val model = object : ViewModel() {
        val gamePath = bind { SimpleStringProperty() }
        val modPath = bind { SimpleStringProperty() }
    }

    override fun onBeforeShow() {
        model.gamePath.value = GameLocation.gamePath.orEmpty()
        model.modPath.value = GameLocation.modPath.orEmpty()
    }

    override val root = form {
        spacing = 7.5
        prefWidth = 500.0

        fieldset("Game Paths") {
            vbox(10) {
                hbox(10) {
                    textfield {
                        bind(model.gamePath, true)
                        isEditable = false

                        HBox.setHgrow(this, Priority.ALWAYS)
                    }

                    button("Victoria 2 Path") {
                        prefWidth = 150.0

                        action {
                            val file = chooseDirectory {
                                title = "Select the Victoria 2 game location."
                            }

                            file?.let {
                                model.gamePath.value = file.path
                            }
                        }
                    }
                }

                hbox(10) {
                    textfield {
                        bind(model.modPath, true)
                        isEditable = false

                        HBox.setHgrow(this, Priority.ALWAYS)
                    }

                    button("Mod Path") {
                        prefWidth = 150.0

                        action {
                            val file = chooseDirectory {
                                title = "Select the mod folder directory."
                            }

                            file?.let {
                                model.modPath.value = file.path
                            }
                        }
                    }
                }
            }
        }

        buttonbar {
            button("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE).action {
                this@DirectoryView.close()
            }

            button("Save", ButtonBar.ButtonData.OK_DONE) {
                isDefaultButton = true

                val booleanBinding = Bindings.or(model.gamePath.isBlank(), model.modPath.isBlank())
                disableProperty().bind(booleanBinding)

                action {
                    model.commit {
                        preferences(preferencesName) {
                            put("game_path", model.gamePath.value)
                            put("mod_path", model.modPath.value)
                        }

                        GameLocation.gamePath = model.gamePath.value
                        GameLocation.modPath = model.modPath.value
                        directoryController.commitGameLocation()
                    }
                }
            }
        }
    }
}
