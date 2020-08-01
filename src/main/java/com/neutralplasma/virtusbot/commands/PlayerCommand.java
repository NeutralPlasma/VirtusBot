package com.neutralplasma.virtusbot.commands;

import com.jagrosh.jdautilities.command.Command;

public abstract class PlayerCommand extends Command {

    public PlayerCommand()
    {
        this.category = new Category("Player", event -> {
            return true;
        });
        this.guildOnly = true;
    }

}
