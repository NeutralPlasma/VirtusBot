package com.neutralplasma.virtusbot.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GraphicUtil {


    public static BufferedImage dye(BufferedImage image, GradientPaint color) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage dyed = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dyed.createGraphics();
        g.drawImage(image, 0,0, null);
        g.setComposite(AlphaComposite.SrcAtop);
        g.setPaint(color);
        g.fillRect(0,0,w,h);
        g.dispose();
        return dyed;
    }
}
