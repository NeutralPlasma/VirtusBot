package com.neutralplasma.virtusbot.commands.admin.SubCommands

import com.jagrosh.jdautilities.commons.utils.FinderUtil
import com.neutralplasma.virtusbot.commands.SubCommand
import com.neutralplasma.virtusbot.commands.SubCommandEvent

class TrashCommand(): SubCommand() {
    override fun execute(event: SubCommandEvent) {
        var commands = ""
        for(arg in event.args){
            commands += "$arg|"
        }

        var user = FinderUtil.findMembers(event.args.toString(), event.getCommandEvent().guild)
        if (user != null){
            event.getCommandEvent().guild.ban(user[0].id, 0, "Fuck you fucking idiot.")
        }

        event.getCommandEvent().reply("Okay provided subcommand $commands")

    }

    init {
        name = "trash"
    }
}