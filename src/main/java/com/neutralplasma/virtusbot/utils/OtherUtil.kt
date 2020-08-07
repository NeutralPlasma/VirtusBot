package com.neutralplasma.virtusbot.utils

import com.neutralplasma.virtusbot.VirtusBot

object OtherUtil {
    val currentVersion: String
        get() = if (VirtusBot::class.java.getPackage() != null && VirtusBot::class.java.getPackage().implementationVersion != null) VirtusBot::class.java.getPackage().implementationVersion else "UNKNOWN"
}