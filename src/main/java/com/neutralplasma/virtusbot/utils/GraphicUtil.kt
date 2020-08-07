package com.neutralplasma.virtusbot.utils

import java.awt.AlphaComposite
import java.awt.GradientPaint
import java.awt.image.BufferedImage

object GraphicUtil {
    @JvmStatic
    fun dye(image: BufferedImage, color: GradientPaint?): BufferedImage {
        val w = image.width
        val h = image.height
        val dyed = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
        val g = dyed.createGraphics()
        g.drawImage(image, 0, 0, null)
        g.composite = AlphaComposite.SrcAtop
        g.paint = color
        g.fillRect(0, 0, w, h)
        g.dispose()
        return dyed
    }
}