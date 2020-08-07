package com.neutralplasma.virtusbot.utils;

import com.neutralplasma.virtusbot.VirtusBot;

public class OtherUtil {

    public static String getCurrentVersion(){
        if(VirtusBot.class.getPackage()!=null && VirtusBot.class.getPackage().getImplementationVersion()!=null)
            return VirtusBot.class.getPackage().getImplementationVersion();
        else
            return "UNKNOWN";
    }
}
