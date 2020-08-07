package com.neutralplasma.virtusbot.audio

import com.sedmelluq.discord.lavaplayer.track.AudioTrack

interface TrackOperation {
    fun execute(track: AudioTrack?)
}