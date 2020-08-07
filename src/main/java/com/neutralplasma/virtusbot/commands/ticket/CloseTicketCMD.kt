package com.neutralplasma.virtusbot.commands.ticket

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.commands.TicketCommand
import com.neutralplasma.virtusbot.storage.locale.LocaleHandler
import com.neutralplasma.virtusbot.storage.ticket.TicketInfo
import com.neutralplasma.virtusbot.storage.ticket.TicketStorage
import com.neutralplasma.virtusbot.utils.AbstractReactionUtil
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User

class CloseTicketCMD(ticketStorage: TicketStorage, localeHandler: LocaleHandler) : TicketCommand() {
    private val ticketStorage: TicketStorage
    private val localeHandler: LocaleHandler
    override fun execute(event: CommandEvent) {
        val ticketid = ticketStorage.getTicketChannel(event.channel.id)
        if (ticketid != null) {
            event.reply("TEST")
            sendMessage(event.textChannel, event.guild, event.author, ticketid)
        } else {
            event.reply("You don't have an ticket.")
        }
    }

    fun sendMessage(channel: TextChannel, guild: Guild?, user: User?, info: TicketInfo) {
        val eb = EmbedBuilder()
        val content = localeHandler.getLocale(guild!!, "TICKET_CLOSE_MESSAGE")
        val field_title = localeHandler.getLocale(guild, "TICKET_CLOSE_FIELD_TITLE")
        val title = localeHandler.getLocale(guild, "TICKET_CLOSE_TITLE")
        eb.setTitle(title)
        eb.addField(field_title, content, false)
        val message = channel.sendMessage(eb.build()).complete()
        message.addReaction("✔").queue()
        channel.sendMessage("Test").queue()
        val reactionUtil = AbstractReactionUtil(user!!, {}, message.jda, "✔", message.id)

        reactionUtil.onClose = {
            ticketStorage.deleteTicket(info.userID, info.channelID)
            deleteChannel(channel)
        }

    }

    fun deleteChannel(channel: TextChannel) {
        channel.delete().queue()
    }

    init {
        name = "close"
        help = "Delete the ticket"
        aliases = arrayOf("closeticket")
        arguments = "<Name|NONE>"
        guildOnly = true
        this.ticketStorage = ticketStorage
        this.localeHandler = localeHandler
    }
}