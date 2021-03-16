package com.neutralplasma.virtusbot.commands.ticket

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.commons.utils.FinderUtil
import com.neutralplasma.virtusbot.Bot
import com.neutralplasma.virtusbot.commands.TicketCommand
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import com.neutralplasma.virtusbot.settings.SettingsList
import com.neutralplasma.virtusbot.storage.ticket.TicketInfo
import com.neutralplasma.virtusbot.storage.ticket.TicketStorage
import com.neutralplasma.virtusbot.utils.PermissionUtil
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.requests.restaction.ChannelAction
import java.awt.Color
import java.text.MessageFormat
import java.util.concurrent.TimeUnit

class CreateTicketCMD(private val ticketStorage: TicketStorage,private val bot: Bot, private val newSettingsManager: NewSettingsManager) : TicketCommand() {
    override fun execute(commandEvent: CommandEvent) {
        val member = commandEvent.member
        val guild = commandEvent.guild
        val channel = commandEvent.textChannel
        if(channel != null){
            createTicket(guild, member, channel)
        }
    }

    fun createTicket(guild: Guild, member: Member, channel: TextChannel){
        if(ticketStorage.getTicketGuild(guild.id, member.id, 0) == null){
            val category = newSettingsManager.getCategory(guild, SettingsList.OPEN_TICKET_CATEGORY)
            val newChannel = createChannel(member.user, guild, category)
            val roles = guild.getRolesByName("@everyone", true)
            val role = newSettingsManager.getRole(guild, SettingsList.SUPPORT_ROLE)
            for (r in roles) {
                try {
                    PermissionUtil.updatePermsRole(r, newChannel, false)
                } catch (ignored: IllegalStateException) {}
            }
            PermissionUtil.updatePermsMember(member, newChannel, true)
            if(role != null){
                PermissionUtil.updatePermsRole(role, newChannel, true)
                sendTicketMessage(newChannel, member, role)
                newChannel.sendMessage(role.asMention).queue { m: Message -> m.delete().queueAfter(5, TimeUnit.SECONDS) }
            }else{
                sendTicketMessage(newChannel, member, null)
            }
            newChannel.sendMessage(member.asMention).queue { m: Message -> m.delete().queueAfter(5, TimeUnit.SECONDS) }
        }else{
            channel.sendMessage("Že imaš odprt ticket!").queue()
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

        ticketStorage.createTicket(TicketInfo(user.id, channel.id, 0, guild.id))
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