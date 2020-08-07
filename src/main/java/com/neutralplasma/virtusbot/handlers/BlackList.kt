package com.neutralplasma.virtusbot.handlers

import com.neutralplasma.virtusbot.storage.dataStorage.StorageHandler
import com.neutralplasma.virtusbot.utils.TextUtil.sendMessage
import java.sql.SQLException
import java.util.*

class BlackList(private val storageHandler: StorageHandler) {
    private val TableName = "BlackList"
    private val blackList = ArrayList<String>()
    fun isBlackListed(userID: String?): Boolean {
        return blackList.contains(userID)
    }

    fun addToBlackList(userID: String) {
        if (!isBlackListed(userID)) {
            blackList.add(userID)
        }
    }

    fun removeFromBlackList(userID: String?) {
        if (isBlackListed(userID)) {
            blackList.remove(userID)
        }
    }

    fun blackListUpdater() {
        val t = Timer()
        t.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                syncer.run()
            }
        }, 100, 60000)
    }

    var syncer = Runnable {
        try {
            syncBlackList()
        } catch (ignored: Exception) {
        }
    }

    @Throws(SQLException::class)
    fun syncBlackList() {
        val data: List<String> = ArrayList(blackList)
        storageHandler.connection.use { connection ->
            val statement = "DELETE FROM $TableName;"
            connection!!.prepareStatement(statement).use { preparedStatement -> preparedStatement.execute() }
            for (userinfo in data) {
                val statement2 = "INSERT INTO " + TableName + " (" +
                        "userID)" +
                        " VALUES (?)"
                connection.prepareStatement(statement2).use { preparedStatement ->
                    preparedStatement.setString(1, userinfo)
                    preparedStatement.execute()
                }
            }
        }
    }

    @Throws(SQLException::class)
    fun cacheBlackList() {
        storageHandler.connection.use { connection ->
            val statement = "SELECT * from $TableName;"
            connection!!.prepareStatement(statement).use { preparedStatement ->
                var amount = 0
                val resultSet = preparedStatement.executeQuery()
                while (resultSet.next()) {
                    amount++
                    try {
                        val ID = resultSet.getString("userID")
                        blackList.add(ID)
                    } catch (ignored: Exception) {
                    }
                }
                sendMessage("Loaded: $amount blacklisted users from database.")
            }
        }
    }

    init {
        try {
            storageHandler.createTable(TableName, "UserID TEXT")
        } catch (error: SQLException) {
            error.printStackTrace()
        }
        try {
            cacheBlackList()
        } catch (error: SQLException) {
            error.printStackTrace()
        }
        blackListUpdater()
    }
}