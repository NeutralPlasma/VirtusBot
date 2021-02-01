package com.neutralplasma.virtusbot.storage.ticket

import com.neutralplasma.virtusbot.storage.dataStorage.StorageHandler
import java.sql.SQLException
import kotlin.jvm.Throws

class TicketStorage(private val sql: StorageHandler) {
    private val tableName = "TicketData"
    fun setup() {
        try {
            sql.createTable(tableName, "userID TEXT, channelID TEXT, closed TINYINT")
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun writeSettings(userID: String, channelID: String, closed: Int) {
        val info = TicketInfo(userID, channelID, closed)
        try {
            addTicket(info)
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }
    @Throws(SQLException::class)
    fun updateTicket(info: TicketInfo){
        sql.connection.use { connection ->
            val statement = "UPDATE $tableName SET closed = ? WHERE channelID = ? AND userID = ?"
            connection!!.prepareStatement(statement).use { preparedStatement ->
                preparedStatement.setInt(1, info.closed)
                preparedStatement.setString(2, info.channelID)
                preparedStatement.setString(3, info.userID)
                preparedStatement.execute()
            }
        }
    }

    fun deleteTicket(userID: String, channelID: String, closed: Int) {
        val info = TicketInfo(userID, channelID, closed)
        try {
            removeTicket(info)
        } catch (sqlerror: SQLException) {
            sqlerror.printStackTrace()
        }
    }

    fun deleteTicket(info: TicketInfo) {
        try {
            removeTicket(info)
        } catch (sqlerror: SQLException) {
            sqlerror.printStackTrace()
        }
    }

    fun getTicketChannel(channelID: String): TicketInfo? {
        try {
            if (channelID.isNotEmpty()) {
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
                    return TicketInfo(resultSet.getString("userID"), resultSet.getString("channelID"), resultSet.getInt("closed"))
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
                    return TicketInfo(resultSet.getString("userID"), resultSet.getString("channelID"), resultSet.getInt("closed"))
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
                    " " + tableName + " (userID, ChannelID, closed) " +
                    "VALUES (?, ?, ?)"
            connection!!.prepareStatement(statement).use { preparedStatement ->
                preparedStatement.setString(1, info.userID)
                preparedStatement.setString(2, info.channelID)
                preparedStatement.setInt(3, info.closed)
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