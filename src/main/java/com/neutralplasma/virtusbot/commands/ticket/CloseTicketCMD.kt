package com.neutralplasma.virtusbot.commands.ticket

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.commands.TicketCommand
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import com.neutralplasma.virtusbot.settings.SettingsList
import com.neutralplasma.virtusbot.storage.ticket.TicketInfo
import com.neutralplasma.virtusbot.storage.ticket.TicketStorage
import com.neutralplasma.virtusbot.utils.AbstractReactionUtil
import com.neutralplasma.virtusbot.utils.PermissionUtil
import com.neutralplasma.virtusbot.utils.TextUtil
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import java.text.MessageFormat

class CloseTicketCMD(private val ticketStorage: TicketStorage, private val newSettingsManager: NewSettingsManager) : TicketCommand() {
    override fun execute(commandEvent: CommandEvent) {
        val ticket = ticketStorage.getTicketChannel(commandEvent.channel.id)
        if(ticket != null){
            sendMessage(commandEvent.textChannel, commandEvent.author, ticket)
        }else{
            commandEvent.reply("Nisi v ticket kanalu.")
        }
    }

    fun sendMessage(channel: TextChannel, user: User, info: TicketInfo){

        if(info.closed == 1){
            val eb = EmbedBuilder()
            eb.addField("Error", "Already closed...", false)
            channel.sendMessage(eb.build()).queue()
            return
        }
        val eb = EmbedBuilder()
        eb.addField("Confirmation", "React with ✔ to close the ticket.", false)

        val message = channel.sendMessage(eb.build()).complete()
        message.addReaction("✔").queue()

        AbstractReactionUtil(user, {
            info.closed = 1
            ticketStorage.closeTicket(info)
            TextUtil.sendMessage("Updated.")
            val category = newSettingsManager.getCategory(channel.guild, SettingsList.CLOSE_TICKET_CATEGORY)
            if (category != null) {
                channel.manager.setParent(category).queue()
                channel.manager.setName("closed-" + channel.name).queue()
            }
            val owner = channel.jda.getUserById(info.userID)
            if (owner != null) {
                val member = channel.guild.getMember(owner)
                if (member != null) PermissionUtil.updatePermsMember(member, channel, false)
            }
            message.delete().queue()

        }, message.jda, "✔", message.id, false)

    }


    init {
        name = "close"
        help = "Close ticket."
        aliases = arrayOf("closeticket")
        arguments = "<Name|NONE>"
        guildOnly = true
    }
}