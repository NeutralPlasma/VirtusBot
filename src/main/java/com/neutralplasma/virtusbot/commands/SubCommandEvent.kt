package com.neutralplasma.virtusbot.commands

import com.jagrosh.jdautilities.command.CommandEvent

class SubCommandEvent (private val event: CommandEvent) {
    public var args : MutableList<String> = mutableListOf()


    init {

        args = event.args.split(" ") as MutableList<String>
        if(args.size > 0) args.removeAt(0)

    }

    fun getCommandEvent(): CommandEvent{
        return this.event
    }



}