package com.neutralplasma.virtusbot.handlers;

import com.neutralplasma.virtusbot.handlers.playerSettings.PlayerSettings;
import com.neutralplasma.virtusbot.storage.SQL;
import com.neutralplasma.virtusbot.utils.TextUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BlackList {
    private SQL sql;
    private final String TableName = "BlackList";
    private ArrayList<String> blackList = new ArrayList<>();

    public BlackList(SQL sql){
        this.sql = sql;

        try{
            sql.createTable(TableName, "UserID TEXT");
        }catch (SQLException error){
            error.printStackTrace();
        }

        try{
            cacheBlackList();
        }catch (SQLException error){
            error.printStackTrace();
        }

        blackListUpdater();

    }

    public boolean isBlackListed(String userID){
        return blackList.contains(userID);
    }

    public void addToBlackList(String userID){
        if(!isBlackListed(userID)){
            blackList.add(userID);
        }
    }
    public void removeFromBlackList(String userID){
        if(isBlackListed(userID)){
            blackList.remove(userID);
        }
    }

    public void blackListUpdater(){
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                syncer.run();
            }

        }, 100, 60000);
    }

    Runnable syncer = () -> {
        try {
            syncBlackList();
        }catch (Exception ignored){}
    };

    public void syncBlackList() throws SQLException{
        List<String> data = new ArrayList<>(blackList);

        try(Connection connection = sql.getConnection()){
            String statement = "DELETE FROM PlayerSettings;";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                preparedStatement.execute();
            }
            for(String userinfo : data){
                String statement2 = "INSERT INTO " + TableName + " (" +
                        "userID)" +
                        " VALUES (?)";
                try(PreparedStatement preparedStatement = connection.prepareStatement(statement2)){
                    preparedStatement.setString(1, userinfo);
                    preparedStatement.execute();
                }
            }
        }
    }

    public void cacheBlackList() throws SQLException{
        try(Connection connection = sql.getConnection()){
            String statement = "SELECT * from " + TableName + ";";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                int amount = 0;
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    amount++;
                    try {
                        String ID = resultSet.getString("userID");
                        blackList.add(ID);
                    }catch (Exception ignored) {}
                }
                TextUtil.sendMessage("Loaded: " + amount + " blacklisted users from database.");
            }
        }
    }
}
