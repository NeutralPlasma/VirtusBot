package com.neutralplasma.virtusbot.commands.ticket;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.neutralplasma.virtusbot.commands.TicketCommand;
import com.neutralplasma.virtusbot.storage.locale.LocaleHandler;
import com.neutralplasma.virtusbot.storage.ticket.TicketInfo;
import com.neutralplasma.virtusbot.storage.ticket.TicketStorage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class DeleteTicketCMD extends TicketCommand {

    private TicketStorage ticketStorage;
    private LocaleHandler localeHandler;

    public DeleteTicketCMD(TicketStorage ticketStorage, LocaleHandler localeHandler){
        this.name = "delete";
        this.help = "Delete the ticket";
        this.aliases = new String[]{"deleteticket"};
        this.arguments = "<Name|NONE>";
        this.guildOnly = true;
        this.ticketStorage = ticketStorage;
        this.localeHandler = localeHandler;
    }

    @Override
    protected void execute(CommandEvent event) {
        TicketInfo ticketid = ticketStorage.getTicketChannel(event.getChannel().getId());
        if(ticketid != null){
            sendMessage(event.getTextChannel(), event.getGuild());
        }
    }

    public void sendMessage(TextChannel channel, Guild guild){
        EmbedBuilder eb = new EmbedBuilder();

        String content = localeHandler.getLocale(guild, "TICKET_DELETE_MESSAGE");
        String field_title = localeHandler.getLocale(guild, "TICKET_DELETE_FIELD_TITLE");
        String title = localeHandler.getLocale(guild, "TICKET_DELETE_TITLE");

        eb.setTitle(title);
        eb.addField(field_title, content, false);
        channel.sendMessage(eb.build()).complete().addReaction("✔").queue();
    }

    public void deleteChannel(TextChannel channel){
        channel.delete().queue();
    }

}
