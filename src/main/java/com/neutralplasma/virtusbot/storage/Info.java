package com.neutralplasma.virtusbot.storage;

public class Info {
    private static final Config CONFIG = new Config();
    public static final String AUTHOR_ID = CONFIG.getValue("authorid");
    public static final String PREFIX = CONFIG.getValue("prefix");
    public static final String TOKEN = CONFIG.getValue("token");
    public static final String YOUTUBE_KEY = CONFIG.getValue("youtube_key");
}