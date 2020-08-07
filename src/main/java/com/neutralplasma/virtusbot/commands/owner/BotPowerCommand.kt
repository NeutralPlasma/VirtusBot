package com.neutralplasma.virtusbot.commands.owner

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.commands.OwnerCommand
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerLeveling
import com.neutralplasma.virtusbot.handlers.playerSettings.PlayerSettingsHandler
import java.sql.SQLException

class BotPowerCommand(playerLeveling: PlayerLeveling, playerSettingsHandler: PlayerSettingsHandler) : OwnerCommand() {
    var playerLeveling: PlayerLeveling
    var playerSettingsHandler: PlayerSettingsHandler
    override fun execute(commandEvent: CommandEvent) {
        val guild = commandEvent.guild
        val args = commandEvent.args.split(" ".toRegex()).toTypedArray()
        if (args.size > 0) {
            if (args[0].equals("save", ignoreCase = true)) {
                try {
                    playerSettingsHandler.syncSettings()
                } catch (error: SQLException) {
                    error.printStackTrace()
                    commandEvent.reply("Failed saving player settings. (check console)")
                }
                try {
                    playerLeveling.syncUsers()
                } catch (error: SQLException) {
                    error.printStackTrace()
                    commandEvent.reply("Failed saving player leveling (check console)")
                }
                commandEvent.reply("Done")
            } else if (args[0].equals("stop", ignoreCase = true)) {
                try {
                    playerLeveling.syncUsers()
                    playerSettingsHandler.syncSettings()
                } catch (error: SQLException) {
                    error.printStackTrace()
                    commandEvent.reply("Failed saving data! (check console)")
                }
                commandEvent.reply("Stopping...")
                System.exit(1)
            }
        }
    }

    init {
        name = "botpower"
        help = "Owner bot managing command."
        arguments = "<SUBCOMMAND>"
        this.playerLeveling = playerLeveling
        this.playerSettingsHandler = playerSettingsHandler
    }
}