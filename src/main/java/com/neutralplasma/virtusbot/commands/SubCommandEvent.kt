package com.neutralplasma.virtusbot.commands

import com.jagrosh.jdautilities.command.CommandEvent

class SubCommandEvent (private val event: CommandEvent) {
    var args : MutableList<String> = mutableListOf()


    init {

        args = event.args.split(" ").toMutableList()

        if(args.isNotEmpty()){ args.removeFirst()}

    }

    fun getCommandEvent(): CommandEvent{
        return this.event
    }



}