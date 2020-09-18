package com.neutralplasma.virtusbot.utils

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.RenderingHints
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage

object TextUtil {
    @JvmStatic
    fun sendMessage(message: String?) {
        println(message)
    }

    @JvmStatic
    fun formatTiming(timing: Long, maximum: Long): String {
        var timing = timing
        timing = timing.coerceAtMost(maximum) / 1000
        val seconds = timing % 60
        timing /= 60
        val minutes = timing % 60
        timing /= 60
        val hours = timing
        return if (maximum >= 3600000L) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%d:%02d", minutes, seconds)
        }
    }

    @JvmStatic
    fun formatTiming(timing: Long): String {
        var timing = timing
        val weeks = (timing / (1000 * 60 * 60 * 24 * 7)).toInt()
        timing -= weeks * 604800 * 1000
        val days = (timing / (1000 * 60 * 60 * 24)).toInt()
        timing -= days * 86400 * 1000
        val hours = (timing / (1000 * 60 * 60) % 24).toInt()
        timing -= hours * 3600 * 1000
        val minutes = (timing / (1000 * 60) % 60).toInt()
        timing -= hours * 60 * 1000
        val seconds = (timing / 1000).toInt() % 60
        return String.format("%d:%02d:%02d:%02d:%02d", weeks, days, hours, minutes, seconds)
    }

    @JvmStatic
    fun filter(input: String): String {
        return input.replace("\u202E", "")
                .replace("@everyone", "@\u0435veryone") // some retarded e so it doesnt ping everyone.
                .replace("@here", "@h\u0435re") // some retarded e so it doesnt ping everyone.
                .trim { it <= ' ' }
    }


}