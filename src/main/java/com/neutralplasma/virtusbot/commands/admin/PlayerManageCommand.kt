package com.neutralplasma.virtusbot.commands.admin

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.commands.AdminCommand
import com.neutralplasma.virtusbot.commands.admin.SubCommands.playermanager.PMAddXpCommand
import com.neutralplasma.virtusbot.commands.admin.SubCommands.playermanager.PMCheckXpCommand
import com.neutralplasma.virtusbot.commands.admin.SubCommands.playermanager.PMRemoveXpCommand
import com.neutralplasma.virtusbot.commands.admin.SubCommands.playermanager.PMResetCommand
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerLeveling
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import com.neutralplasma.virtusbot.settings.SettingsList
import com.neutralplasma.virtusbot.storage.config.Info
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color

class PlayerManageCommand(private val playerManager: PlayerLeveling) : AdminCommand() {
    override fun executeCommand(commandEvent: CommandEvent) {
        commandEvent.message.delete().queue()
        commandEvent.reply("Lol")
    }

    init {
        name = "pm"
        help = "Manage playerdata"
        subCommand = listOf(
                PMAddXpCommand(playerManager),
                PMRemoveXpCommand(playerManager),
                PMResetCommand(playerManager),
                PMCheckXpCommand(playerManager)
        )
    }
}