package com.neutralplasma.virtusbot.commands.timedcommands

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.commands.TicketCommand
import com.neutralplasma.virtusbot.commands.admin.SubCommands.playermanager.PMAddXpCommand
import com.neutralplasma.virtusbot.commands.admin.SubCommands.playermanager.PMCheckXpCommand
import com.neutralplasma.virtusbot.commands.admin.SubCommands.playermanager.PMRemoveXpCommand
import com.neutralplasma.virtusbot.commands.admin.SubCommands.playermanager.PMResetCommand
import com.neutralplasma.virtusbot.handlers.timedSending.TimedSender
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import com.neutralplasma.virtusbot.settings.SettingsList
import com.neutralplasma.virtusbot.storage.ticket.TicketInfo
import com.neutralplasma.virtusbot.storage.ticket.TicketStorage
import com.neutralplasma.virtusbot.utils.AbstractReactionUtil
import com.neutralplasma.virtusbot.utils.PermissionUtil
import com.neutralplasma.virtusbot.utils.TextUtil
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import java.text.MessageFormat
import java.util.concurrent.TimeUnit

class MainTimedCommand(val timed: TimedSender) : TimedCommand() {
    override fun executeCommand(commandEvent: CommandEvent) {
        val embed = EmbedBuilder()
        embed.addField("Subcommands:","" +
                "**addchannel <seconds> -** Adds the channel to the tier list." +
                "**addchannel -** Removes the channel to the tier list." ,false)
        commandEvent.reply(embed.build()) {
            it.delete().queueAfter(10, TimeUnit.SECONDS)
        }
    }



    init {
        name = "timedmessages"
        help = "Main timedcommand"
        aliases = arrayOf("timedc")
        arguments = "<SUBCOMMAND>"
        guildOnly = true

        subCommand = listOf(
            TimedAddSub(timed),
            TimedTestSub(timed),
            TimedRemoveSub(timed)
        )
    }
}