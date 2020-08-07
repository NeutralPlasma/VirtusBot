package com.neutralplasma.virtusbot.commands.audio

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.audio.AudioManager
import com.neutralplasma.virtusbot.commands.AudioCommand

class VolumeCommand(audioManager: AudioManager) : AudioCommand() {
    private val audioManager: AudioManager
    override fun execute(commandEvent: CommandEvent) {
        commandEvent.message.delete().queue()
        val arg = commandEvent.args
        val args = arg.split(" ".toRegex()).toTypedArray()
        if (arg.length < 1) {
            val volume = args[0].toInt()
            audioManager.getMusicManager(commandEvent.guild).player.volume = volume
        }
    }

    init {
        name = "volume"
        help = "Sets the bot volume"
        aliases = arrayOf("volumeset")
        this.audioManager = audioManager
    }
}