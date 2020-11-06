package com.neutralplasma.virtusbot.commands.admin

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.commands.AdminCommand
import com.neutralplasma.virtusbot.handlers.playerLeveling.MultiplierData
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerLeveling
import com.neutralplasma.virtusbot.utils.MathUtil.formatTimeLong
import com.neutralplasma.virtusbot.utils.TextUtil.formatTiming

class MultiplierCommand(playerLeveling: PlayerLeveling) : AdminCommand() {
    private val playerLeveling: PlayerLeveling
    override fun executeCommand(event: CommandEvent) {
        val args = event.args.split(" ".toRegex()).toTypedArray()
        if (args.size > 0) {
            if (args[0].equals("set", ignoreCase = true)) {
                if (args.size > 1) {
                    var data = playerLeveling.getMultiplierData(event.guild)
                    if (data == null) {
                        data = MultiplierData(1, 0)
                    }
                    val multiplier = args[1].toInt()
                    val time: Long
                    if (args.size > 2) {
                        time = formatTimeLong(args) * 1000
                        data.setMultiplier(multiplier, time)
                        playerLeveling.setMultiplier(event.guild, data)
                        event.reply("Successfully set multiplier: " + data.multiplier + " . For: " + formatTiming(time))
                    }
                }
            } else if (args[0].equals("check", ignoreCase = true)) {
                var data = playerLeveling.getMultiplierData(event.guild)
                if (data == null) {
                    data = MultiplierData(1, 0)
                }
                event.reply("Current multiplier: " + data.multiplier)
            }
        }
    }

    init {
        name = "multiplieradmin"
        help = "XP multiplier stuff."
        arguments = "<SUBCOMMAND>"
        this.playerLeveling = playerLeveling
    }
}