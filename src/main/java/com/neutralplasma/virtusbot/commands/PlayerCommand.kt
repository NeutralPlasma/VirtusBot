package com.neutralplasma.virtusbot.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.VirtusBot.blackList
import java.util.function.Predicate

abstract class PlayerCommand : Command() {
    init {
        this.category = Category("Player", Predicate { event: CommandEvent -> !blackList!!.isBlackListed(event.author.id) })
        guildOnly = true
    }
}