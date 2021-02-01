package com.neutralplasma.virtusbot.commands.ticket

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.commands.TicketCommand
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import com.neutralplasma.virtusbot.settings.SettingsList
import com.neutralplasma.virtusbot.storage.ticket.TicketInfo
import com.neutralplasma.virtusbot.storage.ticket.TicketStorage
import com.neutralplasma.virtusbot.utils.AbstractReactionUtil
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
        val ticketid = ticketStorage.getTicketChannel(commandEvent.channel.id)
        if (ticketid != null) {
            sendMessage(commandEvent.textChannel, commandEvent.author, ticketid)
        } else {
            commandEvent.reply("Nisi v ticket kanalu.")
        }
    }

    fun sendMessage(channel: TextChannel, user: User?, info: TicketInfo) {

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
        if(user != null) {
            AbstractReactionUtil(user, {
                //ticketStorage.deleteTicket(info)
                info.closed = 1
                ticketStorage.updateTicket(info)
                TextUtil.sendMessage("Updated.")
                val category = newSettingsManager.getCategory(channel.guild, SettingsList.CLOSE_TICKET_CATEGORY)
                if (category != null) {
                    channel.manager.setParent(category).queue()
                    channel.manager.setName("Closed-" + user.name.replace(" ", "-")).queue()
                }
                val owner = channel.jda.getUserById(info.userID)
                if (owner != null) {
                    val member = channel.guild.getMember(owner)
                    if (member != null) updatePerms(member, channel, false)
                }

            }, message.jda, "✔", message.id, false)
        }

    }

    fun updatePerms(member: Member, channel: TextChannel, ok: Boolean) {
        if (ok) {
            channel.createPermissionOverride(member).setAllow(
                    Permission.VIEW_CHANNEL,
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_READ,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_ATTACH_FILES,
                    Permission.MESSAGE_ADD_REACTION,
                    Permission.MESSAGE_EXT_EMOJI
            ).reason(MessageFormat.format(
                    "Added member {0}",
                    member.effectiveName
            )).queue()
        } else {
            // Remove old permission overides
            for(permission in channel.permissionOverrides){

                if(permission.member == member){ permission.delete().queue {
                    channel.createPermissionOverride(member).setDeny(
                            Permission.VIEW_CHANNEL,
                            Permission.MESSAGE_WRITE,
                            Permission.MESSAGE_READ,
                            Permission.MESSAGE_HISTORY,
                            Permission.MESSAGE_EMBED_LINKS,
                            Permission.MESSAGE_ATTACH_FILES,
                            Permission.MESSAGE_ADD_REACTION,
                            Permission.MESSAGE_EXT_EMOJI
                    ).reason(MessageFormat.format(
                            "Added member {0}",
                            member.effectiveName
                    )).queue()
                }; break;  }
            }

        }
    }


    fun deleteChannel(channel: TextChannel) {
        channel.delete().queue()
    }

    init {
        name = "close"
        help = "Close ticket."
        aliases = arrayOf("closeticket")
        arguments = "<Name|NONE>"
        guildOnly = true
    }
}