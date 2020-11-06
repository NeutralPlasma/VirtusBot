package com.neutralplasma.virtusbot.commands.admin.SubCommands

import com.neutralplasma.virtusbot.commands.SubCommand
import com.neutralplasma.virtusbot.commands.SubCommandEvent

class TrashCommand(): SubCommand() {
    override fun execute(event: SubCommandEvent) {
        var commands = ""
        for(arg in event.args){
            commands += "$arg|"
        }

        event.getCommandEvent().reply("Okay provided subcommand $commands")

    }

    init {
        name = "trash"
    }
}