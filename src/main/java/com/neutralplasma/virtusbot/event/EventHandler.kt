package com.neutralplasma.virtusbot.event

import com.neutralplasma.virtusbot.commands.ticket.CreateTicketCMD
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerLeveling
import com.neutralplasma.virtusbot.handlers.timedSending.TimedSender
import com.neutralplasma.virtusbot.roleAutomation.ReactionRoleHandler
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import com.neutralplasma.virtusbot.settings.SettingsList
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent
import net.dv8tion.jda.api.hooks.EventListener
import java.awt.Color

class EventHandler(private val newSettingsManager: NewSettingsManager, private val createTicketCMD: CreateTicketCMD,
                   private val playerLeveling: PlayerLeveling, private val reactionRoleHandler: ReactionRoleHandler,
                    private val timed: TimedSender) : EventListener {
    private var setuped = false

    private val blackListed = mutableListOf("804035274409377863")

    override fun onEvent(gevent: GenericEvent) {
        // ADD REACTION TO MESSAGE EVENT
        if (gevent is MessageReactionAddEvent) {
            val event = gevent
            if (event.user!!.isBot) {
                return
            }
            val messageReaction = event.reaction
            val emote = messageReaction.reactionEmote

            // Role handler
//            TextUtil.sendMessage(emote.toString())
            if(event.member != null)
                reactionRoleHandler.addRoles(event.guild, emote, event.messageId, event.member)

//            if(event.messageId == "807255601079910420"){
//                event.retrieveMessage().queue {
//                    it.addReaction(emote.emoji).queue()
//                }
//            }


            //TextUtil.sendMessage(emote.getName());
            if (emote.name.equals("\uD83D\uDCAC", ignoreCase = true)) {
                if (event.channel === newSettingsManager.getTextChannel(event.guild, SettingsList.TICKET_CHANNEL)) {
                    messageReaction.removeReaction(event.user!!).queue()
                    createTicketCMD.createTicket(event.guild, event.member!!, event.textChannel)
                }
            }
            // MESSAGE REACTION EVENT END
        } else if (gevent is MessageReceivedEvent && !blackListed.contains(gevent.guild.id)) {
            val member = gevent.member
            val message = gevent.message
            if (gevent.isFromType(ChannelType.TEXT)) {
                if (message.contentRaw.contains("https://discord.gg/") && !member!!.hasPermission(Permission.MANAGE_SERVER)) {
                    sendServerLog(gevent.guild, message, member)
                    message.delete().queue()
                }
                if (gevent.message.isWebhookMessage || gevent.member!!.user.isBot) {
                    return
                }
                if(newSettingsManager.getTextChannel(gevent.guild, SettingsList.TIMED_MESSAGES_CHANNEL)?.idLong == gevent.channel.idLong){
                    val message = gevent.message
                    timed.addTimedMessage(gevent.guild, message.contentRaw, message.attachments[0].url)
                    gevent.textChannel.sendMessage("Added data to queue..").queue()
                }

                playerLeveling.addXp(gevent.member!!.user, gevent.guild)
            }
        } else if (gevent is MessageReceivedEvent) {
            // Recievevjawnfjoaw event

            if(newSettingsManager.getTextChannel(gevent.guild, SettingsList.TIMED_MESSAGES_CHANNEL)?.idLong == gevent.channel.idLong){
                val message = gevent.message
                timed.addTimedMessage(gevent.guild, message.contentRaw, message.attachments[0].url)
                gevent.textChannel.sendMessage("Added data to queue..").queue()
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
        } else if (gevent is MessageReactionRemoveEvent){
            if (gevent.user!!.isBot) {
                return
            }
            val messageReaction = gevent.reaction
            val emote = messageReaction.reactionEmote
            // Role handler
//            TextUtil.sendMessage(emote.toString())
            if(gevent.member != null)
                reactionRoleHandler.removeRoles(gevent.guild, emote, gevent.messageId, gevent.member)
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