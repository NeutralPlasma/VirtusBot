package com.neutralplasma.virtusbot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.neutralplasma.virtusbot.commands.AdminCommand;
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerData;
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerLeveling;
import com.neutralplasma.virtusbot.handlers.playerSettings.PlayerSettingsHandler;
import com.neutralplasma.virtusbot.settings.NewSettingsManager;
import com.neutralplasma.virtusbot.storage.locale.LocaleHandler;
import com.neutralplasma.virtusbot.storage.dataStorage.StorageHandler;
import com.neutralplasma.virtusbot.utils.FormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.List;

public class ServerDataCmd extends AdminCommand {

    private StorageHandler sql;
    private NewSettingsManager settingsManager;
    private LocaleHandler localeHandler;
    private PlayerLeveling playerLeveling;
    private PlayerSettingsHandler playerSettingsHandler;

    public ServerDataCmd(NewSettingsManager settingsManager, LocaleHandler localeHandler,
                         PlayerLeveling playerLeveling, StorageHandler sql, PlayerSettingsHandler playerSettingsHandler)
    {
        this.name = "serverdata";
        this.help = "Simple command for checking server stuff";
        this.arguments = "<SUBCOMMAND>";
        this.settingsManager = settingsManager;
        this.localeHandler = localeHandler;
        this.playerLeveling = playerLeveling;
        this.sql = sql;
        this.playerSettingsHandler = playerSettingsHandler;
    }


    @Override
    protected void execute(CommandEvent commandEvent) {
        String arg = commandEvent.getArgs();
        Guild guild = commandEvent.getGuild();
        String[] args = arg.split(" ");
        commandEvent.reply("Args length " + args.length + " Args: `" + arg + "` " );


        if(args.length >= 1){

            if(args[0].equalsIgnoreCase("setrole")) {
                if (args.length > 2) {
                    String setting = args[1];
                    List<Role> roles = FinderUtil.findRoles(args[2], guild);
                    if (roles.isEmpty()) {
                        commandEvent.reply("No role found!");
                    } else if (roles.size() > 1) {
                        commandEvent.reply(commandEvent.getClient().getWarning() + FormatUtil.listOfRoles(roles, arg));
                    } else {
                        commandEvent.reply("Set: " + roles.get(0).getAsMention());
                        settingsManager.addStringData(guild, setting, roles.get(0).getId());
                    }
                }else{
                    commandEvent.reply("**setrole:** <setting> <data>");
                }
            }else if(args[0].equalsIgnoreCase("setchannel")) {
                if (args.length > 2) {
                    String setting = args[1];
                    List<TextChannel> channels = FinderUtil.findTextChannels(args[2], guild);
                    if (channels.isEmpty()) {
                        commandEvent.reply("No channel found!");
                    } else if (channels.size() > 1) {
                        commandEvent.reply(commandEvent.getClient().getWarning() + FormatUtil.listOfTChannels(channels, arg));
                    } else {
                        commandEvent.reply("Set: " + channels.get(0).getAsMention());
                        settingsManager.addStringData(guild, setting, channels.get(0).getId());
                    }
                }else{
                    commandEvent.reply("**set:** <setting> <data>");
                }
            }else if(args[0].equalsIgnoreCase("getdata")){
                String setting = args[1];
                commandEvent.reply("Data: `" + settingsManager.getData(guild, setting) + "`");

            }else if(args[0].equalsIgnoreCase("setstringdata")){
                if (args.length > 2) {
                    String setting = args[1];
                    String data = args[2];
                    settingsManager.addStringData(guild, setting, data);
                }else{
                    commandEvent.reply("**setrole:** <setting> <data>");
                }
            }else if(args[0].equalsIgnoreCase("setlocale")){
                if (args.length > 2) {
                    String setting = args[1];
                    StringBuilder builder = new StringBuilder();
                    for (int i = 2; i < args.length; i++){
                        builder.append(" ").append(args[i]);
                    }
                    String data = args[2];
                    localeHandler.updateLocale(guild, setting, builder.toString());
                    commandEvent.reply("Succesfully updated: " + setting + " data to: " + builder.toString());
                }else{
                    commandEvent.reply("**setrole:** <setting> <data>");
                }
            }else if(args[0].equalsIgnoreCase("getlocales")){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(Color.magenta.brighter());
                eb.setTitle("All locales");
                StringBuilder builder = new StringBuilder();
                for (String string : localeHandler.getDefaultLocales().keySet()){
                    builder.append(string).append("\n");
                }
                eb.addField("Locales", builder.toString(), false);

                commandEvent.reply(eb.build());
            }else if(args[0].equalsIgnoreCase("test")){
                try {
                    PlayerData data = playerLeveling.getUser(commandEvent.getAuthor(), commandEvent.getGuild());
                    playerLeveling.sendLevelUpMessage(commandEvent.getAuthor(), data, commandEvent.getTextChannel());

                }catch (Exception error){
                    error.printStackTrace();
                }
            }

        }else{
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("SubCommands");
            eb.addField("List:", "**data:** <steamid> - gets player data \n" +
                    "**money:** <steamid> - gets player money \n" +
                    "**bank:** <steamid> - gets player bank \n" +
                    "**setrole:** <setting> <data> - sets server setting \n" +
                    "**setchannel:** <setting> <data> - sets server setting \n" +
                    "**adminget:** <setting> - returns setting from SQL \n " +
                    "**adminadd** <setting> <data> - adds data to SQL \n " +
                    "**setlocale** <locale> <locale_text> - adds localedata to locales \n " +
                    "**getlocales** - gets all possible locales to be set \n " +
                    "", false);
            eb.setColor(Color.ORANGE);
            commandEvent.reply(eb.build());
        }
    }


}
