package com.neutralplasma.virtusbot.commands.audio;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.neutralplasma.virtusbot.audio.AudioManager;
import com.neutralplasma.virtusbot.audio.search.YoutubeSearch;
import com.neutralplasma.virtusbot.commands.AudioCommand;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.Collection;
import java.util.Collections;

public class ShuffleCommand extends AudioCommand {

    private AudioManager audioManager;

    public ShuffleCommand(AudioManager audioManager){
        this.name = "shuffle";
        this.help = "Shuffles current queue.";
        this.aliases = new String[]{"shuff", "queueshuffle"};
        this.audioManager = audioManager;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.getMessage().delete().queue();

        audioManager.shuffle(commandEvent.getGuild());

        commandEvent.reply("**Shuffled queue!**");

    }
}
