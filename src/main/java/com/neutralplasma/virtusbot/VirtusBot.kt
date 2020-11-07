package com.neutralplasma.virtusbot

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandClientBuilder
import com.jagrosh.jdautilities.command.GuildSettingsManager
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import com.jagrosh.jdautilities.examples.command.AboutCommand
import com.neutralplasma.virtusbot.audio.AudioManager
import com.neutralplasma.virtusbot.audio.search.YoutubeSearch
import com.neutralplasma.virtusbot.commands.admin.*
import com.neutralplasma.virtusbot.commands.audio.*
import com.neutralplasma.virtusbot.commands.general.HelpCommand
import com.neutralplasma.virtusbot.commands.general.SuggestCmd
import com.neutralplasma.virtusbot.commands.general.TestCommand
import com.neutralplasma.virtusbot.commands.owner.BlackListCommand
import com.neutralplasma.virtusbot.commands.owner.BotPowerCommand
import com.neutralplasma.virtusbot.commands.player.LevelCommand
import com.neutralplasma.virtusbot.commands.player.PlayerSettingsCommand
import com.neutralplasma.virtusbot.commands.ticket.CloseTicketCMD
import com.neutralplasma.virtusbot.commands.ticket.CreateTicketCMD
import com.neutralplasma.virtusbot.event.EventHandler
import com.neutralplasma.virtusbot.handlers.BlackList
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerLeveling
import com.neutralplasma.virtusbot.handlers.playerSettings.PlayerSettingsHandler
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import com.neutralplasma.virtusbot.storage.config.Info
import com.neutralplasma.virtusbot.storage.dataStorage.StorageHandler
import com.neutralplasma.virtusbot.storage.ticket.TicketStorage
import com.neutralplasma.virtusbot.utils.OtherUtil
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.requests.GatewayIntent
import java.awt.Color
import java.util.*
import java.util.function.Consumer
import javax.security.auth.login.LoginException
import kotlin.collections.ArrayList

object VirtusBot {
    val RECOMMENDED_PERMS = arrayOf(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION,
            Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_MANAGE, Permission.MESSAGE_EXT_EMOJI,
            Permission.MANAGE_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.NICKNAME_CHANGE)
    @JvmStatic
    val prefix = Info.PREFIX
    @JvmStatic
    var commands = ArrayList<Command>()
    var commandCategories = ArrayList<String>()
    lateinit var storageHandler: StorageHandler
    @JvmStatic
    lateinit var blackList: BlackList

    @JvmStatic
    fun main(args: Array<String>) {
        // GLOBAL VARIABLES -- YES --
        val waiter = EventWaiter()
        val bot = Bot(waiter)
        val version = OtherUtil.currentVersion
        val audioManager = AudioManager()
        storageHandler = StorageHandler()
        blackList = BlackList(storageHandler)
        val ticketStorage = TicketStorage(storageHandler)
        val newSettingsManager = NewSettingsManager(storageHandler)

        // DATASTORAGE - TODO: SETUP NEEDS TO BE REDONE!!!!!!!!!!!
        val playerSettingsHandler = PlayerSettingsHandler(storageHandler)
        val playerLeveling = PlayerLeveling(storageHandler, playerSettingsHandler, newSettingsManager)

        //mySQL.setup();
        System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0")
        ticketStorage.setup()



        // MUSIC
        val youtubeSearch = YoutubeSearch()

        // PRECREATE COMMANDS
        val createTicketCMD = CreateTicketCMD(ticketStorage, bot, newSettingsManager)
        val eventHandler = EventHandler(newSettingsManager, createTicketCMD,
                playerLeveling)


        // ABOUT COMMAND -- DD --
        val aboutCommand = AboutCommand(Color.BLUE.brighter(),
                "Simple yet effective VirtusBOT (v$version)", arrayOf("Made with <3, made for use."),
                *RECOMMENDED_PERMS)
        aboutCommand.setIsAuthor(false)
        aboutCommand.setReplacementCharacter("\uD83C\uDFB6")
        // SETTINGS COMING SQL BASED ONE
        val guildSettingsManager: GuildSettingsManager<*> = GuildSettingsManager<Any?> { guild -> newSettingsManager.getSettings(guild) }
        // CLIENT BUILDER WITH COMMANDS

        // COMMANDS
        commands.add(aboutCommand)
        commands.add(SuggestCmd(newSettingsManager ))
        commands.add(TestCommand(newSettingsManager ))
        commands.add(HelpCommand(newSettingsManager, bot))

        // ticket
        commands.add(createTicketCMD)
        commands.add(CreateTicketChannelCmd(newSettingsManager))
        commands.add(CloseTicketCMD(ticketStorage))

        // admin
        commands.add(ServerDataCmd(newSettingsManager, playerLeveling, storageHandler, playerSettingsHandler))
        commands.add(SetSuggestCmd(newSettingsManager))
        commands.add(SayCommand())
        commands.add(MultiplierCommand(playerLeveling))
        commands.add(NewAdminCommand())

        // music
        commands.add(PlayCommand(audioManager, youtubeSearch))
        commands.add(EqualizerCommand(audioManager))
        commands.add(MoveToCommand(audioManager))
        commands.add(SkipCommand(audioManager))
        commands.add(StopCommand(audioManager))
        commands.add(QueueCommand(audioManager, bot))
        commands.add(CurrentPlayingCommand(audioManager))
        commands.add(VolumeCommand(audioManager))
        commands.add(ShuffleCommand(audioManager))
        commands.add(RepeatCommand(audioManager))

        // owner
        commands.add(BlackListCommand())
        commands.add(BotPowerCommand(playerLeveling, playerSettingsHandler))

        // player
        commands.add(LevelCommand(playerLeveling))
        commands.add(PlayerSettingsCommand(playerSettingsHandler))
        val cb = CommandClientBuilder()
                .setPrefix(prefix)
                .setOwnerId(Info.AUTHOR_ID)
                .setEmojis("\u2705", "\u26A0", "\u274C")
                .setHelpWord("help")
                .setLinkedCacheSize(200)
                .setGuildSettingsManager(guildSettingsManager)
        cb.useHelpBuilder(false)
        commands.forEach(Consumer { command: Command? -> cb.addCommand(command) })

        commands.forEach(Consumer { command: Command? -> if(!commandCategories.contains(command?.category?.name)) command?.category?.name?.let { commandCategories.add(it) } })
        commandCategories.add("general")


        //cb.useHelpBuilder(false);
        cb.setStatus(OnlineStatus.ONLINE)
        val client = cb.build()

        // BUILD THE BOT
        try {
            val jda = JDABuilder.create(Info.TOKEN, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                    .setStatus(OnlineStatus.ONLINE)
                    .setToken(Info.TOKEN)
                    .addEventListeners(waiter, client, eventHandler)
                    .setBulkDeleteSplittingEnabled(true)
                    .build()
            bot.jda = jda
        } catch (error: LoginException) {
            error.printStackTrace()
        } catch (error: IllegalArgumentException) {
            error.printStackTrace()
        }
    }

}