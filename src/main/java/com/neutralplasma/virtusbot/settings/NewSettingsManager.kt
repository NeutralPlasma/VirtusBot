package com.neutralplasma.virtusbot.settings

import com.neutralplasma.virtusbot.storage.dataStorage.StorageHandler
import com.neutralplasma.virtusbot.utils.TextUtil.sendMessage
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import java.sql.SQLException
import java.util.*
import kotlin.jvm.Throws

class NewSettingsManager(private val storageHandler: StorageHandler) {
    private val loadedSettings = HashMap<String, NewSettings>()
    fun addStringData(guild: Guild, settingName: SettingsList, setting: String): Boolean {
        val settings = getSettings(guild)
        return try {
            setSetting(settingName, setting, guild.id, "ServerSettings", "STRING")
            settings.addStringData(settingName.toString(), setting)
            loadedSettings[guild.id] = settings
            true
        } catch (error: SQLException) {
            error.printStackTrace()
            false
        }
    }

    fun getTextChannel(guild: Guild, setting: SettingsList): TextChannel? {
        val settings = getSettings(guild)
        var channel: TextChannel?
        return try {
            if (!settings.getStringData(setting.toString()).equals("none", ignoreCase = true)) {
                channel = guild.getTextChannelById(settings.getStringData(setting.toString()))
                if (channel == null) {
                    channel = guild.getTextChannelById(settings.getLongData(setting.toString()))
                }
                return channel
            }
            null
        } catch (error: NullPointerException) {
            null
        }
    }

    fun getCategory(guild: Guild, setting: SettingsList): Category? {
        val settings = getSettings(guild)
        var category: Category?
        return try {
            if (!settings.getStringData(setting.toString()).equals("none", ignoreCase = true)) {
                category = guild.getCategoryById(settings.getStringData(setting.toString()))
                if (category == null) {
                    category = guild.getCategoryById(settings.getLongData(setting.toString()))
                }
                return category
            }
            null
        } catch (error: NullPointerException) {
            null
        }
    }

    fun getRole(guild: Guild, setting: SettingsList): Role? {
        val settings = getSettings(guild)
        var role: Role?
        return try {
            if (!settings.getStringData(setting.toString()).equals("none", ignoreCase = true)) {
                role = guild.getRoleById(settings.getStringData(setting.toString()))
                if (role == null) {
                    role = guild.getRoleById(settings.getLongData(setting.toString()))
                }
                return role
            }
            null
        } catch (error: NullPointerException) {
            null
        }
    }

    fun getVoiceChannel(guild: Guild, setting: SettingsList): VoiceChannel? {
        val settings = getSettings(guild)
        return if (!settings.getStringData(setting.toString()).equals("none", ignoreCase = true)) {
            guild.getVoiceChannelById(settings.getStringData(setting.toString()))
        } else null
    }

    fun getData(guild: Guild, setting: SettingsList): String {
        val settings = getSettings(guild)
        return settings.getStringData(setting.toString())
    }

    fun getSettings(guild: Guild): NewSettings {
        var settings = loadedSettings[guild.toString()]
        if (settings == null) {
            settings = NewSettings(HashMap(), HashMap(), HashMap())
            try {
                val dataset = getAllSettings(guild.id, "ServerSettings")
                for (string in dataset.keys) {
                    dataset[string]?.let { settings!!.addStringData(string, it) }
                }
            } catch (error: SQLException) {
                settings = NewSettings(HashMap(), HashMap(), HashMap())
            }
        }
        loadedSettings[guild.id] = settings
        return settings
    }

    fun loadSettings(jda: JDA) {
        for (guild in jda.guilds) {
            try {
                val settings = getAllSettings(guild.id, "ServerSettings")
                val settings1 = NewSettings(settings, HashMap(), HashMap())
                loadedSettings[guild.id] = settings1
            } catch (error: SQLException) {
                error.printStackTrace()
            }
        }
    }

    /*
        SQL STUFF
     */
    fun removeSetting(settingName: SettingsList, guild: String, tablename: String): Boolean {
        try {
            storageHandler.connection.use { connection ->
                val statement = "DELETE FROM $tablename WHERE guildID = ? AND settingName = ?"
                connection!!.prepareStatement(statement).use { preparedStatement ->
                    preparedStatement.setString(1, guild)
                    preparedStatement.setString(2, settingName.toString())
                    preparedStatement.execute()
                    return true
                }
            }
        } catch (error: SQLException) {
            return false
        }
    }

    @Throws(SQLException::class)
    fun getSetting(guildID: String?, settingName: SettingsList, tablename: String, type: String?): String {
        storageHandler.connection.use { connection ->
            val statement = "SELECT * FROM $tablename WHERE guildID = ? AND settingName = ? AND type = ?"
            connection!!.prepareStatement(statement).use { preparedStatement ->
                preparedStatement.setString(1, guildID)
                preparedStatement.setString(2, settingName.toString())
                preparedStatement.setString(3, type)
                val resultSet = preparedStatement.executeQuery()
                while (resultSet.next()) {
                    return resultSet.getString("data")
                }
            }
        }
        return "ERROR"
    }

    @Throws(SQLException::class)
    fun getAllSettings(guildID: String, tablename: String): HashMap<String, String> {
        val settings = HashMap<String, String>()
        storageHandler.connection.use { connection ->
            val statement = "SELECT * FROM $tablename WHERE guildID = ?"
            connection!!.prepareStatement(statement).use { preparedStatement ->
                preparedStatement.setString(1, guildID)
                val resultSet = preparedStatement.executeQuery()
                while (resultSet.next()) {
                    // To be done.
                    settings[resultSet.getString("settingName")] = resultSet.getString("data")
                }
            }
        }
        settings["TEST"] = "TEST2"
        return settings
    }

    @Throws(SQLException::class)
    fun setSetting(settingName: SettingsList, setting: String, guild: String, tablename: String, type: String?): Boolean {
        if (getSetting(guild, settingName, tablename, type) != "ERROR") {
            removeSetting(settingName, guild, tablename)
        }
        storageHandler.connection.use { connection ->
            val statement = "INSERT INTO " +
                    " " + tablename + " (data, guildID, settingName, type) " +
                    "VALUES (?, ?, ?, ?)"
            connection!!.prepareStatement(statement).use { preparedStatement ->
                preparedStatement.setString(1, setting)
                preparedStatement.setString(2, guild)
                preparedStatement.setString(3, settingName.toString())
                preparedStatement.setString(4, type)
                preparedStatement.execute()
                return true
            }
        }
    }

    init {
        try {
            storageHandler.createTable("ServerSettings",
                    "settingName TEXT," +
                            "guildID TEXT," +
                            "data TEXT," +
                            "type TEXT")
        } catch (error: SQLException) {
            sendMessage(error.message)
        }
    }
}