package com.neutralplasma.virtusbot.commands.ticket;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.neutralplasma.virtusbot.Bot;
import com.neutralplasma.virtusbot.commands.TicketCommand;
import com.neutralplasma.virtusbot.settings.NewSettingsManager;
import com.neutralplasma.virtusbot.storage.locale.LocaleHandler;
import com.neutralplasma.virtusbot.storage.ticket.TicketStorage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

import java.awt.*;
import java.text.MessageFormat;
import java.util.List;

public class CreateTicketCMD extends TicketCommand {

    private TicketStorage ticketStorage;
    private Bot bot;
    private NewSettingsManager newSettingsManager;
    private LocaleHandler localeHandler;

    public CreateTicketCMD(TicketStorage ticketStorage, Bot bot, NewSettingsManager newSettingsManager, LocaleHandler localeHandler)
    {
        this.name = "create";
        this.help = "Create new ticket";
        this.aliases = new String[]{"new", "support"};
        this.arguments = "<Name|NONE>";
        this.guildOnly = true;
        this.ticketStorage = ticketStorage;
        this.bot = bot;
        this.newSettingsManager = newSettingsManager;
        this.localeHandler = localeHandler;
    }

    protected void execute(CommandEvent event) {
        if(ticketStorage.getTicketChannel(event.getAuthor().getId()) == null) {
            String content = localeHandler.getLocale(event.getGuild(), "TICKET_CREATE_MESSAGE");
            event.reply(content);
            createTicket(event.getMember(), event.getGuild());
        }else{
            event.reply("You already have an open ticket!");
        }
    }

    public void createTicket(Member member, Guild guild){
        if(ticketStorage.getTicketID(member.getUser().getId()) == null) {
            TextChannel newChannel = createChannel(member.getUser(), guild);
            List<Role> roles = guild.getRolesByName("@everyone", true);
            Role role = newSettingsManager.getRole(guild, "supportRole");
            for (Role r : roles) {
                try {
                    updatePerms(r, newChannel, false);
                } catch (IllegalStateException ignored) {}
            }
            updatePerms(member, newChannel, true);

            if (role != null) {
                updatePerms(role, newChannel, true);
                sendTicketMessage(newChannel, member, role, guild);
            } else {
                sendTicketMessage(newChannel, member, null, guild);
            }
        }
    }


    public void updatePerms(Member member, TextChannel channel, boolean ok) {
        if (ok) {
            channel.createPermissionOverride(member).setAllow(
                    Permission.VIEW_CHANNEL,
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_READ,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_ATTACH_FILES,
                    Permission.MESSAGE_ADD_REACTION,
                    Permission.MESSAGE_EXT_EMOJI
            ).reason(MessageFormat.format(
                    "Added member {0}",
                    member.getEffectiveName()
            )).queue();
        } else{
            channel.createPermissionOverride(member).setDeny(
                    Permission.VIEW_CHANNEL,
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_READ,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_ATTACH_FILES,
                    Permission.MESSAGE_ADD_REACTION,
                    Permission.MESSAGE_EXT_EMOJI
            ).reason(MessageFormat.format(
                    "Added member {0}",
                    member.getEffectiveName()
            )).queue();
        }
    }

    public void updatePerms(Role owner, TextChannel channel, boolean ok){
        if (ok) {
            channel.createPermissionOverride(owner).setAllow(
                    Permission.VIEW_CHANNEL,
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_READ,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_ATTACH_FILES,
                    Permission.MESSAGE_ADD_REACTION,
                    Permission.MESSAGE_EXT_EMOJI
            ).reason(MessageFormat.format(
                    "Added member {0}", "DD"
            )).queue();
        } else{
            channel.createPermissionOverride(owner).setDeny(
                    Permission.VIEW_CHANNEL,
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_READ,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_ATTACH_FILES,
                    Permission.MESSAGE_ADD_REACTION,
                    Permission.MESSAGE_EXT_EMOJI
            ).reason(MessageFormat.format(
                    "Added member {0}", "DD"
            )).queue();
        }
    }


    public void sendTicketMessage(TextChannel channel, Member member, Role supportRole, Guild guild){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Ticket");
        String content = localeHandler.getLocale(guild, "TICKET_INFO_MESSAGE");
        String field_title = localeHandler.getLocale(guild, "TICKET_INFO_FIELD_TITLE");
        if (supportRole != null) {
            eb.addField(field_title,  content + member.getAsMention() + supportRole.getAsMention(), false);
        }else{
            eb.addField(field_title, content + member.getAsMention()
                    , false);
        }
        eb.setColor(Color.RED);
        channel.sendMessage(eb.build()).queue();
    }

    public TextChannel createChannel(User user, Guild guild){
        ChannelAction newchannel = guild.createTextChannel(user.getId()).setName(user.getName());
        TextChannel channel = (TextChannel) newchannel.complete();
        String id = channel.getId();
        String userid = user.getId();
        ticketStorage.writeSettings(userid, id);

        return channel;
    }
}
