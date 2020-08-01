package com.neutralplasma.virtusbot.commands.audio;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.neutralplasma.virtusbot.audio.AudioManager;
import com.neutralplasma.virtusbot.commands.AudioCommand;

public class VolumeCommand extends AudioCommand {
    private AudioManager audioManager;

    public VolumeCommand(AudioManager audioManager){
        this.name = "volume";
        this.help = "Sets the bot volume";
        this.aliases = new String[]{"volumeset"};
        this.audioManager = audioManager;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.getMessage().delete().queue();
        String arg = commandEvent.getArgs();
        String[] args = arg.split(" ");
        if(arg.length() < 1){
            int volume = Integer.parseInt(args[0]);
            audioManager.getMusicManager(commandEvent.getGuild()).player.setVolume(volume);
        }
    }
}
