package com.neutralplasma.virtusbot.commands.player

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.commands.PlayerCommand
import com.neutralplasma.virtusbot.handlers.playerSettings.PlayerSettings
import com.neutralplasma.virtusbot.handlers.playerSettings.PlayerSettingsHandler
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color
import java.net.URL

class PlayerSettingsCommand(playerSettingsHandler: PlayerSettingsHandler) : PlayerCommand() {
    private val playerSettingsHandler: PlayerSettingsHandler
    override fun execute(commandEvent: CommandEvent) {
        commandEvent.message.delete().queue()
        val args = commandEvent.args.split(" ".toRegex()).toTypedArray()
        if (args.size > 0) {
            var playerSettings = playerSettingsHandler.getSettings(commandEvent.author)
            if (playerSettings == null) {
                playerSettings = PlayerSettings(false, "", Color.orange, Color.red)
            }
            if (args[0].equals("toggleDark", ignoreCase = true)) {
                playerSettings.setDarkTheme(!playerSettings.isDarkTheme())
                if (playerSettings.isDarkTheme()) {
                    commandEvent.reply("Enabled dark theme!")
                } else {
                    commandEvent.reply("Disabled dark theme!")
                }
                playerSettingsHandler.updateUser(commandEvent.author, playerSettings)
            } else if (args[0].equals("avatarBackGround", ignoreCase = true)) {
                commandEvent.reply("URL: " + playerSettings.getAvatarBackgroundImage())
            } else if (args[0].equals("setColor1", ignoreCase = true)) {
                if (args.size > 1) {
                    val color = args[1].split(":".toRegex()).toTypedArray()
                    if (color.size > 2) {
                        playerSettings.setColor1(Color(color[0].toInt(), color[1].toInt(), color[2].toInt()))
                        commandEvent.reply("Successfully set the color.")
                        playerSettingsHandler.updateUser(commandEvent.author, playerSettings)
                        return
                    } else {
                        commandEvent.reply("Follow this format: RRR:GGG:BBB")
                        return
                    }
                }
                commandEvent.reply("Provide color...")
            } else if (args[0].equals("setColor2", ignoreCase = true)) {
                if (args.size > 1) {
                    val color = args[1].split(":".toRegex()).toTypedArray()
                    if (color.size > 2) {
                        playerSettings.setColor2(Color(color[0].toInt(), color[1].toInt(), color[2].toInt()))
                        commandEvent.reply("Successfully set the color.")
                        playerSettingsHandler.updateUser(commandEvent.author, playerSettings)
                        return
                    } else {
                        commandEvent.reply("Follow this format: RRR:GGG:BBB")
                        return
                    }
                }
                commandEvent.reply("Provide color...")
            } else if (args[0].equals("resetColor1", ignoreCase = true)) {
                playerSettings.setColor1(Color.orange)
                playerSettingsHandler.updateUser(commandEvent.author, playerSettings)
                commandEvent.reply("Reset the color.")
            } else if (args[0].equals("resetColor2", ignoreCase = true)) {
                playerSettings.setColor2(Color.red)
                playerSettingsHandler.updateUser(commandEvent.author, playerSettings)
                commandEvent.reply("Reset the color.")
            } else if (args[0].equals("setBackGround", ignoreCase = true)) {
                if (args.size > 1) {
                    try {
                        URL(args[1]).toURI()
                        playerSettings.setAvatarBackgroundImage(args[1])
                        playerSettingsHandler.updateUser(commandEvent.author, playerSettings)
                    } catch (exception: Exception) {
                        commandEvent.reply("Invalid URL link!")
                    }
                } else {
                    playerSettings.setAvatarBackgroundImage("")
                    playerSettingsHandler.updateUser(commandEvent.author, playerSettings)
                }
            } else {
                commandEvent.reply(infoMessage.build())
            }
        } else {
            commandEvent.reply(infoMessage.build())
        }
    }

    val infoMessage: EmbedBuilder
        get() {
            val eb = EmbedBuilder()
            eb.addField("Commands", """
     
     **toggleDark** - Enables/Disables dark theme. 
     **avatarBackGround** - Returns current avatar background url. 
     **setColor1** <RRR:GGG:BBB> - Sets the first color. 
     **setColor2** <RRR:GGG:BBB> - Sets the second color. 
     **resetColor1**  - Resets the second color. 
     **resetColor2** - Resets the second color. 
     **setBackGround** <URL> - Sets new avatar background image.
     """.trimIndent(), false)
            eb.setColor(Color.MAGENTA)
            return eb
        }

    init {
        name = "psettings"
        help = "Gets your or someones stats"
        arguments = "<USER/NONE>"
        this.playerSettingsHandler = playerSettingsHandler
    }
}