package com.neutralplasma.virtusbot.commands;

import com.jagrosh.jdautilities.command.Command;
import net.dv8tion.jda.api.Permission;

public abstract class AudioCommand extends Command {

    public AudioCommand()
    {
        this.category = new Category("Music", event ->
        {
            return true;
            /*if(event.getAuthor().getId().equals(event.getClient().getOwnerId()))
                return true;
            if(event.getGuild()==null)
                return true;
            return event.getMember().hasPermission(Permission.MANAGE_SERVER);*/
        });
        this.guildOnly = true;
    }

}
