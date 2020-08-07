package com.neutralplasma.virtusbot.utils

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*

class AbstractChatUtil(user: User, private val function: (ChatConfirmEvent) -> Unit, jda: JDA) : ListenerAdapter() {
    var onClose: () -> Unit = {}


    private var listener: ListenerAdapter? = null

    private fun initializeListeners(jda: JDA) {
        listener = object : ListenerAdapter() {
            override fun onMessageReceived(event: MessageReceivedEvent) {
                val user = event.author
                if (!isRegistered(user)) return
                unregister(user)

                val chatConfirmEvent = ChatConfirmEvent(user, event.message.contentRaw)

                function(chatConfirmEvent)
                onClose()
                jda.removeEventListener(listener)
            }
        }
        jda.addEventListener(listener)
    }


    class ChatConfirmEvent(val user: User, val message: String)


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