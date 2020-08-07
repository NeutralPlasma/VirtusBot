package com.neutralplasma.virtusbot.commands.audio;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.neutralplasma.virtusbot.audio.AudioManager;
import com.neutralplasma.virtusbot.audio.TrackOperation;
import com.neutralplasma.virtusbot.audio.search.YoutubeSearch;
import com.neutralplasma.virtusbot.commands.AudioCommand;
import com.neutralplasma.virtusbot.event.EventHandler;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class PlayCommand extends AudioCommand {

    private AudioManager audioManager;
    private YoutubeSearch youtubeSearch;

    public PlayCommand(AudioManager audioManager, YoutubeSearch youtubeSearch){
        this.name = "play";
        this.help = "Add music to queue";
        this.arguments = "<LINK / NAME>";
        this.aliases = new String[]{"p", "queueadd"};
        this.audioManager = audioManager;
        this.youtubeSearch = youtubeSearch;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.getMessage().delete().queue();
        String arg = commandEvent.getArgs();
        if(youtubeSearch.isUrl(arg)) {
            VoiceChannel channel = commandEvent.getMember().getVoiceState().getChannel();
            if (channel != null) {
                audioManager.loadAndPlay(commandEvent.getTextChannel(), arg, channel);
            }else{
                commandEvent.reply("**Please join a voice channel!**");
            }
        }else{
            String url = youtubeSearch.searchYoutube(arg);
            VoiceChannel channel = commandEvent.getMember().getVoiceState().getChannel();
            if (channel != null) {
                audioManager.loadAndPlay(commandEvent.getTextChannel(), url, channel);
            }else{
                commandEvent.reply("**Please join a voice channel!**");
            }
        }
    }
}
