package com.neutralplasma.virtusbot.storage.config

object Info {
    private val CONFIG = Config()
    val AUTHOR_ID = CONFIG.getValue("authorid")
    @JvmField
    val PREFIX = CONFIG.getValue("prefix")
    val TOKEN = CONFIG.getValue("token")
    @JvmField
    val YOUTUBE_KEY = CONFIG.getValue("youtube_key")
    val USE_MYSQL = CONFIG.getBoolean("use_mysql")!!
    val USE_SSL = CONFIG.getBoolean("use_SSL")!!
    val DATABASE_IP = CONFIG.getValue("database_ip")
    val DATABASE_PORT = CONFIG.getValue("database_port")
    val DATABASE_NAME = CONFIG.getValue("database_name")
    val DATABASE_USER = CONFIG.getValue("database_user")
    val DATABASE_PASSWORD = CONFIG.getValue("database_password")
}