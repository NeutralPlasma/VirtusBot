package com.neutralplasma.virtusbot.commands

import java.lang.Exception

abstract class SubCommand{
    var name : String = "Null"
    var help : String = "No help provided."


    protected open fun execute(event: SubCommandEvent){}

    fun run(event: SubCommandEvent){
        try {
            this.execute(event)
        }catch (error: Exception){
            event.getCommandEvent().reply("Error occured: " + error.message)
        }
    }

}