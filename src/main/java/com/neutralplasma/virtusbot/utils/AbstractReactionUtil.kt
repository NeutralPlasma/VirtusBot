package com.neutralplasma.virtusbot.utils

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*

class AbstractReactionUtil(user: User, private val handler: () -> Unit, jda: JDA, private val emoji: String, private val message: String) : ListenerAdapter() {
    var onClose: () -> Unit = {}

    private var listener: ListenerAdapter? = null
    fun initializeListeners(jda: JDA) {
        listener = object : ListenerAdapter() {
            override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
                val user = event.user ?: return
                if (!isRegistered(user)) return
                if (event.messageId != message) return
                val emote = event.reactionEmote
                if (!emote.emoji.equals(emoji, ignoreCase = true)) return

                unregister(user)
                val chatConfirmEvent = ChatConfirmEvent(user, emote.emoji)


                onClose.invoke()

                jda.removeEventListener(listener)
            }
        }
        jda.addEventListener(listener)
    }


    interface ChatConfirmHandler {
        fun onChat(event: ChatConfirmEvent?)
    }

    class ChatConfirmEvent(val user: User, private val emote: String) {

        fun getMessage(): String {
            return emote
        }

    }

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