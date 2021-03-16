package com.neutralplasma.virtusbot.handlers.timedSending

import com.google.gson.Gson
import com.neutralplasma.virtusbot.Bot
import com.neutralplasma.virtusbot.storage.dataStorage.StorageHandler
import com.neutralplasma.virtusbot.utils.TextUtil
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel
import java.awt.Image
import java.io.File
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.scheduleAtFixedRate
import javax.imageio.ImageIO


class TimedSender(val storage: StorageHandler,val bot: Bot) {
    val messagesToSend: MutableList<TimedMessageData> = mutableListOf()
    val timedChannels: MutableMap<Long, MutableList<TimedRole>> = mutableMapOf()
    var gson = Gson()

    val tableName = "TimedMessages"
    val tableName2 = "TimedChannels"
    var timer: TimerTask? = null

    init {

    }

    fun setup(){
        // Create table.
        try {
            storage.execute(
                "CREATE TABLE IF NOT EXISTS $tableName(" +
                        " `ID` INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " `guild` BIGINT NOT NULL ," +
                        " `message` LONGTEXT NOT NULL ," +
                        " `files` TEXT NULL DEFAULT NULL ," +
                        " `time_created` BIGINT NOT NULL ," +
                        " `has_sent_all` INT NOT NULL ," +
                        " `sent_to` LONGTEXT NOT NULL" +
                        ")"
            )

            "CREATE TABLE `comics`. ( `ID` INT NOT NULL AUTO_INCREMENT , `guild` BIGINT NOT NULL , `channel` BIGINT NOT NULL , `delay` BIGINT NULL DEFAULT NULL , PRIMARY KEY (`ID`)"

            storage.execute(
                "CREATE TABLE IF NOT EXISTS $tableName2(" +
                        "`ID` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "`guild` BIGINT NOT NULL, " +
                        "`channel` BIGINT NOT NULL, " +
                        "`delay` BIGINT NOT NULL" +
                        ")"
            )

        } catch (error: Exception) {
            error.printStackTrace()
        }

        storage.connection.use { connection ->
            val statement = "SELECT * FROM $tableName WHERE has_sent_all = ?"
            connection?.prepareStatement(statement)?.use {
                it.setInt(1, 0)
                val resultSet = it.executeQuery()
                while (resultSet.next()) {
                    try {
                        val message = TimedMessageData(
                            resultSet.getLong("time_created"),
                            resultSet.getLong("guild"),
                            resultSet.getString("message"),
                            resultSet.getString("files"),
                            resultSet.getInt("has_sent_all") == 1,
                            gson.fromJson(resultSet.getString("sent_to"), MutableList::class.java) as MutableList<String>
                        )
                        messagesToSend.add(message)
                    } catch (ignored: Exception) {
                    }
                }
            }
        }
        // Cache all channels where to send messages.
        storage.connection.use { connection ->
            val statement = "SELECT * FROM $tableName2 "
            connection?.prepareStatement(statement)?.use {
                val resultSet = it.executeQuery()
                while (resultSet.next()) {
                    try {
                        val guild = resultSet.getLong("guild")
                        val channel = TimedRole(
                            resultSet.getLong("delay"),
                            resultSet.getLong("channel"),
                        )
                        if(timedChannels[guild] != null) {
                            timedChannels[guild]?.add(channel)
                        }else{
                            timedChannels[guild] = mutableListOf(channel)
                        }
                    } catch (ignored: Exception) {
                    }
                }
            }
        }
        // Checking task
        timer = Timer("ServerUpdater", true).scheduleAtFixedRate(TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(10)) {
            try {
                checkAndSend()
            }catch (ignored: Exception) {}
        }
    }

    fun addChannel(guild: Guild,channel: TextChannel, delay: Long){
        val channelData = TimedRole(delay, channel.idLong)
        storage.connection.use { connection ->
            val statement = "INSERT INTO $tableName2(guild, channel, delay) VALUES(?,?,?)"
            connection?.prepareStatement(statement)?.use {
                it.setLong(1, guild.idLong)
                it.setLong(2, channel.idLong)
                it.setLong(3, delay)
                it.execute()
            }
        }
        if(timedChannels[guild.idLong] != null) {
            timedChannels[guild.idLong]?.add(channelData)
        }else{
            timedChannels[guild.idLong] = mutableListOf(channelData)
        }
    }

    fun getChannel(guild: Guild,tchannel: TextChannel): TimedRole?{
        if(timedChannels[guild.idLong] != null) {
            for(channel in timedChannels[guild.idLong]!!){
                if(channel.channelID == tchannel.idLong){
                    return channel
                }
            }
        }
        return null
    }

    fun removeChannel(guild: Guild,channel: TextChannel): Boolean{
        val channelData = getChannel(guild, channel)
        if(channelData != null) {
            storage.connection.use { connection ->
                val statement = "DELETE FROM $tableName2 WHERE guild = ? AND channel = ?"
                connection?.prepareStatement(statement)?.use {
                    it.setLong(1, guild.idLong)
                    it.setLong(2, channel.idLong)
                    it.execute()
                }
            }

            if(timedChannels[guild.idLong] != null) {
                timedChannels[guild.idLong]?.remove(channelData)
            }
            return true
        }
        return false
    }

    fun addTimedMessage(guild: Guild, message: String, file: String){
        val messageData = TimedMessageData(System.currentTimeMillis(),
        guild.idLong, message, file, false, mutableListOf())

        storage.connection.use { connection ->
            val statement = "INSERT INTO $tableName(guild, message, files, time_created, has_sent_all, sent_to) VALUES(?,?,?,?,?,?)"
            connection?.prepareStatement(statement)?.use {
                it.setLong(1, guild.idLong)
                it.setString(2, messageData.message)
                it.setString(3, messageData.filePath)
                it.setLong(4, messageData.createdOn)
                it.setInt(5, 0)
                it.setString(6, gson.toJson(messageData.sentTo))
                it.execute()
            }
        }
        messagesToSend.add(messageData)
    }

    fun updateTimedMessage(timedMessageData: TimedMessageData){

        TextUtil.sendMessage(gson.toJson(timedMessageData.sentTo))

        storage.connection.use { connection ->
            val statement = "UPDATE $tableName SET sent_to = ?, has_sent_all = ? WHERE guild = ? AND time_created = ?"
            connection?.prepareStatement(statement)?.use {
                it.setString(1, gson.toJson(timedMessageData.sentTo))
                it.setInt(2, if (timedMessageData.sent) 1 else 0)
                it.setLong(3, timedMessageData.guildID)
                it.setLong(4, timedMessageData.createdOn)
                it.execute()
            }
        }
    }


    fun checkAndSend(){
        for(message in messagesToSend){
            if(timedChannels[message.guildID] != null) {
                for (channel in timedChannels[message.guildID]!!){
//                    TextUtil.sendMessage("Channel: ${channel.delay} | ${( message.createdOn + channel.delay) - System.currentTimeMillis()}  | ${message.sentTo.contains(channel.channelID.toString())}")
                    if(( message.createdOn + channel.delay) - System.currentTimeMillis() <= 0  && !message.sentTo.contains(channel.channelID.toString())){
                        var image: Image? = null
                        var file: File? = File("image.png")
                        try {
                            val url = URL(message.filePath)
                            image = ImageIO.read(url)
                            ImageIO.write(image, "png", file)
                        }catch (ignored: java.lang.Exception) {
                            file = null
                        }
                        if(file != null){
                            if(message.message == "" || message.message.isEmpty() || message.message.isBlank()){
                                bot.jda.getGuildById(message.guildID)?.getTextChannelById(channel.channelID)?.sendFile(file)?.queue()
                            }else{
                                bot.jda.getGuildById(message.guildID)?.getTextChannelById(channel.channelID)?.sendMessage(message.message)?.addFile(file)?.queue()
                            }


                            message.sentTo.add(channel.channelID.toString())
                            message.sent = message.sentTo.size >= timedChannels[message.guildID]!!.size
                            updateTimedMessage(message)
                        }else{
                            bot.jda.getGuildById(message.guildID)?.getTextChannelById(channel.channelID)?.sendMessage(message.message)?.queue()
                            message.sentTo.add(channel.channelID.toString())
                            message.sent = message.sentTo.size >= timedChannels[message.guildID]!!.size
                            updateTimedMessage(message)
                        }
                    }
                }
            }
        }
    }

}
