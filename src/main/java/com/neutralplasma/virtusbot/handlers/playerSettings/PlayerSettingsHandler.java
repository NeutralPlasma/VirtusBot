package com.neutralplasma.virtusbot.handlers.playerSettings;

import com.google.gson.Gson;
import com.neutralplasma.virtusbot.storage.dataStorage.SQL;
import com.neutralplasma.virtusbot.storage.dataStorage.StorageHandler;
import com.neutralplasma.virtusbot.utils.TextUtil;
import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerSettingsHandler {

    private StorageHandler sql;
    private Gson gson = new Gson();

    private HashMap<String, PlayerSettings> pSettings = new HashMap<>();

    public PlayerSettingsHandler(StorageHandler sql){
        this.sql = sql;

        try {
            sql.createTable("PlayerSettings", "userID TEXT, settings TEXT");
        }catch (SQLException error){
            error.printStackTrace();
        }

        try {
            cachePlayerSettings();
        }catch (SQLException error){
            error.printStackTrace();
        }

        pSettingsUpdater();

    }

    public void pSettingsUpdater(){
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                settingsUpdater.run();
            }

        }, 100, 60000);
    }

    Runnable settingsUpdater = () -> {
        try {
            syncSettings();
        }catch (Exception ignored){}
    };

    public void syncSettings() throws SQLException{
        HashMap<String, PlayerSettings> data = new HashMap<>(pSettings);

        try(Connection connection = sql.getConnection()){
            String statement = "DELETE FROM PlayerSettings;";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                preparedStatement.execute();
            }
            for(String userinfo : data.keySet()){
                PlayerSettings udata = data.get(userinfo);
                String settings = gson.toJson(udata);
                String statement2 = "INSERT INTO PlayerSettings (" +
                        "userID," +
                        "settings) VALUES (?, ?)";
                try(PreparedStatement preparedStatement = connection.prepareStatement(statement2)){
                    preparedStatement.setString(1, userinfo);
                    preparedStatement.setString(2, settings);
                    preparedStatement.execute();
                }
            }
        }
    }

    public void cachePlayerSettings() throws SQLException{
        try(Connection connection = sql.getConnection()){
            String statement = "SELECT * from PlayerSettings;";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                int amount = 0;
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    amount++;
                    try {
                        String ID = resultSet.getString("userID");
                        String settings = resultSet.getString("settings");
                        PlayerSettings playerSettings = gson.fromJson(settings, PlayerSettings.class);

                        pSettings.put(ID, playerSettings);
                    }catch (Exception ignored) {}
                }
                TextUtil.sendMessage("Loaded: " + amount + " playerSettings from database.");
            }
        }
    }


    public PlayerSettings getUserSettings(User user){
        return pSettings.get(user.getId());
    }
    public void updateUserSettings(User user, PlayerSettings playerSettings){
        pSettings.put(user.getId(), playerSettings);
    }
    public void addUserSettings(User user, PlayerSettings playerSettings){
        pSettings.put(user.getId(), playerSettings);
    }



    public PlayerSettings getSettings(User user){
        return getUserSettings(user);
    }

    public void addUser(User user, PlayerSettings playerSettings){
        addUserSettings(user, playerSettings);
    }

    public void updateUser(User user, PlayerSettings playerSettings){
        updateUserSettings(user, playerSettings);
    }


}
