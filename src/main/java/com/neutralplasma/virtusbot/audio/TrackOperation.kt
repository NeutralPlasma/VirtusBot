package com.neutralplasma.virtusbot.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public interface TrackOperation {
    void execute(AudioTrack track);
}
