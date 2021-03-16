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

class DeleteTicketCMD(private val ticketStorage: TicketStorage, private val newSettingsManager: NewSettingsManager) : TicketCommand() {
    override fun execute(commandEvent: CommandEvent) {
        val ticket = ticketStorage.getTicketChannel(commandEvent.channel.id)
        if(ticket != null){
            sendMessage(commandEvent.textChannel, commandEvent.author, ticket)
        }else{
            commandEvent.reply("Nisi v ticket kanalu.")
        }
    }

    fun sendMessage(channel: TextChannel, user: User, info: TicketInfo){

        if(info.closed != 1){
            val eb = EmbedBuilder()
            eb.addField("Error", "You must close ticket before deleting it.", false)
            channel.sendMessage(eb.build()).queue()
            return
        }
        val eb = EmbedBuilder()
        eb.addField("Confirmation", "React with ✔ to delete the ticket.", false)

        val message = channel.sendMessage(eb.build()).complete()
        message.addReaction("✔").queue()

        AbstractReactionUtil(user, {
            ticketStorage.deleteTicket(info)
            channel.delete().queue()
        }, message.jda, "✔", message.id, false)

    }


    init {
        name = "delete"
        help = "Delete ticket."
        aliases = arrayOf("deleteticket")
        guildOnly = true
    }
}