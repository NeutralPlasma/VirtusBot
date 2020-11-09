package com.neutralplasma.virtusbot.event

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.TextChannel
import java.awt.Color

object MessageHandler {

    @JvmStatic
    fun sendError(message: String, channel: TextChannel){
        val embed = EmbedBuilder()
        embed.setColor(Color.red)
        embed.addField("Error:", message, false)
        channel.sendMessage(embed.build()).queue()
    }

    @JvmStatic
    fun sendSuccess(message: String, channel: TextChannel){
        val embed = EmbedBuilder()
        embed.setColor(Color(0, 255, 85))
        embed.addField("Success:", message, false)
        channel.sendMessage(embed.build()).queue()
    }


}