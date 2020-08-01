package com.neutralplasma.virtusbot.commands.player;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.neutralplasma.virtusbot.commands.PlayerCommand;
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerData;
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerLeveling;
import com.neutralplasma.virtusbot.handlers.playerSettings.PlayerSettings;
import com.neutralplasma.virtusbot.handlers.playerSettings.PlayerSettingsHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.net.URL;
import java.util.List;

public class PlayerSettingsCommand extends PlayerCommand {
    private PlayerSettingsHandler playerSettingsHandler;

    public PlayerSettingsCommand(PlayerSettingsHandler playerSettingsHandler){
        this.name = "psettings";
        this.help = "Gets your or someones stats";
        this.arguments = "<USER/NONE>";
        this.playerSettingsHandler = playerSettingsHandler;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.getMessage().delete().queue();
        String[] args = commandEvent.getArgs().split(" ");
        if(args.length > 0){
            PlayerSettings playerSettings = playerSettingsHandler.getSettings(commandEvent.getAuthor());
            if(playerSettings  == null){
                playerSettings = new PlayerSettings(false, null);
            }
            if(args[0].equalsIgnoreCase("enableDark")){
                playerSettings.setDarkTheme(!playerSettings.isDarkTheme());
                if(playerSettings.isDarkTheme()){
                    commandEvent.reply("Enabled dark theme!");
                }else{
                    commandEvent.reply("Disabled dark theme!");
                }
                playerSettingsHandler.updateUser(commandEvent.getAuthor(), playerSettings);
            } else if(args[0].equalsIgnoreCase("avatarBackGround")){
                commandEvent.reply("URL: " + playerSettings.getAvatarBackgroundImage());
            } else if(args[0].equalsIgnoreCase("setBackGround")){
                if(args.length > 1){
                    try {
                        new URL(args[1]).toURI();
                        playerSettings.setAvatarBackgroundImage(args[1]);
                        playerSettingsHandler.updateUser(commandEvent.getAuthor(), playerSettings);
                    }catch (Exception exception){
                        commandEvent.reply("Invalid URL link!");
                    }
                }else{
                    playerSettings.setAvatarBackgroundImage(null);
                }
            }else{
                commandEvent.reply(getInfoMessage().build());
            }
        }else{
            commandEvent.reply(getInfoMessage().build());
        }
    }

    public EmbedBuilder getInfoMessage(){
        EmbedBuilder eb = new EmbedBuilder();
        eb.addField("Commands", "\n" +
                "**enableDark** - Enables/Disables dark theme. \n" +
                "**avatarBackGround** - Returns current avatar background url. \n" +
                "**setBackGround** <URL> - Sets new avatar background image.", false);
        eb.setColor(Color.MAGENTA);
        return eb;
    }
}
