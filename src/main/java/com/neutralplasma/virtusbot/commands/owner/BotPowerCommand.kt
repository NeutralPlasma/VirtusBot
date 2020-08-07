package com.neutralplasma.virtusbot.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.neutralplasma.virtusbot.VirtusBot;
import com.neutralplasma.virtusbot.commands.OwnerCommand;
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerLeveling;
import com.neutralplasma.virtusbot.handlers.playerSettings.PlayerSettings;
import com.neutralplasma.virtusbot.handlers.playerSettings.PlayerSettingsHandler;
import com.neutralplasma.virtusbot.settings.NewSettingsManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.sql.SQLException;
import java.util.List;

public class BotPowerCommand extends OwnerCommand {

    PlayerLeveling playerLeveling;
    PlayerSettingsHandler playerSettingsHandler;


    public BotPowerCommand(PlayerLeveling playerLeveling, PlayerSettingsHandler playerSettingsHandler){
        this.name = "botpower";
        this.help = "Owner bot managing command.";
        this.arguments = "<SUBCOMMAND>";
        this.playerLeveling = playerLeveling;
        this.playerSettingsHandler = playerSettingsHandler;
    }


    @Override
    protected void execute(CommandEvent commandEvent) {
        Guild guild = commandEvent.getGuild();
        String[] args = commandEvent.getArgs().split(" ");

        if(args.length > 0){
            if(args[0].equalsIgnoreCase("save")){
                try {
                    playerSettingsHandler.syncSettings();
                }catch (SQLException error){
                    error.printStackTrace();
                    commandEvent.reply("Failed saving player settings. (check console)");
                }
                try {
                    playerLeveling.syncUsers();
                }catch (SQLException error){
                    error.printStackTrace();
                    commandEvent.reply("Failed saving player leveling (check console)");
                }
                commandEvent.reply("Done");
            }else if(args[0].equalsIgnoreCase("stop")){
                try {
                    playerLeveling.syncUsers();
                    playerSettingsHandler.syncSettings();
                }catch (SQLException error){
                    error.printStackTrace();
                    commandEvent.reply("Failed saving data! (check console)");
                }
                commandEvent.reply("Stopping...");
                System.exit(1);
            }
        }
    }

}
