package com.neutralplasma.virtusbot;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.GuildSettingsManager;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.AboutCommand;
import com.neutralplasma.virtusbot.audio.AudioManager;
import com.neutralplasma.virtusbot.audio.search.YoutubeSearch;
import com.neutralplasma.virtusbot.commands.admin.*;
import com.neutralplasma.virtusbot.commands.audio.*;
import com.neutralplasma.virtusbot.commands.general.HelpCommand;
import com.neutralplasma.virtusbot.commands.general.SuggestCmd;
import com.neutralplasma.virtusbot.commands.general.TestCommand;
import com.neutralplasma.virtusbot.commands.player.LevelCommand;
import com.neutralplasma.virtusbot.commands.player.PlayerSettingsCommand;
import com.neutralplasma.virtusbot.commands.ticket.CreateTicketCMD;
import com.neutralplasma.virtusbot.commands.ticket.DeleteTicketCMD;
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerLeveling;
import com.neutralplasma.virtusbot.handlers.playerSettings.PlayerSettingsHandler;
import com.neutralplasma.virtusbot.storage.*;
import com.neutralplasma.virtusbot.event.EventHandler;
import com.neutralplasma.virtusbot.settings.NewSettingsManager;
import com.neutralplasma.virtusbot.utils.OtherUtil;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class VirtusBot {

    public final static Permission[] RECOMMENDED_PERMS = new Permission[]{Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION,
            Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_MANAGE, Permission.MESSAGE_EXT_EMOJI,
            Permission.MANAGE_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.NICKNAME_CHANGE};

    //public final static String prefix = "*";
    public final static String prefix = Info.PREFIX;
    private final static String levelingTable = "levelingdata";
    public static ArrayList<Command> commands = new ArrayList<>();

    public static void main(String[] args) {
        // GLOBAL VARIABLES -- YES --
        EventWaiter waiter = new EventWaiter();
        Bot bot = new Bot(waiter);
        String version = OtherUtil.getCurrentVersion();
        AudioManager audioManager = new AudioManager();
        Config config = new Config();
        SQL sql = new SQL();
        MySQL mySQL = new MySQL();
        sql.openConnection();

        TicketStorage ticketStorage = new TicketStorage(sql);
        NewSettingsManager newSettingsManager = new NewSettingsManager(sql);

        // DATASTORAGE SETUP
        PlayerSettingsHandler playerSettingsHandler = new PlayerSettingsHandler(sql);
        PlayerLeveling playerLeveling = new PlayerLeveling(sql, playerSettingsHandler);

        //mySQL.setup();
        System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0");

        ticketStorage.setup();


        // LOCALE HANDLER
        LocaleHandler localeHandler = new LocaleHandler(newSettingsManager, sql, bot);

        // MUSIC
        YoutubeSearch youtubeSearch = new YoutubeSearch();

        // PRECREATE COMMANDS
        CreateTicketCMD createTicketCMD = new CreateTicketCMD(ticketStorage, bot, newSettingsManager, localeHandler);
        DeleteTicketCMD deleteTicketCMD = new DeleteTicketCMD(ticketStorage, localeHandler);

        EventHandler eventHandler = new EventHandler(newSettingsManager, createTicketCMD, ticketStorage, deleteTicketCMD,
                localeHandler, playerLeveling);



        // ABOUT COMMAND -- DD --
        AboutCommand aboutCommand = new AboutCommand(Color.BLUE.brighter(),
                "Simple yet effective VirtusBOT (v"+version+")",
                new String[]{"Made with <3, made for use."},
                RECOMMENDED_PERMS);
        aboutCommand.setIsAuthor(false);
        aboutCommand.setReplacementCharacter("\uD83C\uDFB6");
        // SETTINGS COMING SQL BASED ONE
        GuildSettingsManager guildSettingsManager = new GuildSettingsManager() {
            @Nullable
            @Override
            public Object getSettings(Guild guild) {
                return newSettingsManager.getSettings(guild);
            }
        };
        // CLIENT BUILDER WITH COMMANDS

        // COMMANDS
        commands.add(aboutCommand);
        commands.add(new SuggestCmd(newSettingsManager, localeHandler));
        commands.add(new TestCommand(newSettingsManager, localeHandler));
        commands.add(new HelpCommand(newSettingsManager, localeHandler, bot));

        // ticket
        commands.add(createTicketCMD);
        commands.add(deleteTicketCMD);
        commands.add(new CreateTicketChannelCmd(newSettingsManager, localeHandler));

        // admin
        commands.add(new ServerDataCmd(mySQL, newSettingsManager, localeHandler, playerLeveling, sql, playerSettingsHandler));
        commands.add(new SetSuggestCmd(newSettingsManager, localeHandler));
        commands.add(new VoteCommand(localeHandler));
        commands.add(new SayCommand());

        // music
        commands.add(new PlayCommand(audioManager, youtubeSearch));
        commands.add(new EqualizerCommand(audioManager));
        commands.add(new MoveToCommand(audioManager));
        commands.add(new SkipCommand(audioManager));
        commands.add(new StopCommand(audioManager));
        commands.add(new QueueCommand(audioManager, bot));
        commands.add(new CurrentPlayingCommand(audioManager));
        commands.add(new VolumeCommand(audioManager));
        commands.add(new ShuffleCommand(audioManager));
        commands.add(new RepeatCommand(audioManager));

        // player
        commands.add(new LevelCommand(playerLeveling));
        commands.add(new PlayerSettingsCommand(playerSettingsHandler));

        CommandClientBuilder cb = new CommandClientBuilder()
                .setPrefix(prefix)
                .setOwnerId(Info.AUTHOR_ID)
                .setEmojis("", "", "")
                .setHelpWord("help")
                .setLinkedCacheSize(200)
                .setGuildSettingsManager(guildSettingsManager);
        cb.useHelpBuilder(false);
        commands.forEach(cb::addCommand);


        //cb.useHelpBuilder(false);
        cb.setStatus(OnlineStatus.ONLINE);
        CommandClient client = cb.build();


        // BUILD THE BOT
        try{
            JDA jda = JDABuilder.create(Info.TOKEN, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                    .setStatus(OnlineStatus.ONLINE)
                    .setToken(Info.TOKEN)
                    .addEventListeners(waiter, client, eventHandler)
                    .setBulkDeleteSplittingEnabled(true)
                    .build();
            bot.setJDA(jda);
        }catch (LoginException | IllegalArgumentException error) {
            error.printStackTrace();
        }

    }


    public static ArrayList<Command> getCommands(){
        return commands;
    }


    public static String getPrefix(){
        return prefix;
    }
}
