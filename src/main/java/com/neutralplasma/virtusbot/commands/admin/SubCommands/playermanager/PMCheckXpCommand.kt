package com.neutralplasma.virtusbot.commands.admin.SubCommands.playermanager

import com.jagrosh.jdautilities.commons.utils.FinderUtil
import com.neutralplasma.virtusbot.commands.SubCommand
import com.neutralplasma.virtusbot.commands.SubCommandEvent
import com.neutralplasma.virtusbot.event.MessageHandler
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerLeveling

class PMCheckXpCommand (private var playerManager: PlayerLeveling): SubCommand(){

    override fun execute(event: SubCommandEvent) {
        val args = event.args
        val mevent = event.getCommandEvent()

        val users = FinderUtil.findMembers(args[0], mevent.guild)
        if(users.isEmpty()){
            MessageHandler.sendError("You must provide user!", mevent.textChannel)
        }else if(users.size > 1){
            MessageHandler.sendError("You provided more than 1 user.", mevent.textChannel)
        }else{
            var playerData = playerManager.getUser(users[0].user, mevent.guild);
            if(playerData != null){
                MessageHandler.sendSuccess("Users xp =  ${playerData.xp}", mevent.textChannel)
                // SUCCESS HANDLER
            }
        }
    }

    init {
        name = "check"
    }
}