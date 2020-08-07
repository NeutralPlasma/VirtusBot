package com.neutralplasma.virtusbot.audio

import com.neutralplasma.virtusbot.utils.TextUtil.formatTiming
import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.managers.AudioManager
import java.awt.Color
import java.util.*
import java.util.concurrent.TimeUnit

class AudioManager : ListenerAdapter() {
    private val equalizer: EqualizerFactory
    private val playerManager: AudioPlayerManager
    private val musicManagers: MutableMap<Long, GuildMusicManager>

    @Synchronized
    private fun getGuildAudioPlayer(guild: Guild): GuildMusicManager {
        val guildId = guild.id.toLong()
        var musicManager = musicManagers[guildId]
        if (musicManager == null) {
            musicManager = GuildMusicManager(playerManager)
            musicManager.player.volume = 50
            musicManagers[guildId] = musicManager
        }
        guild.audioManager.sendingHandler = musicManager.sendHandler
        return musicManager
    }

    fun eqStart(guild: Guild) {
        val musicManager = getGuildAudioPlayer(guild)
        musicManager.player.setFilterFactory(equalizer)
        musicManager.player.setFrameBufferDuration(500)
    }

    fun eqStop(guild: Guild) {
        val musicManager = getGuildAudioPlayer(guild)
        for (i in BASS_BOOST.indices) {
            equalizer.setGain(i, BASS_BOOST[i])
        }
        musicManager.player.setFilterFactory(null)
    }

    fun eqSet(guild: Guild?, eq: String, value: Float) {
        if (eq.equals("HIGHBASS", ignoreCase = true)) {
            for (i in BASS_BOOST.indices) {
                equalizer.setGain(i, BASS_BOOST[i] + value)
            }
        }
        if (eq.equals("LOWBASS", ignoreCase = true)) {
            for (i in BASS_BOOST.indices) {
                equalizer.setGain(i, -BASS_BOOST[i] + value)
            }
        }
    }

    fun getQueue(guild: Guild): ArrayList<AudioTrack?> {
        val musicManager = getGuildAudioPlayer(guild)
        val queue = musicManager.scheduler.queue
        val queueList = ArrayList<AudioTrack?>()
        for (track in queue) {
            queueList.add(track)
        }
        return queueList
    }

    fun shuffle(guild: Guild) {
        val musicManager = getGuildAudioPlayer(guild)
        musicManager.scheduler.shuffle()
    }

    fun repeat(guild: Guild): Boolean {
        val musicManager = getGuildAudioPlayer(guild)
        return musicManager.scheduler.toggleRepeat()
    }

    fun loadAndPlay(channel: TextChannel, trackUrl: String, voiceChannel: VoiceChannel?) {
        val musicManager = getGuildAudioPlayer(channel.guild)
        musicManager.scheduler.setChannel(channel)
        playerManager.loadItemOrdered(musicManager, trackUrl, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                val eb = EmbedBuilder()
                val info = track.info
                eb.addField("Added to queue: ", "**" + info.title + "** - " + formatTiming(track.duration), false)
                eb.setColor(Color.magenta.brighter())
                eb.setFooter("VirtusDevelops 2015-2020")
                channel.sendMessage(eb.build()).queue { m: Message -> m.delete().queueAfter(15, TimeUnit.SECONDS) }
                play(channel.guild, musicManager, track, voiceChannel)
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                val eb = EmbedBuilder()
                val maxsize = 200
                var currentsize = 0
                val builder = StringBuilder()
                for (track in playlist.tracks) {
                    play(channel.guild, musicManager, track, voiceChannel)
                    currentsize++
                    val info = track.info
                    if (currentsize < 10) {
                        builder.append("**").append(info.title).append("** - ").append(formatTiming(info.length)).append("\n")
                    }
                    if (currentsize >= maxsize) {
                        break
                    }
                }
                if (currentsize > 10) {
                    builder.append("and ").append(currentsize - 10).append(" more...")
                }
                eb.addField("Loaded tracks: ", builder.toString(), false)
                eb.setFooter("VirtusDevelops 2015-2020")
                eb.setColor(Color.magenta.brighter())
                channel.sendMessage(eb.build()).queue { m: Message -> m.delete().queueAfter(30, TimeUnit.SECONDS) }

                //channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

                //play(channel.getGuild(), musicManager, firstTrack, voiceChannel);
            }

            override fun noMatches() {
                val eb = EmbedBuilder()
                eb.addField("Error", "Could not find any song by that name. `$trackUrl`", false)
                eb.setFooter("VirtusDevelops 2015-2020")
                channel.sendMessage(eb.build()).queue { m: Message -> m.delete().queueAfter(15, TimeUnit.SECONDS) }
            }

            override fun loadFailed(exception: FriendlyException) {
                val eb = EmbedBuilder()
                eb.addField("Error", "Could not play this song.", false)
                eb.setFooter("VirtusDevelops 2015-2020")
                channel.sendMessage(eb.build()).queue { m: Message -> m.delete().queueAfter(15, TimeUnit.SECONDS) }
            }
        })
    }

    fun play(guild: Guild, musicManager: GuildMusicManager, track: AudioTrack?, voiceChannel: VoiceChannel?) {
        connectToFirstVoiceChannel(guild.audioManager, voiceChannel)
        musicManager.scheduler.queue(track)
    }

    fun getMusicManager(guild: Guild): GuildMusicManager {
        return getGuildAudioPlayer(guild)
    }

    fun clearList(guild: Guild) {
        val musicManager = getGuildAudioPlayer(guild)
        musicManager.scheduler.clearQueue()
        guild.audioManager.closeAudioConnection()
    }

    fun skipTrack(channel: TextChannel) {
        val musicManager = getGuildAudioPlayer(channel.guild)
        musicManager.scheduler.nextTrack()
        channel.sendMessage("**Skipping to next song...**").queue { m: Message -> m.delete().queueAfter(15, TimeUnit.SECONDS) }
    }

    fun forPlayingTrack(operation: (AudioTrack) -> Unit, guild: Guild) {
        val musicManager = getGuildAudioPlayer(guild)
        val track = musicManager.player.playingTrack
        if (track != null) {
            operation(track)
        }
    }

    private fun connectToFirstVoiceChannel(audioManager: AudioManager, voiceChannel: VoiceChannel?) {
        if (!audioManager.isConnected) {
            audioManager.openAudioConnection(voiceChannel)
        }
    }

    companion object {
        private val BASS_BOOST = floatArrayOf(0.2f, 0.15f, 0.1f, 0.05f, 0.0f, -0.05f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f,
                -0.1f, -0.1f, -0.1f, -0.1f)
    }

    init {
        musicManagers = HashMap()
        playerManager = DefaultAudioPlayerManager()
        equalizer = EqualizerFactory()
        AudioSourceManagers.registerRemoteSources(playerManager)
        AudioSourceManagers.registerLocalSource(playerManager)
    }
}