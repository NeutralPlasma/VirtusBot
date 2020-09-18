package com.neutralplasma.virtusbot.handlers.playerSettings

import com.google.gson.Gson
import com.neutralplasma.virtusbot.storage.dataStorage.StorageHandler
import com.neutralplasma.virtusbot.utils.TextUtil.sendMessage
import net.dv8tion.jda.api.entities.User
import java.sql.SQLException
import java.util.*

class PlayerSettingsHandler(private val sql: StorageHandler) {
    private val gson = Gson()
    private val pSettings = HashMap<String, PlayerSettings>()

    private val toUpdate = HashMap<String, PlayerSettings>()
    fun pSettingsUpdater() {
        val t = Timer()
        t.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                settingsUpdater.run()
            }
        }, 100, 60000)
    }

    var settingsUpdater = Runnable {
        try {
            syncSettings()
        } catch (ignored: Exception) {
        }
    }

    @Throws(SQLException::class)
    fun syncSettings() {
        val data = HashMap(toUpdate)
        toUpdate.clear()
        sql.connection.use { connection ->
            if(connection == null) throw SQLException("Connection can't be null.")

            for (userinfo in data.keys) {
                val udata = data[userinfo]

                val statement1 = "DELETE FROM PlayerSettings WHERE userID = ?"
                connection.prepareStatement(statement1).use { preparedStatement ->
                    preparedStatement.setString(1, userinfo)
                    preparedStatement.execute()
                }


                val settings = gson.toJson(udata)
                val statement2 = "INSERT INTO PlayerSettings (" +
                        "userID," +
                        "settings) VALUES (?, ?)"
                connection.prepareStatement(statement2).use { preparedStatement ->
                    preparedStatement.setString(1, userinfo)
                    preparedStatement.setString(2, settings)
                    preparedStatement.execute()
                }
            }
        }
    }

    @Throws(SQLException::class)
    fun cachePlayerSettings() {
        sql.connection.use { connection ->
            val statement = "SELECT * from PlayerSettings;"
            connection!!.prepareStatement(statement).use { preparedStatement ->
                var amount = 0
                val resultSet = preparedStatement.executeQuery()
                while (resultSet.next()) {
                    amount++
                    try {
                        val ID = resultSet.getString("userID")
                        val settings = resultSet.getString("settings")
                        val playerSettings = gson.fromJson(settings, PlayerSettings::class.java)
                        pSettings[ID] = playerSettings
                    } catch (ignored: Exception) {
                    }
                }
                sendMessage("Loaded: $amount playerSettings from database.")
            }
        }
    }

    fun getSettings(user: User): PlayerSettings? {
        return pSettings[user.id]
    }

    /**
     *
     * @param user
     * @param playerSettings
     * Adds user if not present.
     */
    fun updateUser(user: User, playerSettings: PlayerSettings) {
        pSettings[user.id] = playerSettings
        toUpdate[user.id] = playerSettings
    }

    init {
        try {
            sql.createTable("PlayerSettings", "userID TEXT, settings TEXT")
        } catch (error: SQLException) {
            error.printStackTrace()
        }
        try {
            cachePlayerSettings()
        } catch (error: SQLException) {
            error.printStackTrace()
        }
        pSettingsUpdater()
    }
}