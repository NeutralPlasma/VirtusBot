package com.neutralplasma.virtusbot.fivem

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import org.json.JSONArray
import org.json.JSONObject
import java.awt.Color
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.Exception
import kotlin.concurrent.scheduleAtFixedRate




class DataUpdater(val bot: JDA) {
    val servers = mutableListOf<ServerInfo>()
    var timer: TimerTask? = null

    fun start(){
        timer = Timer("ServerUpdater", true).scheduleAtFixedRate(TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(10)) {
            try {
                for (server in servers) {
                    update(server)
                }
            }catch (ignored: Exception) {}
        }
    }

    fun end(){
        timer?.cancel()
    }

    fun addServer(serverInfo: ServerInfo){
        servers.add(serverInfo)
    }

    fun update(info: ServerInfo){
        val url = URL("http://${info.ip}:${info.port}/players.json")
        val urlConnection = url.openConnection() as HttpURLConnection

        try {
            val text = urlConnection.inputStream.bufferedReader().readText()
            val data = JSONArray(text)
            info.players.clear()
            for (player in data) {
                player as JSONObject
                info.players.add(player.getString("name"))
            }

            if(info.messageID != 0L){
                val embed = EmbedBuilder()
                embed.addField("Server IP: ", info.connectIP , false)

                embed.addField("Kako se pridružite strežniku?", "Strežniku se pridružite tako, da vnesete `connect ${info.connectIP}` v F8", false)

                embed.addField("Igralci (${info.players.size}/${info.maxPlayers}): ", info.getPlayers(), false)

                embed.setFooter("Posodobljeno: " + LocalTime.now())
                embed.setColor(Color.decode("#ffa500"))
                val guild = bot.getGuildById(info.guild)
                if(guild != null ){
                    val channel = bot.getTextChannelById(info.channel)

                    channel?.retrieveMessageById(info.messageID)?.queue ({
                        it.editMessage(embed.build()).queue()
                    }) {
                        channel.deleteMessagesByIds(listOf(info.messageID.toString())).queue()
                        info.messageID = 0L
                    }


                }
            }else{
                val embed = EmbedBuilder()
                embed.addField("Server IP: ", info.connectIP, false)

                embed.addField("Kako se pridružite strežniku?", "Strežniku se pridružite tako, da vnesete `connect ${info.connectIP}` v F8", false)

                embed.addField("Igralci (${info.players.size}/${info.maxPlayers}): ", info.getPlayers(), false)
                embed.setFooter("Posodobljeno: " + LocalTime.now())
                embed.setColor(Color.decode("#ffa500"))
                val guild = bot.getGuildById(info.guild)
                if(guild != null ){
                    val channel = bot.getTextChannelById(info.channel)

                    channel?.sendMessage(embed.build())?.queue {
                        info.messageID = it.idLong
                    }
                }
            }
        } catch (error: Exception){
            urlConnection.disconnect()
        } finally {
            urlConnection.disconnect()
        }
    }
}