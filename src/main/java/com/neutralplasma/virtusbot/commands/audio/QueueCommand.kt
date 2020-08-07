package com.neutralplasma.virtusbot.commands.audio;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.Paginator;
import com.neutralplasma.virtusbot.Bot;
import com.neutralplasma.virtusbot.VirtusBot;
import com.neutralplasma.virtusbot.audio.AudioManager;
import com.neutralplasma.virtusbot.audio.GuildMusicManager;
import com.neutralplasma.virtusbot.commands.AudioCommand;
import com.neutralplasma.virtusbot.utils.TextUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class QueueCommand extends AudioCommand {
    private AudioManager audioManager;
    private final Paginator.Builder builder;

    private final static String REPEAT = "\uD83D\uDD01"; // 🔁

    public QueueCommand(AudioManager audioManager, Bot bot){
        this.name = "queue";
        this.help = "Gets the current queue.";
        this.aliases = new String[]{"q", "getqueue"};
        this.audioManager = audioManager;
        builder = new Paginator.Builder()
                .setColumns(1)
                .setFinalAction(m -> {try{m.clearReactions().queue();}catch(PermissionException ignore){}})
                .setItemsPerPage(10)
                .waitOnSinglePage(false)
                .useNumberedItems(true)
                .showPageNumbers(true)
                .wrapPageEnds(true)
                .setEventWaiter(bot.getWaiter())
                .setTimeout(1, TimeUnit.MINUTES);

    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.getMessage().delete().queue();
        ArrayList<AudioTrack> songsl = audioManager.getQueue(commandEvent.getGuild());
        String[] songs = new String[songsl.size()];
        long total = 0;
        for(int i=0; i<songsl.size(); i++)
        {
            total += songsl.get(i).getDuration();
            songs[i] = songsl.get(i).getInfo().title;
        }
        long fintotal = total;
        if(songs.length > 0) {
            builder.setText((i1, i2) -> getQueueTitle(commandEvent.getClient().getSuccess(), songs.length, fintotal, false))
                    .setItems(songs)
                    .setUsers(commandEvent.getAuthor())
                    .setColor(Color.magenta.brighter())
            ;
            builder.build().paginate(commandEvent.getChannel(), 1);
        }else{
            commandEvent.reply("Nope.");
        }
    }

    private String getQueueTitle(String success, int songslength, long total, boolean repeatmode){
        StringBuilder sb = new StringBuilder();
        return TextUtil.filter(sb.append(success).append(" Current Queue | ").append(songslength)
                .append(" entries | `").append(TextUtil.formatTiming(total, 1000000000000L)).append("` ")
                .append(repeatmode ? "| " + REPEAT : "").toString());
    }
}
