package com.neutralplasma.virtusbot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.neutralplasma.virtusbot.VirtusBot;
import com.neutralplasma.virtusbot.commands.AdminCommand;
import com.neutralplasma.virtusbot.settings.NewSettingsManager;
import com.neutralplasma.virtusbot.storage.LocaleHandler;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class SetSuggestCmd extends AdminCommand {

    private NewSettingsManager newSettingsManager;
    private LocaleHandler localeHandler;

    public SetSuggestCmd(NewSettingsManager newSettingsManager, LocaleHandler localeHandler){
        this.name = "setsuggest";
        this.help = "Sets suggestions channel";
        this.newSettingsManager = newSettingsManager;
        this.localeHandler = localeHandler;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.getMessage().delete().queue();

        String content = localeHandler.getLocale(commandEvent.getGuild(), "SUGGEST_CREATE_CONTENT");
        String field_title = localeHandler.getLocale(commandEvent.getGuild(), "SUGGEST_CREATE_FIELD_TITLE");
        String title = localeHandler.getLocale(commandEvent.getGuild(), "SUGGEST_CREATE_TITLE");

        content = content.replace("{prefix}", VirtusBot.getPrefix());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title);
        eb.addField(field_title, content, false);
        eb.setColor(Color.orange);


        try {
            newSettingsManager.addStringData(commandEvent.getGuild(), "SUGGEST_CHANNEL", commandEvent.getTextChannel().getId());
            //settingsManager.getSettings(commandEvent.getGuild()).setSuggestId(commandEvent.getChannel().getIdLong());
        }catch (NullPointerException error){
            error.printStackTrace();
        }
        commandEvent.getChannel().sendMessage(eb.build()).queue();

    }
}

