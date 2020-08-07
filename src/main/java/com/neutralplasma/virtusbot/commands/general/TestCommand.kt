package com.neutralplasma.virtusbot.commands.general

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import com.neutralplasma.virtusbot.storage.locale.LocaleHandler
import com.neutralplasma.virtusbot.utils.AbstractChatUtil

class TestCommand(newSettingsManager: NewSettingsManager, localeHandler: LocaleHandler) : Command() {
    private val newSettingsManager: NewSettingsManager
    private val localeHandler: LocaleHandler
    override fun execute(event: CommandEvent) {
        event.reply("Doing stuff.")
        val abstractChatUtil = AbstractChatUtil(event.author, { chatInfo: AbstractChatUtil.ChatConfirmEvent -> event.reply(chatInfo.message) }, event.jda)
        abstractChatUtil.onClose = { event.reply("DONE") }
    }

    init {
        name = "test"
        help = "some testing"
        guildOnly = true
        this.newSettingsManager = newSettingsManager
        this.localeHandler = localeHandler
    }
}