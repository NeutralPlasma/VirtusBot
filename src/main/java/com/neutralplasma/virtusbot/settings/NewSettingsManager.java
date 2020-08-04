package com.neutralplasma.virtusbot.settings;

import com.neutralplasma.virtusbot.storage.dataStorage.SQL;
import com.neutralplasma.virtusbot.storage.dataStorage.StorageHandler;
import com.neutralplasma.virtusbot.utils.TextUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class NewSettingsManager {
    private StorageHandler storageHandler;
    private HashMap<String, NewSettings> loadedSettings = new HashMap<>();

    public NewSettingsManager(StorageHandler storageHandler){
        this.storageHandler = storageHandler;

        try {
            storageHandler.createTable("ServerSettings",
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
            setSetting(settingName, setting, guild.getId(), "ServerSettings", "STRING");
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
                HashMap<String, String> dataset=  getAllSettings(guild.getId(), "ServerSettings");
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
                HashMap<String, String> settings = getAllSettings(guild.getId(), "ServerSettings");
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


    /*
        SQL STUFF
     */
    public boolean removeSetting(String settingName, String guild, String tablename) {
        try (Connection connection = storageHandler.getConnection()) {
            String statement = "DELETE FROM " + tablename + " WHERE guildID = ? AND settingName = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, guild);
                preparedStatement.setString(2, settingName);
                preparedStatement.execute();
                return true;
            }

        }catch (SQLException error){
            return false;
        }
    }

    public String getSetting(String guildID, String settingName, String tablename, String type) throws SQLException{
        try(Connection connection = storageHandler.getConnection()){
            String statement = "SELECT * FROM " + tablename + " WHERE guildID = ? AND settingName = ? AND type = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                preparedStatement.setString(1, guildID);
                preparedStatement.setString(2, settingName);
                preparedStatement.setString(3, type);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    return resultSet.getString("data");
                }
            }
        }
        return "ERROR";
    }

    public HashMap<String,String> getAllSettings(String guildID, String tablename) throws SQLException{
        HashMap<String, String> settings = new HashMap<>();
        try(Connection connection = storageHandler.getConnection()){
            String statement = "SELECT * FROM " + tablename + " WHERE guildID = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                preparedStatement.setString(1, guildID);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    // To be done.
                    settings.put(resultSet.getString("settingName"), resultSet.getString("data"));
                }
            }
        }
        settings.put("TEST", "TEST2");
        return settings;
    }

    public boolean setSetting(String settingName, String setting ,String guild, String tablename, String type) throws SQLException{
        if (!getSetting(guild, settingName, tablename, type).equals("ERROR")){
            removeSetting(settingName, guild, tablename);
        }

        try(Connection connection = storageHandler.getConnection()) {
            String statement = "INSERT INTO " +
                    " " + tablename + " (data, guildID, settingName, type) " +
                    "VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, setting);
                preparedStatement.setString(2, guild);
                preparedStatement.setString(3, settingName);
                preparedStatement.setString(4, type);
                preparedStatement.execute();
                return true;
            }
        }
    }

}
