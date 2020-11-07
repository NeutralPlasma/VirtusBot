package com.neutralplasma.virtusbot.commands.ticket

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.commands.TicketCommand
import com.neutralplasma.virtusbot.storage.ticket.TicketInfo
import com.neutralplasma.virtusbot.storage.ticket.TicketStorage
import com.neutralplasma.virtusbot.utils.AbstractReactionUtil
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User

class CloseTicketCMD(private val ticketStorage: TicketStorage) : TicketCommand() {
    override fun execute(commandEvent: CommandEvent) {
        val ticketid = ticketStorage.getTicketChannel(commandEvent.channel.id)
        if (ticketid != null) {
            sendMessage(commandEvent.textChannel, commandEvent.author, ticketid)
        } else {
            commandEvent.reply("Nisi v ticket kanalu.")
        }
    }

    fun sendMessage(channel: TextChannel, user: User?, info: TicketInfo) {
        val eb = EmbedBuilder()
        eb.addField("Potrditev", "Reaktraj na sporoćilo z ✔ da zapreš ta ticket.", false)

        val message = channel.sendMessage(eb.build()).complete()
        message.addReaction("✔").queue()

        val reactionUtil = AbstractReactionUtil(user!!, {
            ticketStorage.deleteTicket(info.userID, info.channelID)
            deleteChannel(channel)
        }, message.jda, "✔", message.id, false)

    }

    fun deleteChannel(channel: TextChannel) {
        channel.delete().queue()
    }

    init {
        name = "close"
        help = "Zapri ticket."
        aliases = arrayOf("closeticket")
        arguments = "<Name|NONE>"
        guildOnly = true
    }
}