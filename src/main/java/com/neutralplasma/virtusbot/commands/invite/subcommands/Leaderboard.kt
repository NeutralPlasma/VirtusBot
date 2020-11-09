package com.neutralplasma.virtusbot.commands.invite.subcommands

import com.neutralplasma.virtusbot.commands.SubCommand
import com.neutralplasma.virtusbot.commands.SubCommandEvent

class Leaderboard (): SubCommand(){
    override fun execute(sevent: SubCommandEvent) {
        var event = sevent.getCommandEvent()
        var args = sevent.args


        event.reply("Okay getting leaderboard...")

    }

    init {
        name = "leaderboard"
    }
}