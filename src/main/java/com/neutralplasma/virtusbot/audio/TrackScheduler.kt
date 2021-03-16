package com.neutralplasma.virtusbot.audio

import com.neutralplasma.virtusbot.utils.TextUtil.formatTiming
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import java.awt.Color
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
class TrackScheduler(private val player: AudioPlayer) : AudioEventAdapter() {
    val queue: BlockingQueue<AudioTrack?>
    private var channel: TextChannel? = null
    private var repeating = false
    fun setChannel(channel: TextChannel?) {
        this.channel = channel
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    fun queue(track: AudioTrack?) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(track)
        }
    }

    /*
        Shuffles current queue.
     */
    fun shuffle() {
        val tracks = ArrayList(queue)
        Collections.shuffle(tracks)
        queue.clear()
        queue.addAll(tracks)
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    fun nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        player.startTrack(queue.poll(), false)
    }

    fun clearQueue() {
        queue.clear()
        player.stopTrack()
    }

    fun toggleRepeat(): Boolean {
        return if (repeating) {
            repeating = false
            false
        } else {
            repeating = true
            true
        }
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            if (repeating) {
                player.startTrack(track.makeClone(), false)
            } else {
                nextTrack()
            }
        }
    }

    override fun onPlayerPause(player: AudioPlayer) {
        // Player was paused
    }

    override fun onPlayerResume(player: AudioPlayer) {
        // Player was resumed
    }

    override fun onTrackStart(player: AudioPlayer, track: AudioTrack) {
        // MESSAGE HANDLER
        val info = track.info
        val data = info.uri.split("=".toRegex()).toTypedArray()
        val imgurl = "https://img.youtube.com/vi/" + data[1] + "/hqdefault.jpg"
        val eb = EmbedBuilder()
        eb.addField("Now playing:", info.title, false)
        eb.addField("Author:", info.author, true)
        eb.addField("Duration:", formatTiming(track.duration, 3600000L), true)
        eb.addField("Link:", info.uri, false)
        eb.setThumbnail(imgurl)
        eb.setFooter("VirtusDevelops 2015-2021")
        eb.setColor(Color.magenta.brighter())
        channel!!.sendMessage(eb.build()).queue { m: Message -> m.delete().queueAfter(10, TimeUnit.SECONDS) }
        // END
    }

    override fun onTrackStuck(player: AudioPlayer, track: AudioTrack, thresholdMs: Long) {
        nextTrack()
    }

    /**
     * @param player The audio player this scheduler uses
     */
    init {
        queue = LinkedBlockingQueue()
    }
}