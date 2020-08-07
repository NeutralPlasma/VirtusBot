package com.neutralplasma.virtusbot.storage.ticket;


import com.neutralplasma.virtusbot.storage.dataStorage.StorageHandler;
import com.neutralplasma.virtusbot.storage.ticket.TicketInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TicketStorage {
    private StorageHandler sql;
    private final String tableName = "TicketData";

    public TicketStorage(StorageHandler sql){
        this.sql = sql;
    }

    public void setup(){
        try {
            sql.createTable(tableName, "userID TEXT, channelID TEXT");
        }catch (Exception error){
            error.printStackTrace();
        }
    }
    public void writeSettings(String userID, String channelID){
        TicketInfo info = new TicketInfo(userID, channelID);
        try {
            addTicket(info);
        }catch (Exception error){
            error.printStackTrace();
        }
    }

    public void deleteTicket(String userID, String channelID){
        TicketInfo info = new TicketInfo(userID, channelID);
        try {
            removeTicket(info);
        }catch (SQLException sqlerror){
            sqlerror.printStackTrace();
        }
    }

    public TicketInfo getTicketChannel(String channelID){
        try {
            if (!channelID.isEmpty()) {
                TicketInfo ticketid = getTicketbyChannel(channelID);
                if (ticketid != null) {
                    return ticketid;
                }
            }

            return null;
        }catch (Exception error){
            error.printStackTrace();
        }
        return null;
    }

    public String getTicketID(String userID){
        try {
            if (!userID.isEmpty()) {
                TicketInfo ticketid = getTicketSQL(userID);
                if (ticketid != null) {
                    return ticketid.getChannelID();
                }
            }
            return null;
        }catch (Exception error){
            error.printStackTrace();
        }
        return null;
    }

    public TicketInfo getTicketSQL(String usedid) throws SQLException{
        try(Connection connection = sql.getConnection()) {
            String statement = "SELECT * FROM " + tableName + " WHERE userID = ?";
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

    public TicketInfo getTicketbyChannel(String channelID) throws SQLException{
        try(Connection connection = sql.getConnection()) {
            String statement = "SELECT * FROM " + tableName + " WHERE channelID = ?";
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

    public boolean addTicket(TicketInfo info) throws SQLException{
        if(getTicketSQL(info.userid) != null){
            return false;
        }
        try(Connection connection = sql.getConnection()){
            String statement = "INSERT INTO " +
                    " " + tableName + " (userID, ChannelID) " +
                    "VALUES (?, ?)";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                preparedStatement.setString(1, info.userid);
                preparedStatement.setString(2, info.channelID);
                preparedStatement.execute();
                return true;
            }
        }
    }

    public boolean removeTicket(TicketInfo info) throws SQLException {
        if(getTicketSQL(info.userid) != null) {
            try (Connection connection = sql.getConnection()) {
                String statement = "DELETE FROM " + tableName + " WHERE userID = ? AND channelID = ?";
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
