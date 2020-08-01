package com.neutralplasma.virtusbot.storage;


import java.sql.SQLException;

public class TicketStorage {
    private SQL sql;
    private final String tableName = "TicketData";

    public TicketStorage(SQL sql){
        this.sql = sql;
    }

    public void setup(){
        sql.openConnection();
        try {
            //sql.createTable("base");
            sql.createTable(tableName, "userID TEXT, channelID TEXT");
        }catch (Exception error){
            error.printStackTrace();
        }
    }
    public void writeSettings(String userID, String channelID)
    {
        TicketInfo info = new TicketInfo(userID, channelID);
        try {
            sql.addTicket(tableName, info);
        }catch (Exception error){
            error.printStackTrace();
        }
    }

    public void deleteTicket(String userID, String channelID){
        TicketInfo info = new TicketInfo(userID, channelID);
        try {
            sql.removeTicket(tableName, info);
        }catch (SQLException sqlerror){
            sqlerror.printStackTrace();
        }
    }

    public TicketInfo getTicket(String channelID){
        try {
            if (!channelID.isEmpty()) {
                TicketInfo ticketid = sql.getTicketbyChannel(tableName, channelID);
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
                TicketInfo ticketid = sql.getTicket(tableName, userID);
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

}
