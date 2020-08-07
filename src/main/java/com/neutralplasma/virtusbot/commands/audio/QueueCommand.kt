package com.neutralplasma.virtusbot.commands.audio

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.menu.Paginator
import com.neutralplasma.virtusbot.Bot
import com.neutralplasma.virtusbot.audio.AudioManager
import com.neutralplasma.virtusbot.commands.AudioCommand
import com.neutralplasma.virtusbot.utils.TextUtil.filter
import com.neutralplasma.virtusbot.utils.TextUtil.formatTiming
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.exceptions.PermissionException
import java.awt.Color
import java.util.concurrent.TimeUnit

class QueueCommand(audioManager: AudioManager, bot: Bot) : AudioCommand() {
    private val audioManager: AudioManager
    private val builder: Paginator.Builder
    override fun execute(commandEvent: CommandEvent) {
        commandEvent.message.delete().queue()
        val songsl = audioManager.getQueue(commandEvent.guild)
        val songs = arrayOfNulls<String>(songsl.size)
        var total: Long = 0
        for (i in songsl.indices) {
            val song = songsl[i]
            if(song != null) {
                total += song.duration
                songs[i] = song.info.title
            }
        }
        val fintotal = total
        if (songs.size > 0) {
            builder.setText { i1: Int?, i2: Int? -> getQueueTitle(commandEvent.client.success, songs.size, fintotal, false) }
                    .setItems(*songs)
                    .setUsers(commandEvent.author)
                    .setColor(Color.magenta.brighter())
            builder.build().paginate(commandEvent.channel, 1)
        } else {
            commandEvent.reply("Nope.")
        }
    }

    private fun getQueueTitle(success: String, songslength: Int, total: Long, repeatmode: Boolean): String {
        val sb = StringBuilder()
        return filter(sb.append(success).append(" Current Queue | ").append(songslength)
                .append(" entries | `").append(formatTiming(total)).append("` ")
                .append(if (repeatmode) "| $REPEAT" else "").toString())
    }

    companion object {
        private const val REPEAT = "\uD83D\uDD01" // 🔁
    }

    init {
        name = "queue"
        help = "Gets the current queue."
        aliases = arrayOf("q", "getqueue")
        this.audioManager = audioManager
        builder = Paginator.Builder()
                .setColumns(1)
                .setFinalAction { m: Message ->
                    try {
                        m.clearReactions().queue()
                    } catch (ignore: PermissionException) {
                    }
                }
                .setItemsPerPage(10)
                .waitOnSinglePage(false)
                .useNumberedItems(true)
                .showPageNumbers(true)
                .wrapPageEnds(true)
                .setEventWaiter(bot.waiter)
                .setTimeout(1, TimeUnit.MINUTES)
    }
}