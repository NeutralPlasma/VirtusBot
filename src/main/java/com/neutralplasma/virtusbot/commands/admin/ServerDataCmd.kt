package com.neutralplasma.virtusbot.commands.admin

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.commons.utils.FinderUtil
import com.neutralplasma.virtusbot.commands.AdminCommand
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerData
import com.neutralplasma.virtusbot.handlers.playerLeveling.PlayerLeveling
import com.neutralplasma.virtusbot.handlers.playerSettings.PlayerSettingsHandler
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import com.neutralplasma.virtusbot.settings.SettingsList
import com.neutralplasma.virtusbot.storage.dataStorage.StorageHandler
import com.neutralplasma.virtusbot.utils.FormatUtil.listOfRoles
import com.neutralplasma.virtusbot.utils.FormatUtil.listOfTCategories
import com.neutralplasma.virtusbot.utils.FormatUtil.listOfTChannels
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color

class ServerDataCmd(settingsManager: NewSettingsManager,
                    playerLeveling: PlayerLeveling, sql: StorageHandler, playerSettingsHandler: PlayerSettingsHandler) : AdminCommand() {
    private val sql: StorageHandler
    private val settingsManager: NewSettingsManager
    private val playerLeveling: PlayerLeveling
    private val playerSettingsHandler: PlayerSettingsHandler
    override fun executeCommand(commandEvent: CommandEvent) {
        val arg = commandEvent.args
        val guild = commandEvent.guild
        val args = arg.split(" ".toRegex()).toTypedArray()
        commandEvent.reply("Args length " + args.size + " Args: `" + arg + "` ")
        if (args.size >= 1) {
            if (args[0].equals("setrole", ignoreCase = true)) {
                if (args.size > 2) {
                    val setting = SettingsList.valueOf(args[1])
                    val roles = FinderUtil.findRoles(args[2], guild)
                    if (roles.isEmpty()) {
                        commandEvent.reply("No role found!")
                    } else if (roles.size > 1) {
                        commandEvent.reply(commandEvent.client.warning + listOfRoles(roles, arg))
                    } else {
                        commandEvent.reply("Set: " + roles[0].asMention)
                        settingsManager.addStringData(guild, setting, roles[0].id)
                    }
                } else {
                    commandEvent.reply("**setrole:** <setting> <data>")
                }
            } else if (args[0].equals("setchannel", ignoreCase = true)) {
                if (args.size > 2) {
                    val setting = SettingsList.valueOf(args[1])
                    val channels = FinderUtil.findTextChannels(args[2], guild)
                    if (channels.isEmpty()) {
                        commandEvent.reply("No channel found!")
                    } else if (channels.size > 1) {
                        commandEvent.reply(commandEvent.client.warning + listOfTChannels(channels, arg))
                    } else {
                        commandEvent.reply("Set: " + channels[0].asMention)
                        settingsManager.addStringData(guild, setting, channels[0].id)
                    }
                } else {
                    commandEvent.reply("**set:** <setting> <data>")
                }
            } else if (args[0].equals("setcategory", ignoreCase = true)) {
                if (args.size > 2) {
                    val setting = SettingsList.valueOf(args[1])
                    val categories = FinderUtil.findCategories(args[2], guild)
                    if (categories.isEmpty()) {
                        commandEvent.reply("No Categories found!")
                    } else if (categories.size > 1) {
                        commandEvent.reply(commandEvent.client.warning + listOfTCategories(categories, arg))
                    } else {
                        commandEvent.reply("Set: " + categories[0].name)
                        settingsManager.addStringData(guild, setting, categories[0].id)
                    }
                }
            } else if (args[0].equals("getdata", ignoreCase = true)) {
                val setting = SettingsList.valueOf(args[1])
                commandEvent.reply("Data: `" + settingsManager.getData(guild, setting) + "`")
            } else if (args[0].equals("setstringdata", ignoreCase = true)) {
                if (args.size > 2) {
                    val setting = SettingsList.valueOf(args[1])
                    val data = args[2]
                    settingsManager.addStringData(guild, setting, data)
                } else {
                    commandEvent.reply("**setrole:** <setting> <data>")
                }
            }  else if (args[0].equals("test", ignoreCase = true)) {
                try {
                    var data = playerLeveling.getUser(commandEvent.author, commandEvent.guild)
                    if(data != null) {
                        playerLeveling.sendLevelUpMessage(commandEvent.author, data, commandEvent.textChannel)
                    }else{
                        data = PlayerData(commandEvent.author.idLong, commandEvent.guild.idLong, 0L, 0)
                        playerLeveling.addUser(data)
                        playerLeveling.sendLevelUpMessage(commandEvent.author, data, commandEvent.textChannel)
                    }
                } catch (error: Exception) {
                    error.printStackTrace()
                }
            }else if (args[0].equals("testlevel", ignoreCase = true)){
                val playerData = playerLeveling.getUser(commandEvent.author, commandEvent.guild)
                if(playerData != null){
                    playerLeveling.sendLevelUpMessage(commandEvent.author, playerData, commandEvent.textChannel)
                }
            }
        } else {
            val eb = EmbedBuilder()
            eb.setTitle("SubCommands")
            eb.addField("List:", """**data:** <steamid> - gets player data 
**money:** <steamid> - gets player money 
**bank:** <steamid> - gets player bank 
**setrole:** <setting> <data> - sets server setting 
**setchannel:** <setting> <data> - sets server setting 
**adminget:** <setting> - returns setting from SQL 
 **adminadd** <setting> <data> - adds data to SQL 
 **setlocale** <locale> <locale_text> - adds localedata to locales 
 **getlocales** - gets all possible locales to be set 
 **setcategory** <setting> <category> - Set the category
 """, false)
            eb.setColor(Color.ORANGE)
            commandEvent.reply(eb.build())
        }
    }

    init {
        name = "serverdata"
        help = "Simple command for checking server stuff"
        arguments = "<SUBCOMMAND>"
        this.settingsManager = settingsManager
        this.playerLeveling = playerLeveling
        this.sql = sql
        this.playerSettingsHandler = playerSettingsHandler
    }
}