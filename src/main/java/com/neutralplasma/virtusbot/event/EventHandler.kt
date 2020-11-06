package com.neutralplasma.virtusbot.event

import com.neutralplasma.virtusbot.commands.ticket.CreateTicketCMD
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerLeveling
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import com.neutralplasma.virtusbot.settings.SettingsList
import com.neutralplasma.virtusbot.utils.TextUtil
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.hooks.EventListener
import java.awt.Color

class EventHandler(private val newSettingsManager: NewSettingsManager, private val createTicketCMD: CreateTicketCMD,
                   private val playerLeveling: PlayerLeveling) : EventListener {
    private var setuped = false
    override fun onEvent(gevent: GenericEvent) {
        // ADD REACTION TO MESSAGE EVENT
        if (gevent is MessageReactionAddEvent) {
            val event = gevent
            if (event.user!!.isBot) {
                return
            }
            val messageReaction = event.reaction
            val emote = messageReaction.reactionEmote
            //TextUtil.sendMessage(emote.getName());
            if (emote.name.equals("\uD83D\uDCAC", ignoreCase = true)) {
                if (event.channel === newSettingsManager.getTextChannel(event.guild, SettingsList.TICKET_CHANNEL)) {
                    messageReaction.removeReaction(event.user!!).queue()
                    createTicketCMD.createTicket(event.member!!, event.guild)
                }
            }
            // MESSAGE REACTION EVENT END
        } else if (gevent is MessageReceivedEvent) {
            val event = gevent
            val member = event.member
            val message = event.message
            if (event.isFromType(ChannelType.TEXT)) {
                if (message.contentRaw.contains("https://discord.gg/") && !member!!.hasPermission(Permission.MANAGE_SERVER)) {
                    sendServerLog(event.guild, message, member)
                    message.delete().queue()
                }
                if (event.message.isWebhookMessage || event.member!!.user.isBot) {
                    return
                }
                playerLeveling.addXp(event.member!!.user, event.guild)
            }
        } else if (gevent is GuildMemberJoinEvent){ // Member join guild
            val member = gevent.member
            val guild = gevent.guild
            val channel = newSettingsManager.getTextChannel(guild, SettingsList.WELCOME_MESSAGE_CHANNEL)
            if(channel != null){
                //channel.sendMessage("Welcome: " + member.effectiveName).queue()
                val eb = EmbedBuilder()
                eb.setColor(Color(255, 111, 0))
                eb.addField("Dobrodošel!", "Pozdravljen ${member.asMention} v VirtusRP discordu! :hugging: :confetti_ball: \n" +
                        "Preden začneš igrati si preberi pravila!", false)
                channel.sendMessage(eb.build()).queue()
            }
        }
    }

    fun sendServerLog(guild: Guild?, message: Message, member: Member?) {
        val channel = newSettingsManager.getTextChannel(guild!!, SettingsList.LOG_CHANNEL)
        val eb = EmbedBuilder()
        eb.setColor(Color.magenta.brighter())
        eb.setTitle("Opozorilo!")
        eb.addField("Informacije", """
     **Uporabnik:** ${member!!.effectiveName}
     **Text: **${message.contentRaw}
     **Pogovor: **${message.channel.name}
     **ID sporočila: **${message.id}
     """.trimIndent(), false)
        channel?.sendMessage(eb.build())?.queue()
    }

}