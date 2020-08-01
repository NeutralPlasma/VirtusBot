package com.neutralplasma.virtusbot.commands.audio;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.neutralplasma.virtusbot.audio.AudioManager;
import com.neutralplasma.virtusbot.commands.AudioCommand;

public class SkipCommand extends AudioCommand {
    private AudioManager audioManager;

    public SkipCommand(AudioManager audioManager){
        this.name = "skip";
        this.help = "Skips current song";
        this.aliases = new String[]{"s"};
        this.audioManager = audioManager;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.getMessage().delete().queue();
        audioManager.skipTrack(commandEvent.getTextChannel());
    }
}
