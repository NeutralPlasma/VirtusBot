package com.neutralplasma.virtusbot.commands.owner

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.commons.utils.FinderUtil
import com.neutralplasma.virtusbot.VirtusBot.blackList
import com.neutralplasma.virtusbot.commands.OwnerCommand

class BlackListCommand : OwnerCommand() {
    override fun execute(commandEvent: CommandEvent) {
        val guild = commandEvent.guild
        val args = commandEvent.args.split(" ".toRegex()).toTypedArray()
        if (args.size > 0) {
            if (args[0].equals("add", ignoreCase = true)) {
                val members = FinderUtil.findMembers(args[1], guild)
                if (members.isEmpty()) {
                    commandEvent.reply("No user found!")
                } else if (members.size > 1) {
                    commandEvent.reply(commandEvent.client.warning + "You need to specify just 1 user!")
                } else {
                    blackList!!.addToBlackList(members[0].id)
                    commandEvent.reply("Added to blacklist: " + members[0].id)
                }
            }
            if (args[0].equals("remove", ignoreCase = true)) {
                val members = FinderUtil.findMembers(args[1], guild)
                if (members.isEmpty()) {
                    commandEvent.reply("No user found!")
                } else if (members.size > 1) {
                    commandEvent.reply(commandEvent.client.warning + "You need to specify just 1 user!")
                } else {
                    blackList!!.removeFromBlackList(members[0].id)
                    commandEvent.reply("Removed from blacklist: " + members[0].id)
                }
            }
            if (args[0].equals("check", ignoreCase = true)) {
                val members = FinderUtil.findMembers(args[1], guild)
                if (members.isEmpty()) {
                    commandEvent.reply("No user found!")
                } else if (members.size > 1) {
                    commandEvent.reply(commandEvent.client.warning + "You need to specify just 1 user!")
                } else {
                    if (blackList!!.isBlackListed(members[0].id)) {
                        commandEvent.reply("User is blacklisted!")
                    } else {
                        commandEvent.reply("User isn't blacklisted!")
                    }
                }
            }
        }
    }

    init {
        name = "blacklist"
        help = "Owner blacklist."
        arguments = "<SUBCOMMAND>"
    }
}