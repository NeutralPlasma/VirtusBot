package com.neutralplasma.virtusbot.commands.audio;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.neutralplasma.virtusbot.audio.AudioManager;
import com.neutralplasma.virtusbot.commands.AudioCommand;

public class EqualizerCommand extends AudioCommand {
    private AudioManager audioManager;

    public EqualizerCommand(AudioManager audioManager){
        this.name = "eq";
        this.help = "Change equalizer";
        this.aliases = new String[]{"equalizer"};
        this.audioManager = audioManager;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.getMessage().delete().queue();
        String[] args = commandEvent.getArgs().split(" ");
        if(args.length > 0){
            if(args[0].equalsIgnoreCase("highbass")){
                audioManager.eqSet(commandEvent.getGuild(), "HIGHBASS", 0.1f);
                commandEvent.reply("Enabling bass boost!");
            }else if(args[0].equalsIgnoreCase("lowbass")){
                audioManager.eqSet(commandEvent.getGuild(), "LOWBASS", -0.1f);
                commandEvent.reply("Disabling bass boost!");
            }else if(args[0].equalsIgnoreCase("start")){
                audioManager.eqStart(commandEvent.getGuild());
                commandEvent.reply("Enabled equalizer!");
            }else if(args[0].equalsIgnoreCase("stop")){
                audioManager.eqStop(commandEvent.getGuild());
                commandEvent.reply("Disabled equalizer!");
            }
        }
        audioManager.eqStart(commandEvent.getGuild());
    }
}
