package com.neutralplasma.virtusbot.commands.general

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.commands.ExtendedCommand
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import com.neutralplasma.virtusbot.utils.AbstractChatUtil

class TestCommand(private val newSettingsManager: NewSettingsManager) : ExtendedCommand() {


    init {
        name = "test"
        help = "some testing"
        guildOnly = true

        subCommand = listOf(

        )
    }
}