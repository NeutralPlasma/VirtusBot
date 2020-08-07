package com.neutralplasma.virtusbot.commands.audio

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.audio.AudioManager
import com.neutralplasma.virtusbot.commands.AudioCommand

class SkipCommand(audioManager: AudioManager) : AudioCommand() {
    private val audioManager: AudioManager
    override fun execute(commandEvent: CommandEvent) {
        commandEvent.message.delete().queue()
        audioManager.skipTrack(commandEvent.textChannel)
    }

    init {
        name = "skip"
        help = "Skips current song"
        aliases = arrayOf("s")
        this.audioManager = audioManager
    }
}