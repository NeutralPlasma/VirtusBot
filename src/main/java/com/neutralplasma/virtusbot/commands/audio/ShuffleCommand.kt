package com.neutralplasma.virtusbot.commands.audio

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.audio.AudioManager
import com.neutralplasma.virtusbot.commands.AudioCommand

class ShuffleCommand(audioManager: AudioManager) : AudioCommand() {
    private val audioManager: AudioManager
    override fun execute(commandEvent: CommandEvent) {
        commandEvent.message.delete().queue()
        audioManager.shuffle(commandEvent.guild)
        commandEvent.reply("**Shuffled queue!**")
    }

    init {
        name = "shuffle"
        help = "Shuffles current queue."
        aliases = arrayOf("shuff", "queueshuffle")
        this.audioManager = audioManager
    }
}