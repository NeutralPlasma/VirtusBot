package com.neutralplasma.virtusbot.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.neutralplasma.virtusbot.VirtusBot;
import com.neutralplasma.virtusbot.commands.AdminCommand;
import com.neutralplasma.virtusbot.commands.OwnerCommand;
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerData;
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerLeveling;
import com.neutralplasma.virtusbot.handlers.playerSettings.PlayerSettingsHandler;
import com.neutralplasma.virtusbot.settings.NewSettingsManager;
import com.neutralplasma.virtusbot.storage.LocaleHandler;
import com.neutralplasma.virtusbot.storage.MySQL;
import com.neutralplasma.virtusbot.storage.PlayerStorage;
import com.neutralplasma.virtusbot.storage.SQL;
import com.neutralplasma.virtusbot.utils.FormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class BlackListCommand extends OwnerCommand {


    public BlackListCommand(){
        this.name = "blacklist";
        this.help = "Owner blacklist.";
        this.arguments = "<SUBCOMMAND>";
    }


    @Override
    protected void execute(CommandEvent commandEvent) {
        Guild guild = commandEvent.getGuild();
        String[] args = commandEvent.getArgs().split(" ");


        if(args.length > 0){
            if(args[0].equalsIgnoreCase("add")){

                List<Member> members = FinderUtil.findMembers(args[1], guild);
                if (members.isEmpty()) {
                    commandEvent.reply("No user found!");
                } else if (members.size() > 1) {
                    commandEvent.reply(commandEvent.getClient().getWarning() + "You need to specify just 1 user!");
                } else {
                    VirtusBot.getBlackList().addToBlackList(members.get(0).getId());
                    commandEvent.reply("Added to blacklist: " + members.get(0).getId());
                }

            }
            if(args[0].equalsIgnoreCase("remove")){
                List<Member> members = FinderUtil.findMembers(args[1], guild);
                if (members.isEmpty()) {
                    commandEvent.reply("No user found!");
                } else if (members.size() > 1) {
                    commandEvent.reply(commandEvent.getClient().getWarning() + "You need to specify just 1 user!");
                } else {
                    VirtusBot.getBlackList().removeFromBlackList(members.get(0).getId());
                    commandEvent.reply("Removed from blacklist: " + members.get(0).getId());
                }

            }

            if(args[0].equalsIgnoreCase("check")){
                List<Member> members = FinderUtil.findMembers(args[1], guild);
                if (members.isEmpty()) {
                    commandEvent.reply("No user found!");
                } else if (members.size() > 1) {
                    commandEvent.reply(commandEvent.getClient().getWarning() + "You need to specify just 1 user!");
                } else {
                    if(VirtusBot.getBlackList().isBlackListed(members.get(0).getId())){
                        commandEvent.reply("User is blacklisted!");
                    }else{
                        commandEvent.reply("User isn't blacklisted!");
                    }
                }

            }

        }
    }

}
