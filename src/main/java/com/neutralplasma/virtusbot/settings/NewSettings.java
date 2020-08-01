package com.neutralplasma.virtusbot.settings;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.HashMap;

public class NewSettings {

    HashMap<String, String> stringData = new HashMap<>();
    HashMap<String, Long> longData = new HashMap<>();
    HashMap<String, Integer> intData = new HashMap<>();

    public NewSettings(){

    }

    public String getString(String setting){
        return stringData.get(setting);
    }

    public Long getLong(String setting){
        return longData.get(setting);
    }

    public int getInt(String setting){
        return intData.get(setting);
    }

    public void addIntData(String setting, int data){
        intData.put(setting, data);
    }

    public void addLongData(String setting, long data){
        longData.put(setting, data);
    }

    public void addStringData(String setting, String data){
        stringData.put(setting, data);
    }

}
