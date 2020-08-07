package com.neutralplasma.virtusbot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.neutralplasma.virtusbot.commands.AdminCommand;
import com.neutralplasma.virtusbot.utils.AbstractChatUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class SayCommand extends AdminCommand {

    private HashMap<String, EmbedBuilder> savedMessages = new HashMap<>();

    public SayCommand(){
        this.name = "say";
        this.help = "Bot says stuff instead of you in embed.";
        this.arguments = "<SUBCOMMAND>";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        String args = commandEvent.getArgs();
        String[] cargs = args.split(" ");
        Member member = commandEvent.getMember();
        if (cargs.length > 0){
            EmbedBuilder message = savedMessages.get(member.getId());
            if(message == null){
                message = new EmbedBuilder();
            }
            EmbedBuilder finalMessage = message;

            if(cargs[0].equalsIgnoreCase("settitle")){
                commandEvent.reply("Please send the title: ");

                AbstractChatUtil abstractChatUtil = new AbstractChatUtil(commandEvent.getAuthor(), chatInfo -> {
                    finalMessage.setTitle(chatInfo.getMessage());
                    savedMessages.put(member.getId(), finalMessage);
                }, commandEvent.getJDA());
                abstractChatUtil.setOnClose(() -> {
                    commandEvent.reply("DONE");
                });

            }else if (cargs[0].equalsIgnoreCase("addfield")) {
                commandEvent.reply("Please send the field title: ");
                AtomicReference<String> title = new AtomicReference<>("");
                AbstractChatUtil abstractChatUtil = new AbstractChatUtil(commandEvent.getAuthor(), chatInfo -> {
                    title.set(chatInfo.getMessage());

                }, commandEvent.getJDA());
                abstractChatUtil.setOnClose(() -> {
                    commandEvent.reply("Please provide the field content.");
                    AbstractChatUtil abstractChatUtil2 = new AbstractChatUtil(commandEvent.getAuthor(), chatInfo2 -> {
                        finalMessage.addField(title.get(), chatInfo2.getMessage().replace("\\n", System.lineSeparator()), false);
                        savedMessages.put(member.getId(), finalMessage);
                    }, commandEvent.getJDA());

                    abstractChatUtil2.setOnClose(() -> {
                        commandEvent.reply("DONE");
                    });
                });
            }else if (cargs[0].equalsIgnoreCase("color")){
                commandEvent.reply("Please send color in next format: RRR:GGG:BBB (255:255:255):");
                AbstractChatUtil abstractChatUtil = new AbstractChatUtil(commandEvent.getAuthor(), chatInfo -> {
                    String[] color = chatInfo.getMessage().split(":");
                    if(color.length > 2){
                        Color color1 = new Color(Integer.parseInt(color[0]),Integer.parseInt(color[1]),Integer.parseInt(color[2]));
                        finalMessage.setColor(color1);
                        savedMessages.put(member.getId(), finalMessage);

                    }else{
                        commandEvent.reply("Please follow the format: RRR:GGG:BBB");
                    }
                }, commandEvent.getJDA());
                abstractChatUtil.setOnClose(() -> {
                    commandEvent.reply("Finished.");
                });

            }else if (cargs[0].equalsIgnoreCase("clear")){
                savedMessages.remove(member.getId());
                commandEvent.reply("DONE");

            }else if (cargs[0].equalsIgnoreCase("send")){
                commandEvent.reply(finalMessage.build());

            }else{
                commandEvent.reply(getInfo().build());
            }
        }else{
            commandEvent.reply(getInfo().build());
        }
        //commandEvent.getMessage().delete().queueAfter(1L, TimeUnit.SECONDS);
    }

    public EmbedBuilder getInfo(){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Info:");
        eb.setColor(Color.magenta.brighter());
        eb.addField("Available subcommands",
                "**send** - Sends message\n" +
                "**color** - Set the color of the message border\n" +
                "**addfield** - Add the field to message\n" +
                "**removefield <id>** - Remove field from message\n" +
                "**settitle** - Set the title of message\n" +
                "**clear** - Clears the current saved message\n " +
                "**getfields** - Lists all the fields with their titles\n" +
                "**gettitle** - Get the current title\n " +
                "**preview** - Sends the preview message", false);
        return eb;
    }
}
