package com.neutralplasma.virtusbot.commands.admin

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.commands.AdminCommand
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import com.neutralplasma.virtusbot.storage.config.Info
import com.neutralplasma.virtusbot.storage.locale.LocaleHandler
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color

class SetSuggestCmd(newSettingsManager: NewSettingsManager, localeHandler: LocaleHandler) : AdminCommand() {
    private val newSettingsManager: NewSettingsManager
    private val localeHandler: LocaleHandler
    override fun execute(commandEvent: CommandEvent) {
        commandEvent.message.delete().queue()
        var content = localeHandler.getLocale(commandEvent.guild, "SUGGEST_CREATE_CONTENT")
        val field_title = localeHandler.getLocale(commandEvent.guild, "SUGGEST_CREATE_FIELD_TITLE")
        val title = localeHandler.getLocale(commandEvent.guild, "SUGGEST_CREATE_TITLE")
        if(content != null) {
            content = content.replace("{prefix}", Info.PREFIX!!, true)
        }
        val eb = EmbedBuilder()
        eb.setTitle(title)
        eb.addField(field_title, content, false)
        eb.setColor(Color.orange)
        try {
            newSettingsManager.addStringData(commandEvent.guild, "SUGGEST_CHANNEL", commandEvent.textChannel.id)
            //settingsManager.getSettings(commandEvent.getGuild()).setSuggestId(commandEvent.getChannel().getIdLong());
        } catch (error: NullPointerException) {
            error.printStackTrace()
        }
        commandEvent.channel.sendMessage(eb.build()).queue()
    }

    init {
        name = "setsuggest"
        help = "Sets suggestions channel"
        this.newSettingsManager = newSettingsManager
        this.localeHandler = localeHandler
    }
}