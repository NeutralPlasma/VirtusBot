package com.neutralplasma.virtusbot.storage.ticket

import com.neutralplasma.virtusbot.storage.dataStorage.StorageHandler
import java.sql.SQLException
import kotlin.jvm.Throws

class TicketStorage(private val sql: StorageHandler) {
    private val tableName = "TicketStorage"
    fun setup() {
        try {
            sql.createTable(tableName, "userID TEXT, channelID TEXT, guildID TEXT, closed TINYINT")
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun createTicket(info: TicketInfo): Boolean{
        if(getTicket(info) == null){
            sql.connection.use { connection ->
                val statement = "INSERT INTO $tableName (userID, ChannelID, guildID, closed) " +
                        "VALUES (?, ?, ?, ?)"
                connection?.prepareStatement(statement)?.use {
                    it.setString(1, info.userID)
                    it.setString(2, info.channelID)
                    it.setString(3, info.guildID)
                    it.setInt(4, info.closed)
                    it.execute()
                    return true
                }
            }
        }
        return false
    }

    fun deleteTicket(info: TicketInfo): Boolean{
        if(getTicket(info) != null){
            sql.connection.use {
                val statement = "DELETE FROM $tableName WHERE userID = ? AND channelID = ? AND guildID = ? AND closed = ?"
                it?.prepareStatement(statement)?.use { st ->
                    st.setString(1, info.userID)
                    st.setString(2, info.channelID)
                    st.setString(3, info.guildID)
                    st.setInt(4, info.closed)
                    st.execute()
                }
            }
        }
        return false
    }

    fun getTicket(info: TicketInfo): TicketInfo?{
        sql.connection.use {
            val statement = "SELECT * FROM $tableName WHERE userID = ? AND channelID = ? AND guildID = ? AND closed = ?"
            it?.prepareStatement(statement)?.use {st ->
                st.setString(1, info.userID)
                st.setString(2, info.channelID)
                st.setString(3, info.guildID)
                st.setInt(4, info.closed)
                val resultSet = st.executeQuery()
                while (resultSet.next()) {
                    return TicketInfo(
                        resultSet.getString("userID"),
                        resultSet.getString("channelID"),
                        resultSet.getInt("closed"),
                        resultSet.getString("guildID"))
                }
            }
        }
        return null
    }
    fun getTicketGuild(guildID: String, userID: String): TicketInfo?{
        sql.connection.use {
            val statement = "SELECT * FROM $tableName WHERE userID = ? AND guildID = ?"
            it?.prepareStatement(statement)?.use {st ->
                st.setString(1, userID)
                st.setString(2, guildID)
                val resultSet = st.executeQuery()
                while (resultSet.next()) {
                    return TicketInfo(
                        resultSet.getString("userID"),
                        resultSet.getString("channelID"),
                        resultSet.getInt("closed"),
                        resultSet.getString("guildID"))
                }
            }
        }
        return null
    }

    fun getTicketGuild(guildID: String, userID: String, closed: Int): TicketInfo?{
        sql.connection.use {
            val statement = "SELECT * FROM $tableName WHERE userID = ? AND guildID = ? AND closed = ?"
            it?.prepareStatement(statement)?.use {st ->
                st.setString(1, userID)
                st.setString(2, guildID)
                st.setInt(3, closed)
                val resultSet = st.executeQuery()
                while (resultSet.next()) {
                    return TicketInfo(
                        resultSet.getString("userID"),
                        resultSet.getString("channelID"),
                        resultSet.getInt("closed"),
                        resultSet.getString("guildID"))
                }
            }
        }
        return null
    }

    fun getTicketChannel(channelID: String, userID: String): TicketInfo?{
        sql.connection.use {
            val statement = "SELECT * FROM $tableName WHERE userID = ? AND channelID = ?"
            it?.prepareStatement(statement)?.use {st ->
                st.setString(1, userID)
                st.setString(2, channelID)
                val resultSet = st.executeQuery()
                while (resultSet.next()) {
                    return TicketInfo(
                        resultSet.getString("userID"),
                        resultSet.getString("channelID"),
                        resultSet.getInt("closed"),
                        resultSet.getString("guildID"))
                }
            }
        }
        return null
    }

    fun getTicketChannel(channelID: String): TicketInfo?{
        sql.connection.use {
            val statement = "SELECT * FROM $tableName WHERE channelID = ?"
            it?.prepareStatement(statement)?.use {st ->
                st.setString(1, channelID)
                val resultSet = st.executeQuery()
                while (resultSet.next()) {
                    return TicketInfo(
                        resultSet.getString("userID"),
                        resultSet.getString("channelID"),
                        resultSet.getInt("closed"),
                        resultSet.getString("guildID"))
                }
            }
        }
        return null
    }

    fun closeTicket(info: TicketInfo){
        sql.connection.use {
            val statement = "UPDATE $tableName SET closed = ? WHERE channelID = ? AND userID = ? AND guildID = ?"
            it?.prepareStatement(statement)?.use { st ->
                st.setInt(1, 1)
                st.setString(2, info.channelID)
                st.setString(3, info.userID)
                st.setString(4, info.guildID)
                st.execute()
            }
        }
    }
}