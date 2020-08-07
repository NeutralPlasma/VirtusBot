package com.neutralplasma.virtusbot.storage.dataStorage

import java.sql.Connection
import java.sql.SQLException

abstract class Storage {
    open fun openConnection() {}
    open fun closeConnection() {}

    @Throws(SQLException::class)
    open fun createTable(tableName: String, format: String) {
    }

    @get:Throws(SQLException::class)
    open val connection: Connection?
        get() = null
}