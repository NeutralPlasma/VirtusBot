package com.neutralplasma.virtusbot.storage.config;


import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Config {

    private final File configFile = new File("config.json");
    private JSONObject configObject;

    public Config() {
        if (!configFile.exists()) {
            create(); // If the config.json file doesn't exist, generate it.
            System.out.println("Created a config file. Please fill in the credentials.");
            System.exit(0);
        }

        JSONObject object = read(configFile);
        if (object.has("token") && object.has("prefix") && object.has("authorid")) {
            configObject = object;
        } else {
            create(); // If a value is missing, regenerate the config file.
            System.err.println("A value was missing in the config file! Regenerating..");
            System.exit(1);
        }
    }

    public String getValue(String key) {
        return configObject == null ? null : configObject.get(key).toString();
    }
    Boolean getBoolean(String key) {
        return configObject == null ? null : configObject.getBoolean(key);
    }
    Integer getInt(String key) {
        return configObject == null ? null : configObject.getInt(key);
    }

    private void create() {
        try {
            Files.write(Paths.get(configFile.getPath()),
                    new JSONObject()
                            .put("authorid", "")
                            .put("prefix", ".")
                            .put("token", "")
                            .put("youtube_key", "")
                            .put("use_mysql", false)
                            .put("use_SSL", false)
                            .put("database_ip", "localhost")
                            .put("database_port", "3306")
                            .put("database_name", "DATABASE")
                            .put("database_user", "USER")
                            .put("database_password", "PASSWORD")
                            .toString(4)
                            .getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public JSONObject read(File file) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(new String(Files.readAllBytes(Paths.get(file.getPath())), "UTF-8"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return obj;
    }
}