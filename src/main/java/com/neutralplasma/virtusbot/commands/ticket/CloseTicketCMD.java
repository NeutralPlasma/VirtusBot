package com.neutralplasma.virtusbot.commands.ticket;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.neutralplasma.virtusbot.commands.TicketCommand;
import com.neutralplasma.virtusbot.storage.LocaleHandler;
import com.neutralplasma.virtusbot.storage.TicketInfo;
import com.neutralplasma.virtusbot.storage.TicketStorage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

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
        TicketInfo ticketid = ticketStorage.getTicket(event.getChannel().getId());
        if(ticketid != null){
            sendMessage(event.getTextChannel(), event.getGuild());
        }
    }

    public void sendMessage(TextChannel channel, Guild guild){
        EmbedBuilder eb = new EmbedBuilder();

        String content = localeHandler.getLocale(guild, "TICKET_CLOSE_MESSAGE");
        String field_title = localeHandler.getLocale(guild, "TICKET_CLOSE_FIELD_TITLE");
        String title = localeHandler.getLocale(guild, "TICKET_CLOSE_TITLE");

        eb.setTitle(title);
        eb.addField(field_title, content, false);
        channel.sendMessage(eb.build()).complete().addReaction("✔").queue();
    }

    public void deleteChannel(TextChannel channel){
        channel.delete().queue();
    }

}
