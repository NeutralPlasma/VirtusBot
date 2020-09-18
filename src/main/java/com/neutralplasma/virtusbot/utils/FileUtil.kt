package com.neutralplasma.virtusbot.utils

import com.neutralplasma.virtusbot.VirtusBot
import java.awt.Image
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

object FileUtil {
    private val storedImages: HashMap<String,BufferedImage> = hashMapOf()


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

    @JvmStatic
    fun getImage(path: String): BufferedImage{
        val image = storedImages[path]
        if(image != null){
            return image
        }else {
            val image2 = ImageIO.read(VirtusBot.javaClass.getResource("/resources/images/$path"))
            if (image2 != null) {
                storedImages[path] = image2
            }
            return image2
        }
    }


}