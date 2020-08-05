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
                playerSettings = new PlayerSettings(false, null, Color.orange, Color.red);
            }
            if(args[0].equalsIgnoreCase("toggleDark")){
                playerSettings.setDarkTheme(!playerSettings.isDarkTheme());
                if(playerSettings.isDarkTheme()){
                    commandEvent.reply("Enabled dark theme!");
                }else{
                    commandEvent.reply("Disabled dark theme!");
                }
                playerSettingsHandler.updateUser(commandEvent.getAuthor(), playerSettings);
            } else if(args[0].equalsIgnoreCase("avatarBackGround")){
                commandEvent.reply("URL: " + playerSettings.getAvatarBackgroundImage());
            } else if(args[0].equalsIgnoreCase("setColor1")){
                if(args.length > 1){
                    String[] color = args[1].split(":");
                    if(color.length > 2){
                        playerSettings.setColor1(new Color(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2])));
                        commandEvent.reply("Successfully set the color.");
                        playerSettingsHandler.updateUser(commandEvent.getAuthor(), playerSettings);
                        return;
                    }else{
                        commandEvent.reply("Follow this format: RRR:GGG:BBB");
                        return;
                    }
                }
                commandEvent.reply("Provide color...");
            } else if(args[0].equalsIgnoreCase("setColor2")) {
                if (args.length > 1) {
                    String[] color = args[1].split(":");
                    if (color.length > 2) {
                        playerSettings.setColor2(new Color(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2])));
                        commandEvent.reply("Successfully set the color.");
                        playerSettingsHandler.updateUser(commandEvent.getAuthor(), playerSettings);
                        return;
                    } else {
                        commandEvent.reply("Follow this format: RRR:GGG:BBB");
                        return;
                    }
                }
                commandEvent.reply("Provide color...");
            }else if(args[0].equalsIgnoreCase("resetColor1")){
                    playerSettings.setColor1(Color.orange);
                playerSettingsHandler.updateUser(commandEvent.getAuthor(), playerSettings);
                    commandEvent.reply("Reset the color.");
            }else if(args[0].equalsIgnoreCase("resetColor2")){
                playerSettings.setColor2(Color.red);
                playerSettingsHandler.updateUser(commandEvent.getAuthor(), playerSettings);
                commandEvent.reply("Reset the color.");
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
                    playerSettingsHandler.updateUser(commandEvent.getAuthor(), playerSettings);
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
                "**toggleDark** - Enables/Disables dark theme. \n" +
                "**avatarBackGround** - Returns current avatar background url. \n" +
                "**setColor1** <RRR:GGG:BBB> - Sets the first color. \n" +
                "**setColor2** <RRR:GGG:BBB> - Sets the second color. \n" +
                "**resetColor1**  - Resets the second color. \n" +
                "**resetColor2** - Resets the second color. \n" +
                "**setBackGround** <URL> - Sets new avatar background image.", false);
        eb.setColor(Color.MAGENTA);
        return eb;
    }
}
