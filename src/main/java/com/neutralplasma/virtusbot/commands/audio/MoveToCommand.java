package com.neutralplasma.virtusbot.commands.audio;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.neutralplasma.virtusbot.audio.AudioManager;
import com.neutralplasma.virtusbot.commands.AudioCommand;

public class MoveToCommand extends AudioCommand {
    private AudioManager audioManager;

    public MoveToCommand(AudioManager audioManager){
        this.name = "skipto";
        this.aliases = new String[]{"moveto"};
        this.help = "SkipTo command";
        this.arguments = "<DURATION IN SEC>";
        this.audioManager = audioManager;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.getMessage().delete().queue();
        String[] args = commandEvent.getArgs().split(" ");
        if(args.length > 0){
            long position = Long.parseLong(args[0]);
            audioManager.forPlayingTrack(track -> {
                track.setPosition(track.getPosition() + position * 1000L);
            },commandEvent.getGuild());
        }else{
            commandEvent.reply("Please provide all the args.");
        }
    }
}
