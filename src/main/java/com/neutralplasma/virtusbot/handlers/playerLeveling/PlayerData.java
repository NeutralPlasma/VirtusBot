package com.neutralplasma.virtusbot.handlers.playerLeveling;

public class PlayerData {
    private long serverID;
    private long userID;
    private long xp;
    private int level;

    public PlayerData(long userID, long serverID,long xp, int level){
        this.serverID = serverID;
        this.userID = userID;
        this.xp = xp;
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
    public long getServerID() {
        return serverID;
    }
    public long getUserID() {
        return userID;
    }
    public long getXp() {
        return xp;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public void setServerID(long serverID) {
        this.serverID = serverID;
    }
    public void setXp(long xp) {
        this.xp = xp;
    }
}
