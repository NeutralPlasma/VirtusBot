package com.neutralplasma.virtusbot

import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.function.Consumer

class Bot(val waiter: EventWaiter) {
    val threadpool: ScheduledExecutorService
    private var shuttingDown = false
    lateinit var jda: JDA

    fun closeAudioConnection(guildId: Long) {
        val guild = jda.getGuildById(guildId)
        if (guild != null) threadpool.submit { guild.audioManager.closeAudioConnection() }
    }

    fun shutdown() {
        if (shuttingDown) return
        shuttingDown = true
        threadpool.shutdownNow()
        if (jda.status != JDA.Status.SHUTTING_DOWN) {
            jda.guilds.forEach(Consumer { g: Guild -> g.audioManager.closeAudioConnection() })
            jda.shutdown()
        }
        System.exit(0)
    }

    init {
        threadpool = Executors.newSingleThreadScheduledExecutor()
    }
}