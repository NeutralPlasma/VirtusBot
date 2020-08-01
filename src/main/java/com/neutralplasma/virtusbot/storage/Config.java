package com.neutralplasma.virtusbot.storage;


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

    String getValue(String key) {
        return configObject == null ? null : configObject.get(key).toString();
    }

    private void create() {
        try {
            Files.write(Paths.get(configFile.getPath()),
                    new JSONObject()
                            .put("authorid", "")
                            .put("prefix", ".")
                            .put("token", "")
                            .put("youtube_key", "")
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
