package com.neutralplasma.virtusbot.commands.ticket

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.commands.TicketCommand
import com.neutralplasma.virtusbot.storage.locale.LocaleHandler
import com.neutralplasma.virtusbot.storage.ticket.TicketStorage
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel

class DeleteTicketCMD(ticketStorage: TicketStorage, localeHandler: LocaleHandler) : TicketCommand() {
    private val ticketStorage: TicketStorage
    private val localeHandler: LocaleHandler
    override fun execute(event: CommandEvent) {
        val ticketid = ticketStorage.getTicketChannel(event.channel.id)
        if (ticketid != null) {
            sendMessage(event.textChannel, event.guild)
        }
    }

    fun sendMessage(channel: TextChannel, guild: Guild?) {
        val eb = EmbedBuilder()
        val content = localeHandler.getLocale(guild!!, "TICKET_DELETE_MESSAGE")
        val field_title = localeHandler.getLocale(guild, "TICKET_DELETE_FIELD_TITLE")
        val title = localeHandler.getLocale(guild, "TICKET_DELETE_TITLE")
        eb.setTitle(title)
        eb.addField(field_title, content, false)
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
        this.ticketStorage = ticketStorage
        this.localeHandler = localeHandler
    }
}