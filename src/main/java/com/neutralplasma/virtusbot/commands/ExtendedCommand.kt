
package com.neutralplasma.virtusbot.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.VirtusBot.blackList
import net.dv8tion.jda.api.Permission
import java.util.function.Predicate

abstract class ExtendedCommand : Command() {
    var subCommand: List<SubCommand> = listOf()

    override fun execute(p0: CommandEvent?) {
        var executed = false
        if(p0 != null){
            for(command in subCommand){
                if(command.name == p0.args.split(" ")[0]){
                    command.run(SubCommandEvent(p0))
                    executed = true
                }
            }
            if(!executed) executeCommand(p0)
        }
    }

    protected open fun executeCommand(commandEvent: CommandEvent){}
}