package com.neutralplasma.virtusbot.utils

import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.VoiceChannel

/**
 *
 *
 */
object FormatUtil {
    fun formatTime(duration: Long): String {
        if (duration == Long.MAX_VALUE) return "LIVE"
        var seconds = Math.round(duration / 1000.0)
        val hours = seconds / (60 * 60)
        seconds %= 60 * 60.toLong()
        val minutes = seconds / 60
        seconds %= 60
        return (if (hours > 0) "$hours:" else "") + (if (minutes < 10) "0$minutes" else minutes) + ":" + if (seconds < 10) "0$seconds" else seconds
    }

    fun progressBar(percent: Double): String {
        var str = ""
        for (i in 0..11) str += if (i == (percent * 12).toInt()) "\uD83D\uDD18" // 🔘
        else "▬"
        return str
    }

    fun volumeIcon(volume: Int): String {
        if (volume == 0) return "\uD83D\uDD07" // 🔇
        if (volume < 30) return "\uD83D\uDD08" // 🔈
        return if (volume < 70) "\uD83D\uDD09" else "\uD83D\uDD0A" // 🔉
        // 🔊
    }

    @JvmStatic
    fun listOfTChannels(list: List<TextChannel>, query: String): String {
        var out = " Multiple text channels found matching \"$query\":"
        var i = 0
        while (i < 6 && i < list.size) {
            out += """
 - ${list[i].name} (<#${list[i].id}>)"""
            i++
        }
        if (list.size > 6) out += """
**And ${list.size - 6} more...**"""
        return out
    }

    fun listOfVChannels(list: List<VoiceChannel>, query: String): String {
        var out = " Multiple voice channels found matching \"$query\":"
        var i = 0
        while (i < 6 && i < list.size) {
            out += """
 - ${list[i].name} (ID:${list[i].id})"""
            i++
        }
        if (list.size > 6) out += """
**And ${list.size - 6} more...**"""
        return out
    }

    @JvmStatic
    fun listOfRoles(list: List<Role>, query: String): String {
        var out = " Multiple text channels found matching \"$query\":"
        var i = 0
        while (i < 6 && i < list.size) {
            out += """
 - ${list[i].name} (ID:${list[i].id})"""
            i++
        }
        if (list.size > 6) out += """
**And ${list.size - 6} more...**"""
        return out
    }

    fun filter(input: String): String {
        return input.replace("@everyone", "@\u0435veryone").replace("@here", "@h\u0435re").trim { it <= ' ' } // cyrillic letter e
    }
}