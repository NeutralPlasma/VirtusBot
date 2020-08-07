package com.neutralplasma.virtusbot.storage.ticket;

public class TicketInfo {

    public String userid = "";
    public String channelID = "";

    public TicketInfo(String userid, String channelID){
        this.userid = userid;
        this.channelID = channelID;
    }


    public String getUserid(){
        return this.userid;
    }

    public String getChannelID(){
        return this.channelID;
    }

}
