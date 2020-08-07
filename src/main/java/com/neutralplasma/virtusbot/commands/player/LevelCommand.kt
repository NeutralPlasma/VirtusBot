package com.neutralplasma.virtusbot.commands.player

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.commons.utils.FinderUtil
import com.neutralplasma.virtusbot.commands.PlayerCommand
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerData
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerLeveling

class LevelCommand(playerLeveling: PlayerLeveling) : PlayerCommand() {
    private val playerLeveling: PlayerLeveling
    override fun execute(commandEvent: CommandEvent) {
        commandEvent.message.delete().queue()
        val args = commandEvent.args.split(" ".toRegex()).toTypedArray()
        if (args.size > 0) {
            if (args[0].equals("", ignoreCase = true)) {
                var data = playerLeveling.getUser(commandEvent.author, commandEvent.guild)
                if (data != null) {
                    playerLeveling.sendInfoImage(commandEvent.author, data, commandEvent.textChannel)
                } else {
                    data = PlayerData(commandEvent.author.idLong, commandEvent.guild.idLong, 0L, 0)
                    playerLeveling.addUser(data)
                    playerLeveling.sendInfoImage(commandEvent.author, data, commandEvent.textChannel)
                }
            } else {
                val users = FinderUtil.findMembers(args[0], commandEvent.guild)
                if (users.size > 1) {
                    val dbuilder = StringBuilder()
                    for (user in users) {
                        dbuilder.append(user.user.name).append(" ")
                    }
                    commandEvent.reply("Specify just 1 user. Specified: $dbuilder")
                } else if (users.size == 0) {
                    commandEvent.reply("You need to specify a user.")
                } else {
                    val data = playerLeveling.getUser(users[0].user, commandEvent.guild)
                    if (data != null) {
                        playerLeveling.sendInfoImage(users[0].user, data, commandEvent.textChannel)
                    } else {
                        commandEvent.reply("NO DATA FOUND")
                    }
                }
            }
        } else {
        }
    }

    init {
        name = "level"
        help = "Gets your or someones stats"
        cooldown = 10
        arguments = "<USER/NONE>"
        this.playerLeveling = playerLeveling
    }
}