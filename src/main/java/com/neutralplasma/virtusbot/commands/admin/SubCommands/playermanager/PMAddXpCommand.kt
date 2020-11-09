package com.neutralplasma.virtusbot.commands.admin.SubCommands.playermanager

import com.jagrosh.jdautilities.commons.utils.FinderUtil
import com.neutralplasma.virtusbot.commands.SubCommand
import com.neutralplasma.virtusbot.commands.SubCommandEvent
import com.neutralplasma.virtusbot.event.MessageHandler
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerLeveling

class PMAddXpCommand (private var playerManager: PlayerLeveling): SubCommand(){

    override fun execute(event: SubCommandEvent) {
        var args = event.args
        var mevent = event.getCommandEvent()


        val users = FinderUtil.findMembers(args[0], mevent.guild)
        if(users.isEmpty()){
            MessageHandler.sendError("You must provide user!", mevent.textChannel)
        }else if(users.size > 1){
            MessageHandler.sendError("You provided more than 1 user.", mevent.textChannel)
        }else{
            val value = args[1].toLong()
            playerManager.addXp(users[0].user, mevent.guild, value)

            MessageHandler.sendSuccess("Successfully added xp to user ${users[0].effectiveName}", mevent.textChannel)
        }
    }

    init {
        name = "add"
    }
}