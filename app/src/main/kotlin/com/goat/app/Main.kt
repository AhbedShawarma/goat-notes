package com.goat.app

import com.goat.app.business.Controller
import com.goat.app.business.sync.SyncClient
import com.goat.app.persistence.Context
import com.goat.app.persistence.Model
import com.goat.app.presentation.*
import javafx.application.Application
import com.goat.app.persistence.db.DBConnection
import com.goat.app.persistence.db.DBHelper
import javafx.stage.Stage

class Main : Application() {

    private lateinit var model: Model
    private lateinit var controller: Controller
    private lateinit var view: View
    private lateinit var database: DBConnection
    private lateinit var syncClient: SyncClient

    override fun start(stage: Stage) {
        syncClient = SyncClient()
        database = DBConnection()
        database.connection?.let { DBHelper(connection = it) }
        model = Model(database)
        controller = Controller(model, syncClient)
        view = View(controller, model, stage)
        model.update(Context())
    }

    override fun stop() {
        super.stop()
        controller.close()
        database.disconnect()
    }
}

fun main() {
    Application.launch(Main::class.java)
}