package com.neutralplasma.virtusbot.audio.search

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.neutralplasma.virtusbot.storage.config.Info
import java.net.MalformedURLException
import java.net.URL

class YoutubeSearch {
    private val youTube: YouTube?
    fun isUrl(input: String?): Boolean {
        return try {
            URL(input)
            true
        } catch (ignored: MalformedURLException) {
            false
        }
    }

    fun searchYoutube(input: String?): String? {
        try {
            val results = youTube!!.search()
                    .list("id,snippet")
                    .setQ(input)
                    .setMaxResults(1L)
                    .setType("video")
                    .setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)")
                    .setKey(Info.YOUTUBE_KEY)
                    .execute()
                    .items
            if (!results.isEmpty()) {
                val videoId = results[0].id.videoId
                return "https://www.youtube.com/watch?v=$videoId"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    init {
        var temp: YouTube? = null
        try {
            temp = YouTube.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    null
            )
                    .setApplicationName("Menudocs JDA tutorial bot")
                    .build()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        youTube = temp
    }
}