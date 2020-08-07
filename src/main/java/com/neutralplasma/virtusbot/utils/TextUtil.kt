package com.neutralplasma.virtusbot.utils;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TextUtil {

    public static void sendMessage(String message){
        System.out.println(message);
    }

    public static String formatTiming(long timing, long maximum) {
        timing = Math.min(timing, maximum) / 1000;

        long seconds = timing % 60;
        timing /= 60;
        long minutes = timing % 60;
        timing /= 60;
        long hours = timing;

        if (maximum >= 3600000L) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    public static String formatTiming(long timing) {

        int weeks = (int) (timing / (1000*60*60*24*7));
        timing = timing - weeks * 604800 * 1000;

        int days = (int) (timing / (1000*60*60*24));
        timing = timing - days * 86400 * 1000;

        int hours   = (int) ((timing / (1000*60*60)) % 24);
        timing = timing - hours * 3600 * 1000;

        int minutes = (int) ((timing / (1000*60)) % 60);
        timing = timing - hours * 60 * 1000;

        int seconds = (int) (timing / 1000) % 60 ;


        return String.format("%d:%02d:%02d:%02d:%02d", weeks, days, hours, minutes, seconds);

    }


    public static String filter(String input){
        return input.replace("\u202E","")
                .replace("@everyone", "@\u0435veryone") // some retarded e so it doesnt ping everyone.
                .replace("@here", "@h\u0435re") // some retarded e so it doesnt ping everyone.
                .trim();
    }

    public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius, boolean transparent) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();

        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

        if(transparent){
            g2.setComposite(AlphaComposite.SrcIn);
        }else{
            g2.setComposite(AlphaComposite.SrcAtop);
        }
        g2.drawImage(image, 0, 0, null);

        g2.dispose();

        return output;
    }

}