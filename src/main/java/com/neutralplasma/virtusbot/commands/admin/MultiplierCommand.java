package com.neutralplasma.virtusbot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.neutralplasma.virtusbot.commands.AdminCommand;
import com.neutralplasma.virtusbot.handlers.playerLeveling.MultiplierData;
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerLeveling;
import com.neutralplasma.virtusbot.utils.MathUtil;
import com.neutralplasma.virtusbot.utils.TextUtil;

public class MultiplierCommand extends AdminCommand {
    private PlayerLeveling playerLeveling;

    public MultiplierCommand(PlayerLeveling playerLeveling) {
        this.name = "multiplieradmin";
        this.help = "XP multiplier stuff.";
        this.arguments = "<SUBCOMMAND>";
        this.playerLeveling = playerLeveling;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" ");
        if(args.length > 0){
            if(args[0].equalsIgnoreCase("set")){
                if(args.length > 1){
                    MultiplierData data = playerLeveling.getMultiplierData(event.getGuild());
                    if(data == null){
                        data = new MultiplierData();
                    }
                    int multiplier = Integer.parseInt(args[1]);
                    long time;
                    if(args.length > 2){
                        time = MathUtil.formatTimeLong(args) * 1000;
                        data.setMultiplier(multiplier, time);
                        playerLeveling.setMultiplier(event.getGuild(), data);
                        event.reply("Successfully set multiplier: " + data.getMultiplier() + " . For: " + TextUtil.formatTiming(time));
                    }
                }
            } else if(args[0].equalsIgnoreCase("check")){
                MultiplierData data = playerLeveling.getMultiplierData(event.getGuild());
                if(data == null){
                    data = new MultiplierData();
                }
                event.reply("Current multiplier: " + data.getMultiplier());

            }
        }
    }
}
