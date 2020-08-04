package com.neutralplasma.virtusbot.commands.audio;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.neutralplasma.virtusbot.audio.AudioManager;
import com.neutralplasma.virtusbot.commands.AudioCommand;
import com.neutralplasma.virtusbot.utils.TextUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class CurrentPlayingCommand extends AudioCommand {
    private AudioManager audioManager;

    public CurrentPlayingCommand(AudioManager audioManager){
        this.name = "current";
        this.help = "Gets the current playing music.";
        this.aliases = new String[]{"cplay", "cplaying", "c"};
        this.audioManager = audioManager;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.getMessage().delete().queue();

        EmbedBuilder eb = new EmbedBuilder();
        if(audioManager.getMusicManager(commandEvent.getGuild()).player.getPlayingTrack() != null){
            AudioTrack track = audioManager.getMusicManager(commandEvent.getGuild()).player.getPlayingTrack();
            AudioTrackInfo info = track.getInfo();
            eb.setColor(Color.magenta.brighter());
            String imgurl = "https://img.youtube.com/vi/" + info.identifier + "/hqdefault.jpg";
            eb.addField("Currently playing:","**" + info.title + "** - " + TextUtil.formatTiming(info.length, 360000L),false );
            eb.setFooter("VirtusDevelops 2015-2020");
            eb.setThumbnail(imgurl);
            commandEvent.getChannel().sendMessage(eb.build()).queue(m -> {
                m.delete().queueAfter(15, TimeUnit.SECONDS);
            });
        }
    }
}
