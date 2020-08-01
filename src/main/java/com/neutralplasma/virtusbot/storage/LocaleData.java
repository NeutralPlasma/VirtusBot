package com.neutralplasma.virtusbot.storage;


import java.util.HashMap;

public class LocaleData {
    HashMap<String, String> localedata;

    public LocaleData(HashMap<String, String> localedata){
        this.localedata = localedata;
    }

    public String getLocale(String locale){
        try {
            String data = localedata.get(locale);

            if (data != null) {
                return data;
            }
        }catch (NullPointerException error){
            return null;
        }
        return null;
    }

    public void updateLocale(String locale, String localedata){
        this.localedata.put(locale, localedata);
    }

    public HashMap<String, String> getAllLocales(){
        return this.localedata;
    }
}
