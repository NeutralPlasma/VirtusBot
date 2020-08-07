package com.neutralplasma.virtusbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.neutralplasma.virtusbot.VirtusBot;
import net.dv8tion.jda.api.Permission;

public abstract class AudioCommand extends Command {

    public AudioCommand()
    {
        this.category = new Category("Music", event -> {
            if(VirtusBot.getBlackList().isBlackListed(event.getAuthor().getId())){
                return false;
            }
            return true;
        });
        this.guildOnly = true;
    }

}
