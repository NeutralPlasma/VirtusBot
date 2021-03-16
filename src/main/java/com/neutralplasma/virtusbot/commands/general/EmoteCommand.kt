package com.neutralplasma.virtusbot.commands.general

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.commons.utils.FinderUtil
import com.jagrosh.jdautilities.menu.Paginator
import com.neutralplasma.virtusbot.Bot
import com.neutralplasma.virtusbot.VirtusBot
import com.neutralplasma.virtusbot.VirtusBot.commands
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import com.neutralplasma.virtusbot.utils.TextUtil.filter
import com.vdurmont.emoji.EmojiParser
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.exceptions.PermissionException
import java.awt.Color
import java.util.*
import java.util.concurrent.TimeUnit

class EmoteCommand() : Command() {
    override fun execute(event: CommandEvent) {
        val args = event.args.split(" ".toRegex()).toTypedArray()

        if(args.isNotEmpty()){
            val emotes = FinderUtil.findEmotes(event.args, event.jda)
            val emojies = EmojiParser.extractEmojis(event.args)
            for(emote in emotes){
                event.reply("Name: ${emote.name}\n" +
                        "String: ${emote}\n" +
                        "Is Animated: ${emote.isAnimated}\n" +
                        "")
            }

            for(emote in emojies){
                event.reply("```\nName: ${emote}\n" +
                        "```")
            }
        }
    }

    init {
        name = "emote"
        help = "Get emote information."
        arguments = "<emote>"
        guildOnly = true

    }
}