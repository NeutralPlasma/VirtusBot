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
            sendMessage(event.textChannel, event.guild, event.author, ticketid)
        } else {
            event.reply("You don't have an ticket.")
        }
    }

    fun sendMessage(channel: TextChannel, guild: Guild?, user: User?, info: TicketInfo) {
        val eb = EmbedBuilder()
        eb.addField("CLOSE TICKET?", "React with ✔ to close the ticket.", false)

        val message = channel.sendMessage(eb.build()).complete()
        message.addReaction("✔").queue()

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