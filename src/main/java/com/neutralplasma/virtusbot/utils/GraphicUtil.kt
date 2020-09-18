package com.neutralplasma.virtusbot.utils

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.GradientPaint
import java.awt.RenderingHints
import java.awt.geom.RoundRectangle2D
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

    @JvmStatic
    fun makeRoundedCorner(image: BufferedImage, cornerRadius: Int, transparent: Boolean): BufferedImage {
        val w = image.width
        val h = image.height
        val output = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
        val g2 = output.createGraphics()
        g2.composite = AlphaComposite.Src
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.color = Color.WHITE
        g2.fill(RoundRectangle2D.Float(0f, 0f, w.toFloat(), h.toFloat(), cornerRadius.toFloat(), cornerRadius.toFloat()))
        if (transparent) {
            g2.composite = AlphaComposite.SrcIn
        } else {
            g2.composite = AlphaComposite.SrcAtop
        }
        g2.drawImage(image, 0, 0, null)
        g2.dispose()
        return output
    }
}