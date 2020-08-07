package com.neutralplasma.virtusbot.storage.config;

public class Info {
    private static final Config CONFIG = new Config();
    public static final String AUTHOR_ID = CONFIG.getValue("authorid");
    public static final String PREFIX = CONFIG.getValue("prefix");
    public static final String TOKEN = CONFIG.getValue("token");
    public static final String YOUTUBE_KEY = CONFIG.getValue("youtube_key");

    public static final boolean USE_MYSQL = CONFIG.getBoolean("use_mysql");
    public static final boolean USE_SSL = CONFIG.getBoolean("use_SSL");

    public static final String DATABASE_IP = CONFIG.getValue("database_ip");
    public static final String DATABASE_PORT = CONFIG.getValue("database_port");
    public static final String DATABASE_NAME = CONFIG.getValue("database_name");
    public static final String DATABASE_USER = CONFIG.getValue("database_user");
    public static final String DATABASE_PASSWORD = CONFIG.getValue("database_password");
}