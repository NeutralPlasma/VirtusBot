package com.neutralplasma.virtusbot.commands.admin

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.commands.AdminCommand
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color

class CreateTicketChannelCmd(private val settingsManager: NewSettingsManager) : AdminCommand() {
    override fun execute(commandEvent: CommandEvent) {
        commandEvent.message.delete().queue()
        val eb = EmbedBuilder()
        eb.addField("Ticket creation:", "React with: \uD83D\uDCAC to create the ticket channel", false)
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
    }
}