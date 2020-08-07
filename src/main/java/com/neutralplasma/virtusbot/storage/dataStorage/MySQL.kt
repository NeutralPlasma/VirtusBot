package com.neutralplasma.virtusbot.storage.dataStorage

import com.neutralplasma.virtusbot.storage.config.Info
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.sql.SQLException

class MySQL : Storage() {
    private var hikari: HikariDataSource? = null
    override fun openConnection() {
        val timeout = 1000L // timeout
        val poolsize = 10 // pool size max
        hikari = HikariDataSource()
        hikari!!.dataSourceClassName = "com.mysql.cj.jdbc.MysqlDataSource"
        hikari!!.poolName = "Storage"
        hikari!!.maximumPoolSize = poolsize
        hikari!!.connectionTimeout = timeout
        hikari!!.addDataSourceProperty("serverName", Info.DATABASE_IP)
        hikari!!.addDataSourceProperty("port", Info.DATABASE_PORT)
        hikari!!.addDataSourceProperty("databaseName", Info.DATABASE_NAME)
        hikari!!.addDataSourceProperty("user", Info.DATABASE_USER)
        hikari!!.addDataSourceProperty("password", Info.DATABASE_PASSWORD)
        hikari!!.addDataSourceProperty("useSSL", Info.USE_SSL)
    }

    override fun closeConnection() {
        hikari!!.close()
    }

    /**
     * Create table in database.
     *
     * @param tableName table name.
     * @param format Table format.
     */
    @Throws(SQLException::class)
    override fun createTable(tableName: String, format: String) {
        hikari!!.connection.use { connection ->
            val statement = "CREATE TABLE IF NOT EXISTS $tableName ($format);"
            connection.prepareStatement(statement).use { preparedStatement -> preparedStatement.execute() }
        }
    }

    @get:Throws(SQLException::class)
    override val connection: Connection?
        get() = hikari!!.connection

    init {
        openConnection()
    }
}