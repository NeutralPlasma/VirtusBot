package com.neutralplasma.virtusbot.handlers.playerSettings;

public class PlayerSettings {
    private String avatarBackgroundImage;
    private boolean darkTheme;


    public PlayerSettings(boolean darkTheme, String avatarBackground){
        this.darkTheme = darkTheme;
        this.avatarBackgroundImage = avatarBackground;
    }

    public boolean isDarkTheme() {
        return darkTheme;
    }
    public String getAvatarBackgroundImage() {
        return avatarBackgroundImage;
    }

    public void setAvatarBackgroundImage(String avatarBackgroundImage) {
        this.avatarBackgroundImage = avatarBackgroundImage;
    }
    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
    }
}
