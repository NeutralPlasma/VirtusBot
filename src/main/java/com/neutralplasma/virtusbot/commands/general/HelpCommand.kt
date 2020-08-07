package com.neutralplasma.virtusbot.commands.general

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.menu.Paginator
import com.neutralplasma.virtusbot.Bot
import com.neutralplasma.virtusbot.VirtusBot.commands
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import com.neutralplasma.virtusbot.storage.locale.LocaleHandler
import com.neutralplasma.virtusbot.utils.TextUtil.filter
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.exceptions.PermissionException
import java.awt.Color
import java.util.*
import java.util.concurrent.TimeUnit

class HelpCommand(newSettingsManager: NewSettingsManager, localeHandler: LocaleHandler, bot: Bot) : Command() {
    private val newSettingsManager: NewSettingsManager
    private val localeHandler: LocaleHandler
    private val builder: Paginator.Builder
    override fun execute(event: CommandEvent) {
        val commands = commands
        val args = event.args.split(" ".toRegex()).toTypedArray()
        val content = ArrayList<String>()
        if (!args[0].equals("", ignoreCase = true)) {
            for (command in commands) {
                if (command.category != null && command.category.name.equals(args[0], ignoreCase = true)) {
                    content.add("`" + command.name + " " + (if (command.arguments == null) "" else command.arguments) + "`  - " + command.help)
                }
            }
        } else {
            for (command in commands) {
                content.add("`" + command.name + " " + (if (command.arguments == null) "" else command.arguments) + "`  - " + command.help)
            }
        }
        val list = arrayOfNulls<String>(content.size)
        for (i in content.indices) {
            list[i] = content[i]
        }
        builder.setText { i1: Int?, i2: Int? -> getQueueTitle(event.client.success, args[0]) }
                .setItems(*list)
                .setUsers(event.author)
                .setColor(Color.magenta.brighter())
        builder.build().paginate(event.channel, 1)
    }

    private fun getQueueTitle(success: String, category: String): String {
        return filter("$success | $category")
    }

    init {
        name = "help"
        help = "Main help command."
        guildOnly = true
        this.newSettingsManager = newSettingsManager
        this.localeHandler = localeHandler
        builder = Paginator.Builder()
                .setColumns(1)
                .setFinalAction { m: Message ->
                    try {
                        m.clearReactions().queue()
                    } catch (ignore: PermissionException) {
                    }
                }
                .setItemsPerPage(10)
                .waitOnSinglePage(false)
                .useNumberedItems(true)
                .showPageNumbers(true)
                .wrapPageEnds(true)
                .setEventWaiter(bot.waiter)
                .setTimeout(1, TimeUnit.MINUTES)
    }
}