package com.neutralplasma.virtusbot.storage.config

import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

class Config {
    private val configFile = File("config.json")
    private lateinit var configObject: JSONObject
    fun getValue(key: String?): String? {
        return configObject.get(key).toString()
    }

    fun getBoolean(key: String?): Boolean? {
        return configObject.getBoolean(key)
    }

    fun getInt(key: String?): Int? {
        return configObject.getInt(key)
    }

    private fun create() {
        try {
            Files.write(Paths.get(configFile.path),
                    JSONObject()
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
                            .toByteArray())
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    fun read(file: File): JSONObject? {
        var obj: JSONObject? = null
        try {
            obj = JSONObject(String(Files.readAllBytes(Paths.get(file.path))))
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return obj
    }

    init {
        if (!configFile.exists()) {
            create() // If the config.json file doesn't exist, generate it.
            println("Created a config file. Please fill in the credentials.")
            System.exit(0)
        }
        val `object` = read(configFile)
        if (`object`!!.has("token") && `object`.has("prefix") && `object`.has("authorid")) {
            configObject = `object`
        } else {
            create() // If a value is missing, regenerate the config file.
            System.err.println("A value was missing in the config file! Regenerating..")
            System.exit(1)
        }
    }
}