
package com.neutralplasma.virtusbot.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import java.util.function.Predicate

abstract class OwnerCommand : Command() {
    init {
        this.category = Category("Owner", Predicate { event: CommandEvent -> event.author.id == event.client.ownerId})
        guildOnly = false
    }
}