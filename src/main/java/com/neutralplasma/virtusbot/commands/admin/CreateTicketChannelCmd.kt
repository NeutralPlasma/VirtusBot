package com.neutralplasma.virtusbot.commands.admin

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.commands.AdminCommand
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import com.neutralplasma.virtusbot.storage.locale.LocaleHandler
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color

class CreateTicketChannelCmd(settingsManager: NewSettingsManager, localeHandler: LocaleHandler) : AdminCommand() {
    private val settingsManager: NewSettingsManager
    private val localeHandler: LocaleHandler
    override fun execute(commandEvent: CommandEvent) {
        commandEvent.message.delete().queue()
        val content = localeHandler.getLocale(commandEvent.guild, "TICKET_HELP_CREATE_REACT_CONTENT")
        val field_title = localeHandler.getLocale(commandEvent.guild, "TICKET_HELP_CREATE_REACT_FIELD_TITLE")
        val title = localeHandler.getLocale(commandEvent.guild, "TICKET_HELP_CREATE_REACT_FIELD_TITLE")
        val eb = EmbedBuilder()
        eb.setTitle(title)
        //eb.addField("React", "Reactaj z \uD83D\uDCAC, da ustvariš pogovor za pomoč.", false);
        eb.addField(field_title, content, false)
        eb.setColor(Color.orange)
        try {
            settingsManager.addStringData(commandEvent.guild, "TICKET_CHANNEL", commandEvent.channel.id)
        } catch (error: NullPointerException) {
            error.printStackTrace()
        }
        commandEvent.channel.sendMessage(eb.build()).complete().addReaction("\uD83D\uDCAC").queue()
    }

    init {
        name = "createticketchannel"
        help = "Creates ticket channel"
        this.settingsManager = settingsManager
        this.localeHandler = localeHandler
    }
}