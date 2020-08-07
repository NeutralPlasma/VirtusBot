package com.neutralplasma.virtusbot.storage.ticket

import com.neutralplasma.virtusbot.storage.dataStorage.StorageHandler
import java.sql.SQLException

class TicketStorage(private val sql: StorageHandler) {
    private val tableName = "TicketData"
    fun setup() {
        try {
            sql.createTable(tableName, "userID TEXT, channelID TEXT")
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun writeSettings(userID: String, channelID: String) {
        val info = TicketInfo(userID, channelID)
        try {
            addTicket(info)
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun deleteTicket(userID: String, channelID: String) {
        val info = TicketInfo(userID, channelID)
        try {
            removeTicket(info)
        } catch (sqlerror: SQLException) {
            sqlerror.printStackTrace()
        }
    }

    fun getTicketChannel(channelID: String): TicketInfo? {
        try {
            if (!channelID.isEmpty()) {
                val ticketid = getTicketbyChannel(channelID)
                if (ticketid != null) {
                    return ticketid
                }
            }
            return null
        } catch (error: Exception) {
            error.printStackTrace()
        }
        return null
    }

    fun getTicketID(userID: String): String? {
        try {
            if (!userID.isEmpty()) {
                val ticketid = getTicketSQL(userID)
                if (ticketid != null) {
                    return ticketid.channelID
                }
            }
            return null
        } catch (error: Exception) {
            error.printStackTrace()
        }
        return null
    }

    @Throws(SQLException::class)
    fun getTicketSQL(usedid: String?): TicketInfo? {
        sql.connection.use { connection ->
            val statement = "SELECT * FROM $tableName WHERE userID = ?"
            connection!!.prepareStatement(statement).use { preparedStatement ->
                preparedStatement.setString(1, usedid)
                val resultSet = preparedStatement.executeQuery()
                while (resultSet.next()) {
                    return TicketInfo(resultSet.getString("userID"), resultSet.getString("channelID"))
                }
            }
        }
        return null
    }

    @Throws(SQLException::class)
    fun getTicketbyChannel(channelID: String?): TicketInfo? {
        sql.connection.use { connection ->
            val statement = "SELECT * FROM $tableName WHERE channelID = ?"
            connection!!.prepareStatement(statement).use { preparedStatement ->
                preparedStatement.setString(1, channelID)
                val resultSet = preparedStatement.executeQuery()
                while (resultSet.next()) {
                    return TicketInfo(resultSet.getString("userID"), resultSet.getString("channelID"))
                }
            }
        }
        return null
    }

    @Throws(SQLException::class)
    fun addTicket(info: TicketInfo): Boolean {
        if (getTicketSQL(info.userID) != null) {
            return false
        }
        sql.connection.use { connection ->
            val statement = "INSERT INTO " +
                    " " + tableName + " (userID, ChannelID) " +
                    "VALUES (?, ?)"
            connection!!.prepareStatement(statement).use { preparedStatement ->
                preparedStatement.setString(1, info.userID)
                preparedStatement.setString(2, info.channelID)
                preparedStatement.execute()
                return true
            }
        }
    }

    @Throws(SQLException::class)
    fun removeTicket(info: TicketInfo): Boolean {
        if (getTicketSQL(info.userID) != null) {
            sql.connection.use { connection ->
                val statement = "DELETE FROM $tableName WHERE userID = ? AND channelID = ?"
                connection!!.prepareStatement(statement).use { preparedStatement ->
                    preparedStatement.setString(1, info.userID)
                    preparedStatement.setString(2, info.channelID)
                    preparedStatement.execute()
                }
            }
        }
        return false
    }

}