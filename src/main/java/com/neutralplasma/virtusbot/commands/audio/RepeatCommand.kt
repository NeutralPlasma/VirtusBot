package com.neutralplasma.virtusbot.commands.audio

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.audio.AudioManager
import com.neutralplasma.virtusbot.commands.AudioCommand

class RepeatCommand(audioManager: AudioManager) : AudioCommand() {
    private val audioManager: AudioManager
    override fun execute(commandEvent: CommandEvent) {
        commandEvent.message.delete().queue()
        if (audioManager.repeat(commandEvent.guild)) {
            commandEvent.reply("Enabled repeat!")
        } else {
            commandEvent.reply("Stopped repeat!")
        }
    }

    init {
        name = "repeat"
        help = "Repeats current playing song."
        this.audioManager = audioManager
    }
}