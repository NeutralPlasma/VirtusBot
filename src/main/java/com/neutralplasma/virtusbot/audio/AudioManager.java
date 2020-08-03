package com.neutralplasma.virtusbot.audio;

import com.neutralplasma.virtusbot.utils.TextUtil;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class AudioManager extends ListenerAdapter {

    private final EqualizerFactory equalizer;
    private static final float[] BASS_BOOST = { 0.2f, 0.15f, 0.1f, 0.05f, 0.0f, -0.05f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f,
            -0.1f, -0.1f, -0.1f, -0.1f };

    public AudioManager(){

        this.musicManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();
        this.equalizer = new EqualizerFactory();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManager.player.setVolume(50);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
        return musicManager;
    }

    public void eqStart(Guild guild){
        GuildMusicManager musicManager = getGuildAudioPlayer(guild);
        musicManager.player.setFilterFactory(equalizer);
        musicManager.player.setFrameBufferDuration(500);
    }
    public void eqStop(Guild guild){
        GuildMusicManager musicManager = getGuildAudioPlayer(guild);
        for (int i = 0; i < BASS_BOOST.length; i++) {
            equalizer.setGain(i, BASS_BOOST[i]);
        }
        musicManager.player.setFilterFactory(null);
    }

    public void eqSet(Guild guild, String eq, float value){
        if(eq.equalsIgnoreCase("HIGHBASS")) {
            for (int i = 0; i < BASS_BOOST.length; i++) {
                equalizer.setGain(i, BASS_BOOST[i] + value);
            }
        }
        if(eq.equalsIgnoreCase("LOWBASS")) {
            for (int i = 0; i < BASS_BOOST.length; i++) {
                equalizer.setGain(i, -BASS_BOOST[i] + value);
            }
        }
    }

    public ArrayList<AudioTrack> getQueue(Guild guild){
        GuildMusicManager musicManager = getGuildAudioPlayer(guild);
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();
        ArrayList<AudioTrack> queueList = new ArrayList<>();
        for(AudioTrack track : queue){
            queueList.add(track);
        }
        return queueList;
    }


    public void shuffle(Guild guild){
        GuildMusicManager musicManager = getGuildAudioPlayer(guild);
        musicManager.scheduler.shuffle();
    }

    public boolean repeat(Guild guild){
        GuildMusicManager musicManager = getGuildAudioPlayer(guild);
        return musicManager.scheduler.toggleRepeat();
    }

    public void loadAndPlay(final TextChannel channel, final String trackUrl, final VoiceChannel voiceChannel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.setChannel(channel);
        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                EmbedBuilder eb = new EmbedBuilder();
                AudioTrackInfo info = track.getInfo();
                eb.addField("Added to queue: ", "**" + info.title + "** - " + TextUtil.formatTiming(track.getDuration(), 3600000L), false);
                eb.setColor(Color.magenta.brighter());
                eb.setFooter("VirtusDevelops 2015-2020");
                channel.sendMessage(eb.build()).queue(m -> {
                    m.delete().queueAfter(15, TimeUnit.SECONDS);
                });

                play(channel.getGuild(), musicManager, track, voiceChannel);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                EmbedBuilder eb = new EmbedBuilder();

                int maxsize = 200;
                int currentsize = 0;
                StringBuilder builder = new StringBuilder();
                for(AudioTrack track : playlist.getTracks()){
                    play(channel.getGuild(), musicManager, track, voiceChannel);
                    currentsize++;
                    AudioTrackInfo info = track.getInfo();
                    if(currentsize < 10) {
                        builder.append("**").append(info.title).append("** - ").append(TextUtil.formatTiming(info.length, 3600000L)).append("\n");
                    }
                    if(currentsize >= maxsize){
                        break;
                    }
                }
                if(currentsize > 10){
                    builder.append("and ").append(currentsize - 10).append(" more...");
                }
                eb.addField("Loaded tracks: ", builder.toString(), false);
                eb.setFooter("VirtusDevelops 2015-2020");
                eb.setColor(Color.magenta.brighter());
                channel.sendMessage(eb.build()).queue(m -> {
                    m.delete().queueAfter(30, TimeUnit.SECONDS);
                });

                //channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

                //play(channel.getGuild(), musicManager, firstTrack, voiceChannel);
            }

            @Override
            public void noMatches() {

                EmbedBuilder eb = new EmbedBuilder();
                eb.addField("Error", "Could not find any song by that name. `" + trackUrl + "`", false);
                eb.setFooter("VirtusDevelops 2015-2020");
                channel.sendMessage(eb.build()).queue(m -> {
                    m.delete().queueAfter(15, TimeUnit.SECONDS);
                });
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.addField("Error", "Could not play this song.", false);
                eb.setFooter("VirtusDevelops 2015-2020");
                channel.sendMessage(eb.build()).queue(m -> {
                    m.delete().queueAfter(15, TimeUnit.SECONDS);
                });
            }
        });
    }

    public void play(Guild guild, GuildMusicManager musicManager, AudioTrack track, VoiceChannel voiceChannel) {
        connectToFirstVoiceChannel(guild.getAudioManager(), voiceChannel);
        musicManager.scheduler.queue(track);
    }

    public GuildMusicManager getMusicManager(Guild guild){
        return getGuildAudioPlayer(guild);
    }

    public void clearList(Guild guild){
        GuildMusicManager musicManager = getGuildAudioPlayer(guild);
        musicManager.scheduler.clearQueue();
        guild.getAudioManager().closeAudioConnection();

    }

    public void skipTrack(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.nextTrack();

        channel.sendMessage("**Skipping to next song...**").queue(m -> {
            m.delete().queueAfter(15, TimeUnit.SECONDS);
        });
    }

    public void forPlayingTrack(TrackOperation operation, Guild guild) {
        GuildMusicManager musicManager = getGuildAudioPlayer(guild);
        AudioTrack track = musicManager.player.getPlayingTrack();

        if (track != null) {
            operation.execute(track);
        }
    }



    private void connectToFirstVoiceChannel(net.dv8tion.jda.api.managers.AudioManager audioManager, VoiceChannel voiceChannel) {
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            audioManager.openAudioConnection(voiceChannel);
        }
    }

}
