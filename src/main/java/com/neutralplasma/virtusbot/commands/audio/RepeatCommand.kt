package com.neutralplasma.virtusbot.commands.audio;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.neutralplasma.virtusbot.audio.AudioManager;
import com.neutralplasma.virtusbot.commands.AudioCommand;

public class RepeatCommand extends AudioCommand {

    private AudioManager audioManager;

    public RepeatCommand(AudioManager audioManager){
        this.name = "repeat";
        this.help = "Repeats current playing song.";
        this.audioManager = audioManager;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.getMessage().delete().queue();

        if(audioManager.repeat(commandEvent.getGuild())){
            commandEvent.reply("Enabled repeat!");
        }else{
            commandEvent.reply("Stopped repeat!");
        }


    }
}
