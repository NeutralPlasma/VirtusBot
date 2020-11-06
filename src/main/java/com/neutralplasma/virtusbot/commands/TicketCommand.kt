
package com.neutralplasma.virtusbot.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.VirtusBot.blackList
import java.util.function.Predicate


abstract class TicketCommand : Command() {
    override fun execute(commandEvent: CommandEvent) {}



    init {
        guildOnly = true
        this.category = Category("Ticket", Predicate {
            event: CommandEvent ->
            !blackList.isBlackListed(event.author.id)
        })
    }
}