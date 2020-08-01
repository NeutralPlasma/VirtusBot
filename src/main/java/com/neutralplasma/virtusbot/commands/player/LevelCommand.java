package com.neutralplasma.virtusbot.commands.player;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.neutralplasma.virtusbot.audio.AudioManager;
import com.neutralplasma.virtusbot.commands.AudioCommand;
import com.neutralplasma.virtusbot.commands.PlayerCommand;
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerData;
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerLeveling;
import com.neutralplasma.virtusbot.handlers.playerSettings.PlayerSettings;
import com.neutralplasma.virtusbot.handlers.playerSettings.PlayerSettingsHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.util.List;

public class LevelCommand extends PlayerCommand {
    private PlayerLeveling playerLeveling;

    public LevelCommand(PlayerLeveling playerLeveling){
        this.name = "level";
        this.help = "Gets your or someones stats";
        this.cooldown = 10;
        this.arguments = "<USER/NONE>";
        this.playerLeveling = playerLeveling;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.getMessage().delete().queue();
        String[] args = commandEvent.getArgs().split(" ");
        if(args.length > 0){
            if(args[0].equalsIgnoreCase("")){
                PlayerData data = playerLeveling.getUser(commandEvent.getAuthor(), commandEvent.getGuild());
                if(data != null) {
                    playerLeveling.sendInfoImage(commandEvent.getAuthor(), data, commandEvent.getTextChannel());
                }else{
                    commandEvent.reply("NO DATA FOUND");
                }
            }else{

                List<Member> users = FinderUtil.findMembers(args[0], commandEvent.getGuild());
                if(users.size() > 1){
                    StringBuilder dbuilder = new StringBuilder();
                    for(Member user : users){
                        dbuilder.append(user.getUser().getName()).append(" ");
                    }
                    commandEvent.reply("Specify just 1 user. Specified: " + dbuilder);
                }else if(users.size() == 0){
                    commandEvent.reply("You need to specify a user.");
                }else{
                    PlayerData data = playerLeveling.getUser(users.get(0).getUser(), commandEvent.getGuild());
                    if(data != null) {
                        playerLeveling.sendInfoImage(users.get(0).getUser(), data, commandEvent.getTextChannel());
                    }else{
                        commandEvent.reply("NO DATA FOUND");
                    }
                }
            }
        }else{

        }
    }
}
