package com.neutralplasma.virtusbot.storage;

import com.neutralplasma.virtusbot.Bot;
import com.neutralplasma.virtusbot.settings.NewSettingsManager;
import com.neutralplasma.virtusbot.utils.TextUtil;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.SQLException;
import java.util.HashMap;

public class LocaleHandler {
    private NewSettingsManager newSettingsManager;
    private SQL sql;
    private HashMap<String, LocaleData> stored = new HashMap<>();
    private HashMap<String, String> defaultValues = new HashMap<>();
    private Bot bot;



    public LocaleHandler(NewSettingsManager newSettingsManager, SQL sql, Bot bot){
        this.newSettingsManager = newSettingsManager;
        this.sql = sql;
        this.bot = bot;

        try {
            sql.createTable("serverlocales",
                    "guildID TEXT," +
                    "localedata TEXT");
        }catch (SQLException error){
            error.printStackTrace();
        }
    }

    public void setup(){
        for (Guild guild : bot.getJDA().getGuilds()){
            try {
                LocaleData data = sql.getServerLocale(guild.getId());

                if (sql.getServerLocale(guild.getId()) == null){
                    TextUtil.sendMessage("Adding");
                    sql.addServerLocale(guild.getId());
                    data = sql.getServerLocale(guild.getId());
                }
                TextUtil.sendMessage("Added to storage: " + data.getAllLocales().get("TEST"));
                stored.put(guild.getId(), data);
            }catch (SQLException error){
                TextUtil.sendMessage("Error while loading server locale: " + error.getMessage());
            }
        }
        defaultValues.put("TICKET_HELP_CREATE_REACT_CONTENT", "`React with \uD83D\uDCAC to create the ticket.`");
        defaultValues.put("TICKET_HELP_CREATE_REACT_TITLE ", "Ticket Creation.");
        defaultValues.put("TICKET_HELP_CREATE_REACT_FIELD_TITLE", "React with \uD83D\uDCAC to create the ticket.");
        defaultValues.put("SUGGEST_CREATE_CONTENT", "`Type {prefix}suggest <suggestion> - to create new suggestion.`");
        defaultValues.put("SUGGEST_CREATE_FIELD_TITLE", "Suggestion");
        defaultValues.put("SUGGEST_CREATE_TITLE", "Suggestion");
        defaultValues.put("VOTE_TITLE", "Vote");
        defaultValues.put("VOTE_FIELD_TITLE", "Vote info:");
        defaultValues.put("ERROR_FIELD_TITLE", "ERROR");
        defaultValues.put("ERROR_TITLE", "ERROR");
        defaultValues.put("ERROR_WRONG_CHANNEL", "Wrong channel use: {channel}");
        defaultValues.put("ERROR_WRONG_NOCHANNEL", "There is no channel set for this command contact administrator.");
        defaultValues.put("SUGGEST_TITLE_OWNER ", "Suggestor:");
        defaultValues.put("SUGGEST_TITLE ", "Suggestion");
        defaultValues.put("SUGGEST_FIELD_TITLE ", "Suggestion text:");
        defaultValues.put("TICKET_CREATE_MESSAGE", "Creating channel please wait...");
        defaultValues.put("TICKET_INFO_MESSAGE", "`Hello! We have create ticket channel for you, please wait for support team to come and help you.`");
        defaultValues.put("TICKET_INFO_FIELD_TITLE", "Ticket");
        defaultValues.put("TICKET_DELETE_TITLE", "Notification");
        defaultValues.put("TICKET_DELETE_FIELD_TITLE", "Delete");
        defaultValues.put("TICKET_DELETE_MESSAGE", "`Are you sure you want to delete this ticket?`");
        defaultValues.put("SUGGEST_TITLE", "Suggestion");
        defaultValues.put("SUGGEST_FIELD_TITLE", "User suggestion:");
        defaultValues.put("SUGGEST_TITLE_OWNER", "Suggested by:");
    }
    public HashMap<String, String> getDefaultLocales(){
        return this.defaultValues;
    }


    public String getLocale(Guild guild, String locale){
        if (stored.get(guild.getId()).getLocale(locale) == null) {
            return getDefault(locale);
        }
        return stored.get(guild.getId()).getLocale(locale);
    }

    public String getDefault(String locale){
        if (defaultValues.get(locale) != null){
            return defaultValues.get(locale);
        }
        return locale;
    }

    public void updateLocale(Guild guild, String locale, String localedata){
        LocaleData data = stored.get(guild.getId());
        data.updateLocale(locale, localedata);
        stored.put(guild.getId(), data);
        HashMap<String, String> allLocales = data.getAllLocales();
        try {
            sql.updateGuildLocales(guild.getId(), allLocales);
        }catch (SQLException error){
            TextUtil.sendMessage("Could not update guild locales");
        }

    }
}
