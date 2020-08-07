package com.neutralplasma.virtusbot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.neutralplasma.virtusbot.commands.AdminCommand;
import com.neutralplasma.virtusbot.storage.locale.LocaleHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.function.Consumer;

public class VoteCommand extends AdminCommand {
    private LocaleHandler localeHandler;

    public VoteCommand(LocaleHandler localeHandler){
        this.name = "vote";
        this.help = "Dodas glasovanje";
        this.arguments = "<Text>";
        this.localeHandler = localeHandler;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        String arg = commandEvent.getArgs();
        String[] args = arg.split(" ");
        if (args.length >= 1){
            commandEvent.getMessage().delete().queue();
            Role role = commandEvent.getGuild().getRoleById("687345987408232476");
            createVote(commandEvent.getTextChannel(), arg, role, commandEvent.getGuild());
        }
    }

    public void createVote(TextChannel channel, String suggestion, Role role, Guild guild){
        EmbedBuilder eb = new EmbedBuilder();

        String field_title = localeHandler.getLocale(guild, "VOTE_FIELD_TITLE");
        String title = localeHandler.getLocale(guild, "VOTE_TITLE");



        eb.setTitle(title);
        eb.addField(field_title, "`" + suggestion + "`", false);
        eb.addField("Tag", role.getAsMention(), false);
        eb.setColor(Color.orange);
        Consumer<Message> callback = (response) ->{
            response.addReaction("\u2705").queue();
            response.addReaction("\u274C").queue();
        };

        channel.sendMessage(eb.build()).queue(callback);
    }
}
