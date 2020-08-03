package com.neutralplasma.virtusbot.audio;

import com.neutralplasma.virtusbot.utils.TextUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private TextChannel channel;
    private boolean repeating;

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.repeating = false;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void setChannel(TextChannel channel) {
        this.channel = channel;
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public BlockingQueue<AudioTrack> getQueue(){
        return this.queue;
    }


    /*
        Shuffles current queue.
     */
    public void shuffle(){
        ArrayList<AudioTrack> tracks = new ArrayList<>(queue);
        Collections.shuffle(tracks);
        this.queue.clear();
        this.queue.addAll(tracks);
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        player.startTrack(queue.poll(), false);
    }

    public void clearQueue(){
        queue.clear();
        player.stopTrack();
    }

    public boolean toggleRepeat(){
        if(this.repeating){
            this.repeating = false;
            return false;
        }else{
            this.repeating = true;
            return true;
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            if(repeating){
                player.startTrack(track.makeClone(), false);
            }else {
                nextTrack();
            }
        }
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        // Player was paused
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        // Player was resumed
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        // MESSAGE HANDLER
        AudioTrackInfo info = track.getInfo();
        String[] data = info.uri.split("=");
        String imgurl = "https://img.youtube.com/vi/" + data[1] + "/hqdefault.jpg";
        EmbedBuilder eb = new EmbedBuilder();
        eb.addField("Now playing:", info.title, false);
        eb.addField("Author:", info.author, true);
        eb.addField("Duration:", TextUtil.formatTiming(track.getDuration(), 3600000L), true);
        eb.addField("Link:", info.uri, false);
        eb.setThumbnail(imgurl);
        eb.setFooter("VirtusDevelops 2015-2020");
        eb.setColor(Color.magenta.brighter());
        channel.sendMessage(eb.build()).queue(m -> {
            m.delete().queueAfter(10, TimeUnit.SECONDS);
        });
        // END
    }


    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        nextTrack();
    }
}