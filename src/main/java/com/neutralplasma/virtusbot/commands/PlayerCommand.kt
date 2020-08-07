package com.neutralplasma.virtusbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.neutralplasma.virtusbot.VirtusBot;

public abstract class PlayerCommand extends Command {

    public PlayerCommand()
    {
        this.category = new Category("Player", event -> !VirtusBot.getBlackList().isBlackListed(event.getAuthor().getId()));
        this.guildOnly = true;
    }

}
