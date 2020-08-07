package com.neutralplasma.virtusbot.commands.audio

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.audio.AudioManager
import com.neutralplasma.virtusbot.commands.AudioCommand

class EqualizerCommand(audioManager: AudioManager) : AudioCommand() {
    private val audioManager: AudioManager
    override fun execute(commandEvent: CommandEvent) {
        commandEvent.message.delete().queue()
        val args = commandEvent.args.split(" ".toRegex()).toTypedArray()
        if (args.size > 0) {
            if (args[0].equals("highbass", ignoreCase = true)) {
                audioManager.eqSet(commandEvent.guild, "HIGHBASS", 0.1f)
                commandEvent.reply("Enabling bass boost!")
            } else if (args[0].equals("lowbass", ignoreCase = true)) {
                audioManager.eqSet(commandEvent.guild, "LOWBASS", -0.1f)
                commandEvent.reply("Disabling bass boost!")
            } else if (args[0].equals("start", ignoreCase = true)) {
                audioManager.eqStart(commandEvent.guild)
                commandEvent.reply("Enabled equalizer!")
            } else if (args[0].equals("stop", ignoreCase = true)) {
                audioManager.eqStop(commandEvent.guild)
                commandEvent.reply("Disabled equalizer!")
            }
        }
        audioManager.eqStart(commandEvent.guild)
    }

    init {
        name = "eq"
        help = "Change equalizer"
        aliases = arrayOf("equalizer")
        this.audioManager = audioManager
    }
}