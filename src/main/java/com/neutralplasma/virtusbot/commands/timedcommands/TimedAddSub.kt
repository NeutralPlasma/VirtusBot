package com.neutralplasma.virtusbot.commands.timedcommands

import com.neutralplasma.virtusbot.commands.SubCommand
import com.neutralplasma.virtusbot.commands.SubCommandEvent
import com.neutralplasma.virtusbot.handlers.timedSending.TimedSender
import com.neutralplasma.virtusbot.utils.TextUtil

class TimedAddSub(val timed: TimedSender): SubCommand() {
    override fun execute(sevent: SubCommandEvent) {
        val args = sevent.args
        val event = sevent.getCommandEvent()
        if(args[0].toLongOrNull() != null){
            timed.addChannel(event.guild, event.textChannel, args[0].toLong() * 1000)
            event.reply("Successfully added ${event.textChannel.asMention}.")
            TextUtil.sendMessage("Added channel ${event.textChannel.idLong} to guild ${event.guild.idLong}")
        }else{
            event.reply("Error...")
        }
    }

    init {
        name = "addchannel"
    }
}