package com.neutralplasma.virtusbot.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.neutralplasma.virtusbot.settings.NewSettingsManager;
import com.neutralplasma.virtusbot.storage.locale.LocaleHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.function.Consumer;

public class SuggestCmd extends Command {

    private NewSettingsManager newSettingsManager;
    private LocaleHandler localeHandler;

    public SuggestCmd(NewSettingsManager newSettingsManager, LocaleHandler localeHandler){
        this.name = "suggest";
        this.help = "Create a new suggestion";
        this.arguments = "<SUGGESTION>";
        this.guildOnly = true;
        this.newSettingsManager = newSettingsManager;
        this.localeHandler = localeHandler;
    }

    @Override
    protected void execute(CommandEvent event) {
        String arg = event.getArgs();
        Guild guild = event.getGuild();
        event.getMessage().delete();
        TextChannel suggestChannel = newSettingsManager.getTextChannel(guild, "SUGGEST_CHANNEL");
        if(suggestChannel != null) {
            createSuggestion(suggestChannel, event.getAuthor(), arg, guild);
        }else{
            EmbedBuilder eb = new EmbedBuilder();
            String field_title = localeHandler.getLocale(guild, "ERROR_FIELD_TITLE");
            String title = localeHandler.getLocale(guild, "ERROR_TITLE");
            String content = localeHandler.getLocale(guild, "ERROR_WRONG_NOCHANNEL");

            eb.setTitle(title);
            eb.addField(field_title, content, false);
            eb.setColor(Color.orange);
            event.getChannel().sendMessage(eb.build()).queue();
        }

    }

    public void createSuggestion(TextChannel channel, User user, String suggestion, Guild guild){
        EmbedBuilder eb = new EmbedBuilder();

        String field_title = localeHandler.getLocale(guild, "SUGGEST_FIELD_TITLE");
        String title = localeHandler.getLocale(guild, "SUGGEST_TITLE");
        String field_title2 = localeHandler.getLocale(guild, "SUGGEST_TITLE_OWNER");

        eb.setTitle(title);
        eb.addField(field_title, "`" + suggestion + "`", false);
        eb.addField(field_title2, user.getAsMention(), false);
        eb.setColor(Color.orange);
        Consumer<Message> callback = (response) ->{
                response.addReaction("\u2705").queue();
                response.addReaction("\u274C").queue();
        };

        channel.sendMessage(eb.build()).queue(callback);
    }


}
