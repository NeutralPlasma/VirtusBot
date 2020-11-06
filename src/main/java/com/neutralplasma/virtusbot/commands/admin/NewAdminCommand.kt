package com.neutralplasma.virtusbot.commands.admin

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.commands.AdminCommand
import com.neutralplasma.virtusbot.commands.SubCommand
import com.neutralplasma.virtusbot.commands.SubCommandEvent
import com.neutralplasma.virtusbot.commands.admin.SubCommands.TrashCommand
import com.neutralplasma.virtusbot.utils.AbstractChatUtil
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color
import java.util.*
import java.util.concurrent.atomic.AtomicReference

class NewAdminCommand : AdminCommand() {
    private val savedMessages = HashMap<String, EmbedBuilder>()
    override fun executeCommand(commandEvent: CommandEvent) {
        commandEvent.reply("No sub command provided.")
    }


    init {
        name = "trash"
        help = "Bot says stuff instead of you in embed."
        arguments = "<SUBCOMMAND>"
        subCommand = listOf(
                TrashCommand()
        )
    }
}