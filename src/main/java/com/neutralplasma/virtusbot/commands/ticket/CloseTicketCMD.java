package com.neutralplasma.virtusbot.commands.ticket;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.neutralplasma.virtusbot.commands.TicketCommand;
import com.neutralplasma.virtusbot.storage.locale.LocaleHandler;
import com.neutralplasma.virtusbot.storage.ticket.TicketInfo;
import com.neutralplasma.virtusbot.storage.ticket.TicketStorage;
import com.neutralplasma.virtusbot.utils.AbstractChatUtil;
import com.neutralplasma.virtusbot.utils.AbstractReactionUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class CloseTicketCMD extends TicketCommand {

    private TicketStorage ticketStorage;
    private LocaleHandler localeHandler;

    public CloseTicketCMD(TicketStorage ticketStorage, LocaleHandler localeHandler){
        this.name = "close";
        this.help = "Delete the ticket";
        this.aliases = new String[]{"closeticket"};
        this.arguments = "<Name|NONE>";
        this.guildOnly = true;
        this.ticketStorage = ticketStorage;
        this.localeHandler = localeHandler;
    }

    @Override
    protected void execute(CommandEvent event) {
        TicketInfo ticketid = ticketStorage.getTicketChannel(event.getChannel().getId());
        if(ticketid != null){
            event.reply("TEST");
            sendMessage(event.getTextChannel(), event.getGuild(), event.getAuthor(), ticketid);
        }else{
            event.reply("You don't have an ticket.");
        }
    }

    public void sendMessage(TextChannel channel, Guild guild, User user, TicketInfo info){
        EmbedBuilder eb = new EmbedBuilder();

        String content = localeHandler.getLocale(guild, "TICKET_CLOSE_MESSAGE");
        String field_title = localeHandler.getLocale(guild, "TICKET_CLOSE_FIELD_TITLE");
        String title = localeHandler.getLocale(guild, "TICKET_CLOSE_TITLE");

        eb.setTitle(title);
        eb.addField(field_title, content, false);
        Message message = channel.sendMessage(eb.build()).complete();
        message.addReaction("✔").queue();
        channel.sendMessage("Test").queue();

        AbstractReactionUtil reactionUtil = new AbstractReactionUtil(user, chatInfo -> {
        }, message.getJDA(), "✔", message.getId());

        reactionUtil.setOnClose(() -> {
            ticketStorage.deleteTicket(info.getUserid(), info.getChannelID());
            deleteChannel(channel);
        });


    }

    public void deleteChannel(TextChannel channel){
        channel.delete().queue();
    }

}
