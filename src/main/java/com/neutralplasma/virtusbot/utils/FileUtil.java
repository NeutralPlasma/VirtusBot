package com.neutralplasma.virtusbot.utils;

import com.neutralplasma.virtusbot.VirtusBot;

import java.io.File;
import java.security.CodeSource;

public class FileUtil {

    public static String getPath(){
        try{
            CodeSource codeSource = VirtusBot.class.getProtectionDomain().getCodeSource();
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            String jarDir = jarFile.getParentFile().getPath();
            return jarDir;
        }catch (Exception error){
            error.printStackTrace();
        }
        return "NULL";
    }
}
