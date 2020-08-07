package com.neutralplasma.virtusbot.utils;

public class MathUtil {
    public static Long formatTimeLong(String[] numbers) {
        Long totalNumber = 0L;
        for (int index = 0; index < numbers.length; index++) {

            String text = numbers[index];
            text = text.toUpperCase();
            if (index >= 2) {
                if (text.contains("Y")) {
                    text = text.replace("Y", "");
                    int rawnumber = Integer.valueOf(text);
                    Long number = rawnumber * Long.valueOf("31536000");
                    totalNumber += number;
                } else if (text.contains("W")) {
                    text = text.replace("W", "");
                    int rawnumber = Integer.valueOf(text);
                    Long number = rawnumber * Long.valueOf("604800");
                    totalNumber += number;
                } else if (text.contains("D")) {
                    text = text.replace("D", "");
                    int rawnumber = Integer.valueOf(text);
                    Long number = rawnumber * Long.valueOf("86400");
                    totalNumber += number;
                } else if (text.contains("H")) {
                    text = text.replace("H", "");
                    int rawnumber = Integer.valueOf(text);
                    Long number = rawnumber * Long.valueOf("3600");
                    totalNumber += number;
                } else if (text.contains("M")) {
                    text = text.replace("M", "");
                    int rawnumber = Integer.valueOf(text);
                    Long number = rawnumber * Long.valueOf("60");
                    totalNumber += number;
                } else if (text.contains("S")) {
                    text = text.replace("S", "");
                    Long rawnumber = Long.valueOf(text);
                    totalNumber += rawnumber;
                }
            }
        }
        return totalNumber;
    }
}
