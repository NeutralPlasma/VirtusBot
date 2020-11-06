package com.neutralplasma.virtusbot.commands.ticket

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.commons.utils.FinderUtil
import com.neutralplasma.virtusbot.Bot
import com.neutralplasma.virtusbot.commands.TicketCommand
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import com.neutralplasma.virtusbot.settings.SettingsList
import com.neutralplasma.virtusbot.storage.ticket.TicketStorage
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.requests.restaction.ChannelAction
import java.awt.Color
import java.text.MessageFormat
import java.util.concurrent.TimeUnit

class CreateTicketCMD(private val ticketStorage: TicketStorage,private val bot: Bot, private val newSettingsManager: NewSettingsManager) : TicketCommand() {
    override fun execute(commandEvent: CommandEvent) {
        if (ticketStorage.getTicketChannel(commandEvent.author.id) == null) {
            commandEvent.reply("Creating the ticket...")
            createTicket(commandEvent.member, commandEvent.guild)
        } else {
            commandEvent.reply("Trenutno že imaš odprti ticket.")
        }
    }

    fun createTicket(member: Member, guild: Guild) {
        if (ticketStorage.getTicketID(member.user.id) == null) {
            val category = newSettingsManager.getCategory(guild, SettingsList.OPEN_TICKET_CATEGORY)
            val newChannel = createChannel(member.user, guild, category)

            val roles = guild.getRolesByName("@everyone", true)
            val role = newSettingsManager.getRole(guild, SettingsList.SUPPORT_ROLE)
            for (r in roles) {
                try {
                    updatePerms(r, newChannel, false)
                } catch (ignored: IllegalStateException) {
                }
            }
            updatePerms(member, newChannel, true)
            if (role != null) {
                updatePerms(role, newChannel, true)
                sendTicketMessage(newChannel, member, role)
                newChannel.sendMessage(role.asMention).queue { m: Message -> m.delete().queueAfter(5, TimeUnit.SECONDS) }
            } else {
                sendTicketMessage(newChannel, member, null)
            }

            newChannel.sendMessage(member.asMention).queue { m: Message -> m.delete().queueAfter(5, TimeUnit.SECONDS) }
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
        }
    }

    fun updatePerms(owner: Role?, channel: TextChannel, ok: Boolean) {
        if (ok) {
            channel.createPermissionOverride(owner!!).setAllow(
                    Permission.VIEW_CHANNEL,
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_READ,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_ATTACH_FILES,
                    Permission.MESSAGE_ADD_REACTION,
                    Permission.MESSAGE_EXT_EMOJI
            ).reason(MessageFormat.format(
                    "Added member {0}", "DD"
            )).queue()
        } else {
            channel.createPermissionOverride(owner!!).setDeny(
                    Permission.VIEW_CHANNEL,
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_READ,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_ATTACH_FILES,
                    Permission.MESSAGE_ADD_REACTION,
                    Permission.MESSAGE_EXT_EMOJI
            ).reason(MessageFormat.format(
                    "Added member {0}", "DD"
            )).queue()
        }
    }

    fun sendTicketMessage(channel: TextChannel, member: Member, supportRole: Role?) {
        val eb = EmbedBuilder()
        if (supportRole != null) {
            eb.addField("Ticket:", "Pozdravljen kako ti lahko pomagamo? Prosim navedi čim več informacij da ti lahko čim hiteje pomagamo!" + member.asMention + supportRole.asMention, false)
        } else {
            eb.addField("Ticket:", "Pozdravljen kako ti lahko pomagamo? Prosim navedi čim več informacij da ti lahko čim hiteje pomagamo!" + member.asMention
                    , false)
        }
        eb.setColor(Color.RED)
        channel.sendMessage(eb.build()).queue()
    }

    fun createChannel(user: User, guild: Guild, category : net.dv8tion.jda.api.entities.Category?): TextChannel {
        val newchannel: ChannelAction<*> = guild.createTextChannel(user.id, category).setName(user.name)
        val channel = newchannel.complete() as TextChannel
        val id = channel.id
        val userid = user.id
        ticketStorage.writeSettings(userid, id)
        return channel
    }

    init {
        name = "create"
        help = "Create new ticket"
        aliases = arrayOf("new", "support")
        arguments = "<Name|NONE>"
        guildOnly = true
    }
}