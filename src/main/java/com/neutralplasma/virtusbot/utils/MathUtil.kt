package com.neutralplasma.virtusbot.utils

object MathUtil {
    @JvmStatic
    fun formatTimeLong(numbers: Array<String>): Long {
        var totalNumber = 0L
        for (index in numbers.indices) {
            var text = numbers[index]
            text = text.toUpperCase()
            if (index >= 2) {
                if (text.contains("Y")) {
                    text = text.replace("Y", "")
                    val rawnumber = Integer.valueOf(text)
                    val number = rawnumber * java.lang.Long.valueOf("31536000")
                    totalNumber += number
                } else if (text.contains("W")) {
                    text = text.replace("W", "")
                    val rawnumber = Integer.valueOf(text)
                    val number = rawnumber * java.lang.Long.valueOf("604800")
                    totalNumber += number
                } else if (text.contains("D")) {
                    text = text.replace("D", "")
                    val rawnumber = Integer.valueOf(text)
                    val number = rawnumber * java.lang.Long.valueOf("86400")
                    totalNumber += number
                } else if (text.contains("H")) {
                    text = text.replace("H", "")
                    val rawnumber = Integer.valueOf(text)
                    val number = rawnumber * java.lang.Long.valueOf("3600")
                    totalNumber += number
                } else if (text.contains("M")) {
                    text = text.replace("M", "")
                    val rawnumber = Integer.valueOf(text)
                    val number = rawnumber * java.lang.Long.valueOf("60")
                    totalNumber += number
                } else if (text.contains("S")) {
                    text = text.replace("S", "")
                    val rawnumber = java.lang.Long.valueOf(text)
                    totalNumber += rawnumber
                }
            }
        }
        return totalNumber
    }
}