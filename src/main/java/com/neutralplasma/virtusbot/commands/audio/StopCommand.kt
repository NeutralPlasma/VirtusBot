package com.neutralplasma.virtusbot.commands.audio

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.audio.AudioManager
import com.neutralplasma.virtusbot.commands.AudioCommand

class StopCommand(audioManager: AudioManager) : AudioCommand() {
    private val audioManager: AudioManager
    override fun execute(commandEvent: CommandEvent) {
        commandEvent.message.delete().queue()
        val channel = commandEvent.member.voiceState?.channel
        if (channel != null && channel.guild.audioManager.connectedChannel === channel) {
            audioManager.clearList(commandEvent.guild)
            commandEvent.reply("Stopped playing...")
        } else {
            commandEvent.reply("**You must be in same voice channel to stop music player.**")
        }
    }

    init {
        name = "stop"
        help = "Add music to queue"
        aliases = arrayOf("disconnect")
        this.audioManager = audioManager
    }
}