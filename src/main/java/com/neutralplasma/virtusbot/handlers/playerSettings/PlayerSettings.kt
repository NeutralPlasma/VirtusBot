package com.neutralplasma.virtusbot.handlers.playerSettings;

import org.apache.commons.codec.binary.Hex;

import java.awt.*;

public class PlayerSettings {
    private String avatarBackgroundImage;
    private boolean darkTheme;
    private Color color1;
    private Color color2;


    public PlayerSettings(boolean darkTheme, String avatarBackground, Color color1, Color color2){
        this.darkTheme = darkTheme;
        this.avatarBackgroundImage = avatarBackground;
        this.color1 = color1;
        this.color2 = color2;
    }

    public boolean isDarkTheme() {
        return darkTheme;
    }
    public String getAvatarBackgroundImage() {
        return avatarBackgroundImage;
    }
    public Color getColor1() {
        return color1;
    }
    public Color getColor2() {
        return color2;
    }

    public void setAvatarBackgroundImage(String avatarBackgroundImage) {
        this.avatarBackgroundImage = avatarBackgroundImage;
    }
    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
    }
    public void setColor1(Color color1) {
        this.color1 = color1;
    }
    public void setColor2(Color color2) {
        this.color2 = color2;
    }
}
