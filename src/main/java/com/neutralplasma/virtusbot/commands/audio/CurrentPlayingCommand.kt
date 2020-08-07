package com.neutralplasma.virtusbot.commands.audio

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.audio.AudioManager
import com.neutralplasma.virtusbot.commands.AudioCommand
import com.neutralplasma.virtusbot.utils.TextUtil.formatTiming
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import java.awt.Color
import java.util.concurrent.TimeUnit

class CurrentPlayingCommand(audioManager: AudioManager) : AudioCommand() {
    private val audioManager: AudioManager
    override fun execute(commandEvent: CommandEvent) {
        commandEvent.message.delete().queue()
        val eb = EmbedBuilder()
        if (audioManager.getMusicManager(commandEvent.guild).player.playingTrack != null) {
            val track = audioManager.getMusicManager(commandEvent.guild).player.playingTrack
            val info = track.info
            eb.setColor(Color.magenta.brighter())
            val imgurl = "https://img.youtube.com/vi/" + info.identifier + "/hqdefault.jpg"
            eb.addField("Currently playing:", "**" + info.title + "** - " + formatTiming(info.length), false)
            eb.setFooter("VirtusDevelops 2015-2020")
            eb.setThumbnail(imgurl)
            commandEvent.channel.sendMessage(eb.build()).queue { m: Message -> m.delete().queueAfter(15, TimeUnit.SECONDS) }
        }
    }

    init {
        name = "current"
        help = "Gets the current playing music."
        aliases = arrayOf("cplay", "cplaying", "c")
        this.audioManager = audioManager
    }
}