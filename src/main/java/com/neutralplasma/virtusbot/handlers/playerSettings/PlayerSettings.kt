package com.neutralplasma.virtusbot.handlers.playerSettings

import java.awt.Color

class PlayerSettings(private var darkTheme: Boolean, private var avatarBackgroundImage: String, private var color1: Color, private var color2: Color) {
    fun isDarkTheme(): Boolean {
        return darkTheme
    }

    fun getAvatarBackgroundImage(): String {
        return avatarBackgroundImage
    }

    fun getColor1(): Color? {
        return color1
    }

    fun getColor2(): Color? {
        return color2
    }

    fun setAvatarBackgroundImage(avatarBackgroundImage: String) {
        this.avatarBackgroundImage = avatarBackgroundImage
    }

    fun setDarkTheme(darkTheme: Boolean) {
        this.darkTheme = darkTheme
    }

    fun setColor1(color1: Color) {
        this.color1 = color1
    }

    fun setColor2(color2: Color) {
        this.color2 = color2
    }

}