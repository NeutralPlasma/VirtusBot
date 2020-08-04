package com.neutralplasma.virtusbot.event;

import com.neutralplasma.virtusbot.commands.ticket.CreateTicketCMD;
import com.neutralplasma.virtusbot.commands.ticket.DeleteTicketCMD;
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerLeveling;
import com.neutralplasma.virtusbot.settings.NewSettingsManager;
import com.neutralplasma.virtusbot.storage.locale.LocaleHandler;
import com.neutralplasma.virtusbot.storage.ticket.TicketInfo;
import com.neutralplasma.virtusbot.storage.ticket.TicketStorage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import java.awt.*;

public class EventHandler implements EventListener {
    private NewSettingsManager newSettingsManager;
    private CreateTicketCMD createTicketCMD;
    private LocaleHandler localeHandler;
    private PlayerLeveling playerLeveling;
    private boolean setuped = false;


    public EventHandler(NewSettingsManager newSettingsManager, CreateTicketCMD createTicketCMD,
                        LocaleHandler localeHandler, PlayerLeveling playerLeveling){
        this.newSettingsManager = newSettingsManager;
        this.createTicketCMD = createTicketCMD;
        this.localeHandler = localeHandler;
        this.playerLeveling = playerLeveling;
    }

    @Override
    public  void onEvent(GenericEvent gevent){
        // ADD REACTION TO MESSAGE EVENT
        if (gevent instanceof MessageReactionAddEvent) {
            MessageReactionAddEvent event = (MessageReactionAddEvent) gevent;
            if (event.getUser().isBot()) {
                return;
            }
            MessageReaction messageReaction = event.getReaction();
            MessageReaction.ReactionEmote emote = messageReaction.getReactionEmote();
            //TextUtil.sendMessage(emote.getName());
            if (emote.getName().equalsIgnoreCase("\uD83D\uDCAC")) {
                if (event.getChannel() == newSettingsManager.getTextChannel(event.getGuild(), "TICKET_CHANNEL")) {
                    messageReaction.removeReaction(event.getUser()).queue();
                    createTicketCMD.createTicket(event.getMember(), event.getGuild());
                }
            }
            // MESSAGE REACTION EVENT END

        }else if (gevent instanceof MessageReceivedEvent) {
            MessageReceivedEvent event = (MessageReceivedEvent) gevent;
            Member member = event.getMember();
            Message message = event.getMessage();

            if(event.isFromType(ChannelType.TEXT)){
                if(message.getContentRaw().contains("https://discord.gg/") && !member.hasPermission(Permission.MANAGE_SERVER)){
                    sendServerLog(event.getGuild(), message, member);
                    message.delete().queue();
                }

                if (event.getMessage().isWebhookMessage() || event.getMember().getUser().isBot()){
                    return;
                }
                playerLeveling.addXp(event.getMember().getUser(), event.getGuild(), event.getTextChannel());
            }
        }else if (gevent instanceof ReadyEvent){
            if(!setuped) {
                localeHandler.setup();
                setuped = true;
            }
        }
    }



    public void sendServerLog(Guild guild, Message message, Member member){
        TextChannel channel = newSettingsManager.getTextChannel(guild, "LogChannel");
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.magenta.brighter());
        eb.setTitle("Opozorilo!");
        eb.addField("Informacije", "**Uporabnik:** " +
               member.getEffectiveName() +
                "\n**Text: **" + message.getContentRaw() +
                "\n**Pogovor: **" + message.getChannel().getName() +
                "\n**ID sporočila: **" + message.getId(), false);
        if(channel != null){
            channel.sendMessage(eb.build()).queue();
        }
    }

}
