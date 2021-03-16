package com.neutralplasma.virtusbot.utils;



import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class HexUtil {

    private static final List<Pattern> HEX_PATTERNS = Arrays.asList(
            Pattern.compile("<#([A-Fa-f0-9]){6}>"), // <#FFFFFF>
            Pattern.compile("&#([A-Fa-f0-9]){6}"),  // &#FFFFFF
            Pattern.compile("#([A-Fa-f0-9]){6}"),    // #FFFFFF
            Pattern.compile("#([A-Fa-f0-9]){3}")    // #FFF
    );


    private static Color parseHex(String message) {
        String parsed = message;

        for (Pattern pattern : HEX_PATTERNS) {
            Matcher matcher = pattern.matcher(parsed);
            while (matcher.find()) {
                return translateHex(cleanHex(matcher.group()));
            }
        }

        return null;
    }


    public static Color translateHex(String hex) {
        try {
            return Color.decode(hex);
        }catch (Exception error){
            return null;
        }
    }



    private static String cleanHex(String hex) {
        if (hex.startsWith("<")) {
            return hex.substring(1, hex.length() - 1);
        } else if (hex.startsWith("&")) {
            return hex.substring(1);
        } else {
            return hex;
        }
    }


    /**
     * Allows generation of a multi-part gradient with a fixed number of steps
     */
    public static class Gradient {

        private final List<Color> colors;
        private final int stepSize;
        private int step, stepIndex;

        public Gradient(List<Color> colors, int totalColors) {
            if (colors.size() < 2)
                throw new IllegalArgumentException("Must provide at least 2 colors");

            if (totalColors < 1)
                throw new IllegalArgumentException("Must have at least 1 total color");

            this.colors = colors;
            this.stepSize = totalColors / (colors.size() - 1);
            this.step = this.stepIndex = 0;
        }

        /**
         * @return the next color in the gradient
         */
        public Color next() {


            Color color;
            if (this.stepIndex + 1 < this.colors.size()) {
                Color start = this.colors.get(this.stepIndex);
                Color end = this.colors.get(this.stepIndex + 1);
                float interval = (float) this.step / this.stepSize;

                color = getGradientInterval(start, end, interval);
            } else {
                color = this.colors.get(this.colors.size() - 1);
            }

            this.step += 1;
            if (this.step >= this.stepSize) {
                this.step = 0;
                this.stepIndex++;
            }

            return color;
        }

        /**
         * Gets a color along a linear gradient between two colors
         *
         * @param start The start color
         * @param end The end color
         * @param interval The interval to get, between 0 and 1 inclusively
         * @return A Color at the interval between the start and end colors
         */
        public static Color getGradientInterval(Color start, Color end, float interval) {
            if (0 > interval || interval > 1)
                throw new IllegalArgumentException("Interval must be between 0 and 1 inclusively.");

            int r = (int) (end.getRed() * interval + start.getRed() * (1 - interval));
            int g = (int) (end.getGreen() * interval + start.getGreen() * (1 - interval));
            int b = (int) (end.getBlue() * interval + start.getBlue() * (1 - interval));

            return new Color(r, g, b);
        }

    }

    /**
     * Allows generation of a rainbow gradient with a fixed numbef of steps
     */
    public static class Rainbow {

        private final float hueStep, saturation, brightness;
        private float hue;

        public Rainbow(int totalColors, float saturation, float brightness) {
            if (totalColors < 1)
                throw new IllegalArgumentException("Must have at least 1 total color");

            if (0.0F > saturation || saturation > 1.0F)
                throw new IllegalArgumentException("Saturation must be between 0.0 and 1.0");

            if (0.0F > brightness || brightness > 1.0F)
                throw new IllegalArgumentException("Saturation must be between 0.0 and 1.0");

            this.hueStep = 1.0F / totalColors;
            this.saturation = saturation;
            this.brightness = brightness;
            this.hue = 0;
        }

        public Rainbow(int totalColors) {
            this(totalColors, 1.0F, 1.0F);
        }

        /**
         * @return the next color in the gradient
         */
        public Color next() {
            Color color = Color.getHSBColor(this.hue, this.saturation, this.brightness);
            this.hue += this.hueStep;
            return color;
        }
        public void shift(int shift){
            this.hue += hueStep * shift;
        }
    }

}
