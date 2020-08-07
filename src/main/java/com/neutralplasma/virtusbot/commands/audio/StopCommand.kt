package com.neutralplasma.virtusbot.commands.audio;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.neutralplasma.virtusbot.audio.AudioManager;
import com.neutralplasma.virtusbot.commands.AudioCommand;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class StopCommand extends AudioCommand {

    private AudioManager audioManager;

    public StopCommand(AudioManager audioManager){
        this.name = "stop";
        this.help = "Add music to queue";
        this.aliases = new String[]{"disconnect"};
        this.audioManager = audioManager;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.getMessage().delete().queue();
        VoiceChannel channel = commandEvent.getMember().getVoiceState().getChannel();
        if(channel != null && channel.getGuild().getAudioManager().getConnectedChannel() == channel) {
            audioManager.clearList(commandEvent.getGuild());
            commandEvent.reply("Stopped playing...");
        }else{
            commandEvent.reply("**You must be in same voice channel to stop music player.**");
        }
    }

}
