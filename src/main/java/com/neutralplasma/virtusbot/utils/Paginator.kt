package com.neutralplasma.virtusbot.utils

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import java.util.*
import kotlin.concurrent.timerTask

class Paginator (private val content : List<EmbedBuilder>){

    private var messageId : String = ""
    private var reactionStop : AbstractReactionUtil? = null
    private var reactionLeft : AbstractReactionUtil? = null
    private var reactionRight : AbstractReactionUtil? = null
    private var page = 0


    val BIG_LEFT = "⏪"
    val LEFT = "◀"
    val STOP = "⏹"
    val RIGHT = "▶"
    val BIG_RIGHT = "⏩"

    fun build(channel: TextChannel, user: User){
        if(content.size > 1){
            // ADD PAGINATION STUFF
            val message = channel.sendMessage(content[page].build()).complete()
            message.addReaction(LEFT).queue()
            message.addReaction(STOP).queue()
            message.addReaction(RIGHT).queue()
            messageId = message.id

            // PREV PAGE
            reactionLeft =AbstractReactionUtil(user, {
                if(page > 1) page--
                channel.editMessageById(messageId, content[page].build()).queue()
                channel.removeReactionById(messageId, LEFT, user).queue()
            }, channel.jda, LEFT, messageId, true)

            reactionStop =AbstractReactionUtil(user, {
                channel.deleteMessageById(messageId).queue()
                dispose(channel.jda)
            }, channel.jda, STOP, messageId, false)

            // NEXT PAGE
            reactionRight =AbstractReactionUtil(user, {
                if(page < content.size-1) page++
                channel.editMessageById(messageId, content[page].build()).queue()
                channel.removeReactionById(messageId, RIGHT, user).queue()
            }, channel.jda, RIGHT, messageId, true)




            Timer().schedule(timerTask {
                if(reactionStop != null) {
                    channel.deleteMessageById(messageId).queue()
                    reactionStop?.dispose(channel.jda)
                    reactionStop = null
                }
                if(reactionLeft != null) {
                    reactionLeft?.dispose(channel.jda)
                    reactionLeft = null
                }
                if(reactionRight != null) {
                    reactionRight?.dispose(channel.jda)
                    reactionRight = null
                }
            }, 50000)


        }else if(content.isNotEmpty()){
            val message = channel.sendMessage(content[0].build()).complete()
            message.addReaction(STOP).queue()
            messageId = message.id


            reactionStop =AbstractReactionUtil(user, {
                channel.deleteMessageById(messageId).queue()
                dispose(channel.jda)
            }, channel.jda, STOP, messageId, false)

            Timer().schedule(timerTask {
                if(reactionStop != null) {
                    channel.deleteMessageById(messageId).queue()
                    reactionStop?.dispose(channel.jda)
                    reactionStop = null
                }
            }, 10000)
        }
    }

    fun dispose(jda: JDA){
        reactionStop?.dispose(jda)
        reactionLeft?.dispose(jda)
        reactionRight?.dispose(jda)
        reactionLeft = null
        reactionStop = null
        reactionRight = null
    }

}