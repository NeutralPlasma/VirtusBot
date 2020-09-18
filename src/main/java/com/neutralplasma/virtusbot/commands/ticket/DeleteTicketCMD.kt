package com.neutralplasma.virtusbot.commands.ticket

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.commands.TicketCommand
import com.neutralplasma.virtusbot.storage.ticket.TicketStorage
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel

class DeleteTicketCMD(private val ticketStorage: TicketStorage) : TicketCommand() {
    override fun execute(event: CommandEvent) {
        val ticketid = ticketStorage.getTicketChannel(event.channel.id)
        if (ticketid != null) {
            sendMessage(event.textChannel, event.guild)
        }
    }

    fun sendMessage(channel: TextChannel, guild: Guild?) {
        val eb = EmbedBuilder()
        eb.setTitle("DA")
        eb.addField("DA", "DA", false)
        channel.sendMessage(eb.build()).complete().addReaction("✔").queue()
    }

    fun deleteChannel(channel: TextChannel) {
        channel.delete().queue()
    }

    init {
        name = "delete"
        help = "Delete the ticket"
        aliases = arrayOf("deleteticket")
        arguments = "<Name|NONE>"
        guildOnly = true
    }
}