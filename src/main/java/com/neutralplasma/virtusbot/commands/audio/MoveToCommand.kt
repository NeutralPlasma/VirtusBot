package com.neutralplasma.virtusbot.commands.audio

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.audio.AudioManager
import com.neutralplasma.virtusbot.commands.AudioCommand
import com.sedmelluq.discord.lavaplayer.track.AudioTrack

class MoveToCommand(audioManager: AudioManager) : AudioCommand() {
    private val audioManager: AudioManager
    override fun execute(commandEvent: CommandEvent) {
        commandEvent.message.delete().queue()
        val args = commandEvent.args.split(" ".toRegex()).toTypedArray()
        if (args.size > 0) {
            val position = args[0].toLong()
            audioManager.forPlayingTrack({ track: AudioTrack -> track.position = track.position + position * 1000L }, commandEvent.guild)
        } else {
            commandEvent.reply("Please provide all the args.")
        }
    }

    init {
        name = "skipto"
        aliases = arrayOf("moveto")
        help = "SkipTo command"
        arguments = "<DURATION IN SEC>"
        this.audioManager = audioManager
    }
}