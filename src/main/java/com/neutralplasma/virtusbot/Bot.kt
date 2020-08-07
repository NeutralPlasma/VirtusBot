package com.neutralplasma.virtusbot;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Bot {
    private final EventWaiter waiter;
    private final ScheduledExecutorService threadpool;

    private boolean shuttingDown = false;
    private JDA jda;

    public Bot(EventWaiter waiter)
    {
        this.waiter = waiter;
        this.threadpool = Executors.newSingleThreadScheduledExecutor();
    }
    public ScheduledExecutorService getThreadpool()
    {
        return threadpool;
    }

    public JDA getJDA()
    {
        return jda;
    }

    public void closeAudioConnection(long guildId)
    {
        Guild guild = jda.getGuildById(guildId);
        if(guild!=null)
            threadpool.submit(() -> guild.getAudioManager().closeAudioConnection());
    }

    public void shutdown()
    {
        if(shuttingDown)
            return;
        shuttingDown = true;
        threadpool.shutdownNow();
        if(jda.getStatus()!=JDA.Status.SHUTTING_DOWN)
        {
            jda.getGuilds().forEach(g ->
            {
                g.getAudioManager().closeAudioConnection();
            });
            jda.shutdown();
        }
        System.exit(0);
    }

    public EventWaiter getWaiter()
    {
        return waiter;
    }

    public void setJDA(JDA jda)
    {
        this.jda = jda;
    }

}
