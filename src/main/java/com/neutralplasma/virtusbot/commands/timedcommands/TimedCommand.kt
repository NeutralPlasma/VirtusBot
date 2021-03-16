
package com.neutralplasma.virtusbot.commands.timedcommands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.VirtusBot.blackList
import com.neutralplasma.virtusbot.commands.ExtendedCommand
import java.util.function.Predicate


abstract class TimedCommand : ExtendedCommand() {

    init {
        guildOnly = true
        this.category = Category("TimedCommand", Predicate {
            event: CommandEvent ->
            !blackList.isBlackListed(event.author.id)
        })
    }
}