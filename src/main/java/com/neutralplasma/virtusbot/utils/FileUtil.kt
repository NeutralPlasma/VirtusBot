package com.neutralplasma.virtusbot.utils

import com.neutralplasma.virtusbot.VirtusBot
import java.io.File

object FileUtil {
    @JvmStatic
    val path: String
        get() {
            try {
                val codeSource = VirtusBot::class.java.protectionDomain.codeSource
                val jarFile = File(codeSource.location.toURI().path)
                return jarFile.parentFile.path
            } catch (error: Exception) {
                error.printStackTrace()
            }
            return "NULL"
        }
}