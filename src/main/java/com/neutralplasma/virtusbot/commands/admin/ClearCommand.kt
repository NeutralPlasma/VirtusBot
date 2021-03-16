package com.neutralplasma.virtusbot.commands.admin

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.commands.AdminCommand
import com.neutralplasma.virtusbot.handlers.playerLeveling.MultiplierData
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerLeveling
import com.neutralplasma.virtusbot.utils.MathUtil.formatTimeLong
import com.neutralplasma.virtusbot.utils.TextUtil.formatTiming

class ClearCommand() : AdminCommand() {
    override fun executeCommand(event: CommandEvent) {
        val amount = event.args.toInt()
        val history = event.channel.history
        val messages = history.retrievePast(amount).complete()
        event.channel.purgeMessages(messages)
        event.reply("Removed: $amount messages.")
    }

    init {
        name = "clear"
        help = "Purge message history"
        arguments = "<AMOUNT>"
    }
}