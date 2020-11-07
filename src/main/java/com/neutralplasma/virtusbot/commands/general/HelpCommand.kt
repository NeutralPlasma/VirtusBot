package com.neutralplasma.virtusbot.commands.general

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.menu.Paginator
import com.neutralplasma.virtusbot.Bot
import com.neutralplasma.virtusbot.VirtusBot
import com.neutralplasma.virtusbot.VirtusBot.commands
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import com.neutralplasma.virtusbot.utils.TextUtil.filter
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.exceptions.PermissionException
import java.awt.Color
import java.util.*
import java.util.concurrent.TimeUnit

class HelpCommand(private val newSettingsManager: NewSettingsManager, bot: Bot) : Command() {
    override fun execute(event: CommandEvent) {
        val args = event.args.split(" ".toRegex()).toTypedArray()


        val pages = mutableListOf<EmbedBuilder>()

        if(args.isNotEmpty()){
            for(command in commands){
                if(command.name.equals(args[0], true)){
                    val embed = EmbedBuilder()
                    embed.addField(command.name,
                            "Command information: \n" +
                                    "```fix\n" +
                                    "Help: ${command.help}\n" +
                                    "Arguments: ${command.arguments}\n" +
                                    "```", false)
                    event.reply(embed.build())
                    return
                }
            }
        }


        for (category in VirtusBot.commandCategories){
            val embed = EmbedBuilder()
            var commandstext = "```ini\n[\n"
            for(command in commands){
                if(command.category != null) if(command.category.name == category) commandstext += command.name + " | " + command.help +  ",\n"
                if(command.category == null && category == "general") commandstext += command.name + " | " + command.help +  ",\n"
            }
            commandstext += "]```"
            embed.addField("Commands | Category: $category", commandstext, false)
            pages.add(embed)

        }


        val paginator = com.neutralplasma.virtusbot.utils.Paginator(pages)
        paginator.build(event.textChannel, event.author)

    }

    init {
        name = "help"
        help = "Main help command."
        arguments = "<command>"
        guildOnly = true

    }
}