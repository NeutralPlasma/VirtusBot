package com.neutralplasma.virtusbot.storage;

import com.google.gson.Gson;
import com.neutralplasma.virtusbot.VirtusBot;
import com.neutralplasma.virtusbot.handlers.playerSettings.PlayerSettings;
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerData;
import com.neutralplasma.virtusbot.utils.TextUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.security.CodeSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.neutralplasma.virtusbot.utils.FileUtil.getPath;

public class SQL {
    private HikariDataSource hikari;
    private Gson gson = new Gson();



    /**
     * Open SQL connection to file.
     */

    public void openConnection(){

        String database = "DataBase";
        File file = new File(getPath() + database + ".db");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        }catch (Exception error){

        }
        HikariConfig config = new HikariConfig();
        config.setPoolName("Crops");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:" + getPath() + "/" + database + ".db");
        config.setConnectionTestQuery("SELECT 1");
        config.setMaxLifetime(60000); // 60 Sec
        config.setMaximumPoolSize(10); // 50 Connections (including idle connections)

        hikari = new HikariDataSource(config);
    }

    /**
     * Close SQL connection.
     */

    public void closeConnection(){
        hikari.close();
    }

    /**
     * Create table in database.
     *
     * @param tableName table name.
     * @param format Table format.
     */

    public void createTable(String tableName, String format) throws SQLException{
        try(Connection connection = hikari.getConnection()){
            String statement = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + format + ");";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                preparedStatement.execute();
            }
        }
    }



    /**
     * Caching users Settings data on startup.
     *
     * @throws SQLException If connection fails.
     */



    public Connection getConnection() throws SQLException{
        return hikari.getConnection();

    }





    public boolean addTicket(String tablename, TicketInfo info) throws SQLException{
        if(getTicket(tablename, info.userid) != null){
            return false;
        }
        try(Connection connection = hikari.getConnection()){
            String statement = "INSERT INTO " +
                    " " + tablename + " (userID, ChannelID) " +
                    "VALUES (?, ?)";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                preparedStatement.setString(1, info.userid);
                preparedStatement.setString(2, info.channelID);
                preparedStatement.execute();
                return true;
            }
        }
    }


    public TicketInfo getTicketbyChannel(String tablename, String channelID) throws SQLException{
        try(Connection connection = hikari.getConnection()) {
            String statement = "SELECT * FROM " + tablename + " WHERE channelID = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                preparedStatement.setString(1, channelID);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    TicketInfo ticket = new TicketInfo(resultSet.getString("userID"), resultSet.getString("channelID"));
                    return ticket;
                }
            }
        }
        return null;
    }

    public TicketInfo getTicket(String tablename, String usedid) throws SQLException{
        try(Connection connection = hikari.getConnection()) {
            String statement = "SELECT * FROM " + tablename + " WHERE userID = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                preparedStatement.setString(1, usedid);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    TicketInfo ticket = new TicketInfo(resultSet.getString("userID"), resultSet.getString("channelID"));
                    return ticket;
                }
            }
        }
        return null;
    }

    public LocaleData getServerLocale(String guildid) throws SQLException{
        try(Connection connection = hikari.getConnection()){
            String statement = "SELECT localedata FROM serverlocales WHERE guildID = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                preparedStatement.setString(1, guildid);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    return new LocaleData(gson.fromJson(resultSet.getString("localedata"), HashMap.class));
                }
            }
        }
        return null;
    }

    public boolean addServerLocale(String guildid) throws SQLException{
        try(Connection connection = hikari.getConnection()){
            String statement = "INSERT INTO serverlocales (guildID, localedata) VALUES (?, ?)";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                preparedStatement.setString(1, guildid);
                HashMap<String, String> data = new HashMap<>();
                data.put("TEST", "This is setup message");
                String compileddata = gson.toJson(data);
                preparedStatement.setString(2, compileddata);
                preparedStatement.execute();
            }
        }
        return true;
    }

    public void updateGuildLocales(String guildid, HashMap<String, String> data) throws SQLException{
        String json = gson.toJson(data);
        try(Connection connection = hikari.getConnection()){
            String statement = "UPDATE serverlocales " +
                    "SET localedata = ? WHERE guildID = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                preparedStatement.setString(1, json);
                preparedStatement.setString(2, guildid);
                preparedStatement.execute();
            }
        }
    }



    public boolean setSetting(String settingName, String setting ,String guild, String tablename, String type) throws SQLException{
        if (!getSetting(guild, settingName, tablename, type).equals("ERROR")){
            removeSetting(settingName, guild, tablename);
        }

        try(Connection connection = hikari.getConnection()) {
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

    public HashMap<String,String> getAllSettings(String guildID, String tablename) throws SQLException{
        HashMap<String, String> settings = new HashMap<>();
        try(Connection connection = hikari.getConnection()){
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



    public String getSetting(String guildID, String settingName, String tablename, String type) throws SQLException{
        try(Connection connection = hikari.getConnection()){
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

    public boolean removeSetting(String settingName, String guild, String tablename) {
        try (Connection connection = hikari.getConnection()) {
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


    public boolean removeTicket(String tablename, TicketInfo info) throws SQLException {
        if(getTicket(tablename, info.userid) != null) {
            try (Connection connection = hikari.getConnection()) {
                String statement = "DELETE FROM " + tablename + " WHERE userID = ? AND channelID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.setString(1, info.userid);
                    preparedStatement.setString(2, info.channelID);
                    preparedStatement.execute();
                }

            }
        }

        return false;
    }
}

