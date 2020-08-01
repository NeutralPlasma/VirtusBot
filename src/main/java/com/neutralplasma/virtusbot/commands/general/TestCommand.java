package com.neutralplasma.virtusbot.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.neutralplasma.virtusbot.settings.NewSettingsManager;
import com.neutralplasma.virtusbot.storage.LocaleHandler;
import com.neutralplasma.virtusbot.utils.AbstractChatUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.function.Consumer;

public class TestCommand extends Command {

    private NewSettingsManager newSettingsManager;
    private LocaleHandler localeHandler;

    public TestCommand(NewSettingsManager newSettingsManager, LocaleHandler localeHandler){
        this.name = "test";
        this.help = "some testing";
        this.guildOnly = true;
        this.newSettingsManager = newSettingsManager;
        this.localeHandler = localeHandler;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("Doing stuff.");
        AbstractChatUtil abstractChatUtil = new AbstractChatUtil(event.getAuthor(), chatInfo -> {
            event.reply(chatInfo.getMessage());
        }, event.getJDA());
        abstractChatUtil.setOnClose(() -> {
            event.reply("DONE");
        });
    }

}
