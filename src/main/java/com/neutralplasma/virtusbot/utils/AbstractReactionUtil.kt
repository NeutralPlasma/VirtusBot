package com.neutralplasma.virtusbot.utils

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*

class AbstractReactionUtil(user: User, private val function: (ChatConfirmEvent) -> Unit, jda: JDA, private val emoji: String, private val message: String, private val repeat: Boolean) : ListenerAdapter() {

    private var listener: ListenerAdapter? = null
    private fun initializeListeners(jda: JDA) {
        listener = object : ListenerAdapter() {
            override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
                val user = event.user ?: return
                if (!isRegistered(user)) return
                if (event.messageId != message) return
                if (!event.reactionEmote.asReactionCode.equals(emoji, ignoreCase = true)) return
                if(!repeat) unregister(user)

                val chatConfirmEvent = ChatConfirmEvent(user)
                function(chatConfirmEvent)


                if(!repeat) jda.removeEventListener(listener)
            }
        }
        jda.addEventListener(listener)
    }

    fun dispose(jda: JDA){
        jda.removeEventListener(listener)
    }

    class ChatConfirmEvent(val user: User)

    companion object {
        private val registered: MutableList<String> = ArrayList()
        fun isRegistered(player: User): Boolean {
            return registered.contains(player.id)
        }

        fun unregister(player: User): Boolean {
            return registered.remove(player.id)
        }
    }

    init {
        initializeListeners(jda)
        registered.add(user.id)
    }
}