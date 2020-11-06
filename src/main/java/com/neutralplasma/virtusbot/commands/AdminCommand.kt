
package com.neutralplasma.virtusbot.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.VirtusBot.blackList
import net.dv8tion.jda.api.Permission
import java.util.function.Predicate

abstract class AdminCommand : ExtendedCommand() {
    init {
        this.category = Category("Admin", Predicate { event: CommandEvent ->
            !blackList.isBlackListed(event.author.id)
                    &&
            event.author.id == event.client.ownerId
                    ||
            event.member.hasPermission(Permission.MANAGE_SERVER)
        })
        guildOnly = true
    }
}