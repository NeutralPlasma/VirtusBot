package com.neutralplasma.virtusbot.commands.timedcommands

import com.neutralplasma.virtusbot.commands.SubCommand
import com.neutralplasma.virtusbot.commands.SubCommandEvent
import com.neutralplasma.virtusbot.handlers.timedSending.TimedSender

class TimedTestSub(val timed: TimedSender): SubCommand() {
    override fun execute(sevent: SubCommandEvent) {
        val args = sevent.args
        val event = sevent.getCommandEvent()
            //timed.addChannel(event.guild, event.textChannel, args[0].toLong())
            timed.addTimedMessage(event.guild, args.toString(), "")
            event.reply("Sent test message awaiting...")

    }

    init {
        name = "test"
    }
}