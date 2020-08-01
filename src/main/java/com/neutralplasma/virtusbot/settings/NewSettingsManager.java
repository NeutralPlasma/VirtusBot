package com.neutralplasma.virtusbot.settings;

import com.neutralplasma.virtusbot.storage.SQL;
import com.neutralplasma.virtusbot.utils.TextUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.awt.*;
import java.sql.SQLException;
import java.util.HashMap;

public class NewSettingsManager {
    private SQL sql;
    private HashMap<String, NewSettings> loadedSettings = new HashMap<>();

    public NewSettingsManager(SQL sql){
        this.sql = sql;

        try {
            sql.createTable("ServerSettings",
                    "settingName TEXT," +
                    "guildID TEXT," +
                    "data TEXT," +
                    "type TEXT");
        }catch (SQLException error){
            TextUtil.sendMessage(error.getMessage());
        }
    }

    public boolean addStringData(Guild guild, String settingName, String setting){
        NewSettings settings = getSettings(guild);
        try {
            sql.setSetting(settingName, setting, guild.getId(), "ServerSettings", "STRING");
            settings.addStringData(settingName, setting);
            loadedSettings.put(guild.getId(), settings);
            return true;
        }catch (SQLException error){
            error.printStackTrace();
            return false;
        }
    }


    public TextChannel getTextChannel(Guild guild, String setting){
        NewSettings settings = getSettings(guild);

        TextChannel channel;
        try {
            if(!settings.getString(setting).isEmpty()) {
                channel = guild.getTextChannelById(settings.getString(setting));
                if (channel == null) {
                    channel = guild.getTextChannelById(settings.getLong(setting));
                }
                return channel;
            }
            return null;
        }catch (NullPointerException error){
            return null;
        }
    }

    public Role getRole(Guild guild, String setting){
        NewSettings settings = getSettings(guild);

        Role role;
        try {
            if (!settings.getString(setting).isEmpty()) {
                role = guild.getRoleById(settings.getString(setting));
                if (role == null) {
                    role = guild.getRoleById(settings.getLong(setting));
                }
                return role;
            }
            return null;
        }catch(NullPointerException error){
            return null;
        }
    }

    public VoiceChannel getVoiceChannel(Guild guild, String setting){
        NewSettings settings = getSettings(guild);
        if(!settings.getString(setting).isEmpty()) {
            return guild.getVoiceChannelById(settings.getString(setting));
        }
        return null;
    }

    public String getData(Guild guild, String setting){
        NewSettings settings = getSettings(guild);

        return settings.getString(setting);
    }


    public NewSettings getSettings(Guild guild){
        NewSettings settings = loadedSettings.get(guild.toString());
        if(settings == null){
            settings = new NewSettings();
            try {
                HashMap<String, String> dataset=  sql.getAllSettings(guild.getId(), "ServerSettings");
                for(String string : dataset.keySet()){
                    settings.addStringData(string, dataset.get(string));
                }
            }catch (SQLException error){
                settings = new NewSettings();
            }
        }
        loadedSettings.put(guild.getId(), settings);
        return settings;
    }

    public void loadSettings(JDA jda){
        for (Guild guild : jda.getGuilds()){
            try {
                HashMap<String, String> settings = sql.getAllSettings(guild.getId(), "ServerSettings");
                NewSettings settings1 = new NewSettings();
                for(String string : settings.keySet()){
                    settings1.addStringData(string, settings.get(string));
                }
                loadedSettings.put(guild.getId(), settings1);
            }catch (SQLException error){
                error.printStackTrace();
            }
        }
    }

}
