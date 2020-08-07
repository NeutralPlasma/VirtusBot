package com.neutralplasma.virtusbot.commands.audio

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.audio.AudioManager
import com.neutralplasma.virtusbot.audio.search.YoutubeSearch
import com.neutralplasma.virtusbot.commands.AudioCommand

class PlayCommand(audioManager: AudioManager, youtubeSearch: YoutubeSearch) : AudioCommand() {
    private val audioManager: AudioManager
    private val youtubeSearch: YoutubeSearch
    override fun execute(commandEvent: CommandEvent) {
        commandEvent.message.delete().queue()
        val arg = commandEvent.args
        if (youtubeSearch.isUrl(arg)) {
            val channel = commandEvent.member.voiceState?.channel
            if (channel != null) {
                audioManager.loadAndPlay(commandEvent.textChannel, arg, channel)
            } else {
                commandEvent.reply("**Please join a voice channel!**")
            }
        } else {
            val url = youtubeSearch.searchYoutube(arg)

            val channel = commandEvent.member.voiceState?.channel
            if (channel != null) {
                if (url != null) {
                    audioManager.loadAndPlay(commandEvent.textChannel, url, channel)
                }
            } else {
                commandEvent.reply("**Please join a voice channel!**")
            }

        }
    }

    init {
        name = "play"
        help = "Add music to queue"
        arguments = "<LINK / NAME>"
        aliases = arrayOf("p", "queueadd")
        this.audioManager = audioManager
        this.youtubeSearch = youtubeSearch
    }
}