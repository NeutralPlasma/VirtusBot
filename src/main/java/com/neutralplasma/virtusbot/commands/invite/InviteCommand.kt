package com.neutralplasma.virtusbot.commands.invite

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.commons.utils.FinderUtil
import com.neutralplasma.virtusbot.commands.InviteCommand
import com.neutralplasma.virtusbot.commands.PlayerCommand
import com.neutralplasma.virtusbot.commands.invite.subcommands.Leaderboard
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerData
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerLeveling

class InviteCommand() : InviteCommand() {


    override fun executeCommand(commandEvent: CommandEvent) {
        commandEvent.message.delete().queue()
    }


    init {
        name = "invite"
        help = "Main invite command"
        cooldown = 10
        arguments = "<subcommand>"


        subCommand = listOf(
            Leaderboard()
        )
    }
}