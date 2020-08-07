package com.neutralplasma.virtusbot.commands.general

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import com.neutralplasma.virtusbot.storage.locale.LocaleHandler
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import java.awt.Color
import java.util.function.Consumer

class SuggestCmd(newSettingsManager: NewSettingsManager, localeHandler: LocaleHandler) : Command() {
    private val newSettingsManager: NewSettingsManager
    private val localeHandler: LocaleHandler
    override fun execute(event: CommandEvent) {
        val arg = event.args
        val guild = event.guild
        event.message.delete()
        val suggestChannel = newSettingsManager.getTextChannel(guild, "SUGGEST_CHANNEL")
        if (suggestChannel != null) {
            createSuggestion(suggestChannel, event.author, arg, guild)
        } else {
            val eb = EmbedBuilder()
            val field_title = localeHandler.getLocale(guild, "ERROR_FIELD_TITLE")
            val title = localeHandler.getLocale(guild, "ERROR_TITLE")
            val content = localeHandler.getLocale(guild, "ERROR_WRONG_NOCHANNEL")
            eb.setTitle(title)
            eb.addField(field_title, content, false)
            eb.setColor(Color.orange)
            event.channel.sendMessage(eb.build()).queue()
        }
    }

    fun createSuggestion(channel: TextChannel, user: User, suggestion: String, guild: Guild?) {
        val eb = EmbedBuilder()
        val field_title = localeHandler.getLocale(guild!!, "SUGGEST_FIELD_TITLE")
        val title = localeHandler.getLocale(guild, "SUGGEST_TITLE")
        val field_title2 = localeHandler.getLocale(guild, "SUGGEST_TITLE_OWNER")
        eb.setTitle(title)
        eb.addField(field_title, "`$suggestion`", false)
        eb.addField(field_title2, user.asMention, false)
        eb.setColor(Color.orange)
        val callback = Consumer { response: Message ->
            response.addReaction("\u2705").queue()
            response.addReaction("\u274C").queue()
        }
        channel.sendMessage(eb.build()).queue(callback)
    }

    init {
        name = "suggest"
        help = "Create a new suggestion"
        arguments = "<SUGGESTION>"
        guildOnly = true
        this.newSettingsManager = newSettingsManager
        this.localeHandler = localeHandler
    }
}