package com.neutralplasma.virtusbot.commands.timedcommands

import com.neutralplasma.virtusbot.commands.SubCommand
import com.neutralplasma.virtusbot.commands.SubCommandEvent
import com.neutralplasma.virtusbot.handlers.timedSending.TimedSender
import com.neutralplasma.virtusbot.utils.TextUtil

class TimedRemoveSub(val timed: TimedSender): SubCommand() {
    override fun execute(sevent: SubCommandEvent) {
        val event = sevent.getCommandEvent()
        if(timed.removeChannel(event.guild, event.textChannel)){
            event.reply("Removed channel.")
            TextUtil.sendMessage("Removed channel ${event.textChannel.idLong} to guild ${event.guild.idLong}")
        }else{
            event.reply("Unknown channel.")
        }
    }

    init {
        name = "removechannel"
    }
}