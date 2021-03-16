package com.neutralplasma.virtusbot.storage.dataStorage

import com.neutralplasma.virtusbot.storage.config.Info
import java.sql.Connection
import java.sql.SQLException
import kotlin.jvm.Throws

class StorageHandler {
    var storage: Storage? = null

    @get:Throws(SQLException::class)
    val connection: Connection?
        get() = storage?.connection

    @Throws(SQLException::class)
    fun createTable(tableName: String, format: String) {
        storage!!.createTable(tableName, format)
    }

    fun closeConnection() {
        storage!!.closeConnection()
    }

    fun execute(code: String){
        storage!!.execute(code)
    }

    init {
        storage = if (Info.USE_MYSQL) {
            MySQL()
        } else {
            SQL()
        }
        storage!!.openConnection()
    }
}