package com.neutralplasma.virtusbot.storage.locale

import com.google.gson.Gson
import com.neutralplasma.virtusbot.Bot
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import com.neutralplasma.virtusbot.storage.dataStorage.StorageHandler
import com.neutralplasma.virtusbot.utils.TextUtil.sendMessage
import net.dv8tion.jda.api.entities.Guild
import java.sql.SQLException
import java.util.*
import kotlin.collections.HashMap

class LocaleHandler(private val newSettingsManager: NewSettingsManager, private val sql: StorageHandler, private val bot: Bot) {
    private val stored = HashMap<String, LocaleData?>()
    val defaultLocales = HashMap<String, String>()
    private val gson = Gson()
    fun setup() {
        for (guild in bot.jda.getGuilds()) {
            try {
                var data = getServerLocale(guild.id)
                if (getServerLocale(guild.id) == null) {
                    sendMessage("Adding")
                    addServerLocale(guild.id)
                    data = getServerLocale(guild.id)
                }
                sendMessage("Added to storage: " + data!!.allLocales["TEST"])
                stored[guild.id] = data
            } catch (error: SQLException) {
                sendMessage("Error while loading server locale: " + error.message)
            }
        }
        defaultLocales["TICKET_HELP_CREATE_REACT_CONTENT"] = "`React with \uD83D\uDCAC to create the ticket.`"
        defaultLocales["TICKET_HELP_CREATE_REACT_TITLE "] = "Ticket Creation."
        defaultLocales["TICKET_HELP_CREATE_REACT_FIELD_TITLE"] = "React with \uD83D\uDCAC to create the ticket."
        defaultLocales["SUGGEST_CREATE_CONTENT"] = "`Type {prefix}suggest <suggestion> - to create new suggestion.`"
        defaultLocales["SUGGEST_CREATE_FIELD_TITLE"] = "Suggestion"
        defaultLocales["SUGGEST_CREATE_TITLE"] = "Suggestion"
        defaultLocales["VOTE_TITLE"] = "Vote"
        defaultLocales["VOTE_FIELD_TITLE"] = "Vote info:"
        defaultLocales["ERROR_FIELD_TITLE"] = "ERROR"
        defaultLocales["ERROR_TITLE"] = "ERROR"
        defaultLocales["ERROR_WRONG_CHANNEL"] = "Wrong channel use: {channel}"
        defaultLocales["ERROR_WRONG_NOCHANNEL"] = "There is no channel set for this command contact administrator."
        defaultLocales["SUGGEST_TITLE_OWNER "] = "Suggestor:"
        defaultLocales["SUGGEST_TITLE "] = "Suggestion"
        defaultLocales["SUGGEST_FIELD_TITLE "] = "Suggestion text:"
        defaultLocales["TICKET_CREATE_MESSAGE"] = "Creating channel please wait..."
        defaultLocales["TICKET_INFO_MESSAGE"] = "`Hello! We have create ticket channel for you, please wait for support team to come and help you.`"
        defaultLocales["TICKET_INFO_FIELD_TITLE"] = "Ticket"
        defaultLocales["TICKET_DELETE_TITLE"] = "Notification"
        defaultLocales["TICKET_DELETE_FIELD_TITLE"] = "Delete"
        defaultLocales["TICKET_DELETE_MESSAGE"] = "`Are you sure you want to delete this ticket?`"
        defaultLocales["SUGGEST_TITLE"] = "Suggestion"
        defaultLocales["SUGGEST_FIEL D_TITLE"] = "User suggestion:"
        defaultLocales["SUGGEST_TITLE_OWNER"] = "Suggested by:"
    }

    fun getLocale(guild: Guild, locale: String?): String? {
        return if (stored[guild.id]!!.getLocale(locale) == null) {
            getDefault(locale)
        } else stored[guild.id]!!.getLocale(locale)
    }

    fun getDefault(locale: String?): String? {
        return if (defaultLocales[locale] != null) {
            defaultLocales[locale]
        } else locale
    }

    fun updateLocale(guild: Guild, locale: String, localedata: String) {
        val data = stored[guild.id]
        data!!.updateLocale(locale, localedata)
        stored[guild.id] = data
        val allLocales = data.allLocales
        try {
            updateGuildLocales(guild.id, allLocales)
        } catch (error: SQLException) {
            sendMessage("Could not update guild locales")
        }
    }

    /*
        SQL STUFF
     */
    @Throws(SQLException::class)
    fun getServerLocale(guildid: String?): LocaleData? {
        sql.connection.use { connection ->
            val statement = "SELECT localedata FROM serverlocales WHERE guildID = ?"
            connection!!.prepareStatement(statement).use { preparedStatement ->
                preparedStatement.setString(1, guildid)
                val resultSet = preparedStatement.executeQuery()
                while (resultSet.next()) {
                    return LocaleData(gson.fromJson(resultSet.getString("localedata"), HashMap::class.java) as java.util.HashMap<String, String>);
                }
            }
        }
        return null
    }

    @Throws(SQLException::class)
    fun addServerLocale(guildid: String?): Boolean {
        sql.connection.use { connection ->
            val statement = "INSERT INTO serverlocales (guildID, localedata) VALUES (?, ?)"
            connection!!.prepareStatement(statement).use { preparedStatement ->
                preparedStatement.setString(1, guildid)
                val data = HashMap<String, String>()
                data["TEST"] = "This is setup message"
                val compileddata = gson.toJson(data)
                preparedStatement.setString(2, compileddata)
                preparedStatement.execute()
            }
        }
        return true
    }

    @Throws(SQLException::class)
    fun updateGuildLocales(guildid: String?, data: HashMap<String, String>) {
        val json = gson.toJson(data)
        sql.connection.use { connection ->
            val statement = "UPDATE serverlocales " +
                    "SET localedata = ? WHERE guildID = ?"
            connection!!.prepareStatement(statement).use { preparedStatement ->
                preparedStatement.setString(1, json)
                preparedStatement.setString(2, guildid)
                preparedStatement.execute()
            }
        }
    }

    init {
        try {
            sql.createTable("serverlocales",
                    "guildID TEXT, " +
                            "localedata TEXT")
        } catch (error: SQLException) {
            error.printStackTrace()
        }
    }
}