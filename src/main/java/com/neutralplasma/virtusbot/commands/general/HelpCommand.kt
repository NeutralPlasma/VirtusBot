package com.neutralplasma.virtusbot.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.Paginator;
import com.neutralplasma.virtusbot.Bot;
import com.neutralplasma.virtusbot.VirtusBot;
import com.neutralplasma.virtusbot.settings.NewSettingsManager;
import com.neutralplasma.virtusbot.storage.locale.LocaleHandler;
import com.neutralplasma.virtusbot.utils.TextUtil;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class HelpCommand extends Command {

    private NewSettingsManager newSettingsManager;
    private LocaleHandler localeHandler;
    private final Paginator.Builder builder;

    public HelpCommand(NewSettingsManager newSettingsManager, LocaleHandler localeHandler, Bot bot){
        this.name = "help";
        this.help = "Main help command.";
        this.guildOnly = true;
        this.newSettingsManager = newSettingsManager;
        this.localeHandler = localeHandler;

        builder = new Paginator.Builder()
                .setColumns(1)
                .setFinalAction(m -> {try{m.clearReactions().queue();}catch(PermissionException ignore){}})
                .setItemsPerPage(10)
                .waitOnSinglePage(false)
                .useNumberedItems(true)
                .showPageNumbers(true)
                .wrapPageEnds(true)
                .setEventWaiter(bot.getWaiter())
                .setTimeout(1, TimeUnit.MINUTES);
    }

    @Override
    protected void execute(CommandEvent event) {
        ArrayList<Command> commands = VirtusBot.getCommands();
        String[] args = event.getArgs().split(" ");
        ArrayList<String> content = new ArrayList<>();


        if(!args[0].equalsIgnoreCase("")){
            for(Command command : commands){
                if(command.getCategory() != null && command.getCategory().getName().equalsIgnoreCase(args[0])){
                    content.add("`" + command.getName() +" " + (command.getArguments() == null ? "" : command.getArguments()) +  "`  - " + command.getHelp());
                }
            }
        }else{
            for(Command command : commands){
                content.add("`" + command.getName() +" " + (command.getArguments() == null ? "" : command.getArguments()) +  "`  - " + command.getHelp());
            }
        }

        String[] list = new String[content.size()];

        for(int i=0; i<content.size(); i++){
            list[i] = content.get(i);
        }


        builder.setText((i1, i2) -> getQueueTitle(event.getClient().getSuccess(), args[0]))
                .setItems(list)
                .setUsers(event.getAuthor())
                .setColor(Color.magenta.brighter())
        ;
        builder.build().paginate(event.getChannel(), 1);

    }

    private String getQueueTitle(String success, String category){
        return TextUtil.filter(success + " | " + category);
    }
}
