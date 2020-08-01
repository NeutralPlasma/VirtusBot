package com.neutralplasma.virtusbot.storage;

public class TicketInfo {

    String userid = "";
    String channelID = "";

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
