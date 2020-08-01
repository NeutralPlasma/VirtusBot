package com.neutralplasma.virtusbot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.neutralplasma.virtusbot.commands.AdminCommand;
import com.neutralplasma.virtusbot.settings.NewSettingsManager;
import com.neutralplasma.virtusbot.storage.LocaleHandler;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class CreateTicketChannelCmd extends AdminCommand {

    private NewSettingsManager settingsManager;
    private LocaleHandler localeHandler;

    public CreateTicketChannelCmd(NewSettingsManager settingsManager, LocaleHandler localeHandler){
        this.name = "createticketchannel";
        this.help = "Creates ticket channel";
        this.settingsManager = settingsManager;
        this.localeHandler = localeHandler;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.getMessage().delete().queue();

        String content = localeHandler.getLocale(commandEvent.getGuild(), "TICKET_HELP_CREATE_REACT_CONTENT");
        String field_title = localeHandler.getLocale(commandEvent.getGuild(), "TICKET_HELP_CREATE_REACT_FIELD_TITLE");
        String title = localeHandler.getLocale(commandEvent.getGuild(), "TICKET_HELP_CREATE_REACT_FIELD_TITLE");

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(title);
        //eb.addField("React", "Reactaj z \uD83D\uDCAC, da ustvariš pogovor za pomoč.", false);
        eb.addField(field_title, content, false);
        eb.setColor(Color.orange);
        try {
            settingsManager.addStringData(commandEvent.getGuild(),"TICKET_CHANNEL", commandEvent.getChannel().getId());
        }catch (NullPointerException error){
            error.printStackTrace();
        }

        commandEvent.getChannel().sendMessage(eb.build()).complete().addReaction("\uD83D\uDCAC").queue();
    }
}
