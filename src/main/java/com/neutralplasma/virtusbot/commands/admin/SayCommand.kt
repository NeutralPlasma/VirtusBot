package com.neutralplasma.virtusbot.commands.admin

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.commands.AdminCommand
import com.neutralplasma.virtusbot.utils.AbstractChatUtil
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color
import java.util.*
import java.util.concurrent.atomic.AtomicReference

class SayCommand : AdminCommand() {
    private val savedMessages = HashMap<String, EmbedBuilder>()
    override fun execute(commandEvent: CommandEvent) {
        val args = commandEvent.args
        val cargs = args.split(" ".toRegex()).toTypedArray()
        val member = commandEvent.member
        if (cargs.size > 0) {
            var message = savedMessages[member.id]
            if (message == null) {
                message = EmbedBuilder()
            }
            val finalMessage: EmbedBuilder = message
            if (cargs[0].equals("settitle", ignoreCase = true)) {
                commandEvent.reply("Please send the title: ")
                val abstractChatUtil = AbstractChatUtil(commandEvent.author, { chatInfo: AbstractChatUtil.ChatConfirmEvent ->
                    finalMessage.setTitle(chatInfo.message)
                    savedMessages.put(member.id, finalMessage)
                }, commandEvent.jda)


                abstractChatUtil.onClose = {
                    commandEvent.reply("DONE")
                }

            } else if (cargs[0].equals("addfield", ignoreCase = true)) {
                commandEvent.reply("Please send the field title: ")
                val title = AtomicReference("")
                val abstractChatUtil = AbstractChatUtil(commandEvent.author, {
                    chatInfo: AbstractChatUtil.ChatConfirmEvent -> title.set(chatInfo.message)
                }, commandEvent.jda)

                abstractChatUtil.onClose = {
                    commandEvent.reply("Please provide the field content.")
                    val abstractChatUtil2 = AbstractChatUtil(commandEvent.author, { chatInfo2: AbstractChatUtil.ChatConfirmEvent ->
                        finalMessage.addField(title.get(), chatInfo2.message.replace("\\n", System.lineSeparator()), false)
                        savedMessages.put(member.id, finalMessage)
                    }, commandEvent.jda)

                    abstractChatUtil2.onClose = { commandEvent.reply("DONE") }

                }
            } else if (cargs[0].equals("color", ignoreCase = true)) {
                commandEvent.reply("Please send color in next format: RRR:GGG:BBB (255:255:255):")
                val abstractChatUtil = AbstractChatUtil(commandEvent.author, { chatInfo: AbstractChatUtil.ChatConfirmEvent ->
                    val color = chatInfo.message.split(":".toRegex()).toTypedArray()
                    if (color.size > 2) {
                        val color1 = Color(color[0].toInt(), color[1].toInt(), color[2].toInt())
                        finalMessage.setColor(color1)
                        savedMessages.put(member.id, finalMessage)
                    } else {
                        commandEvent.reply("Please follow the format: RRR:GGG:BBB")
                    }
                }, commandEvent.jda)

                abstractChatUtil.onClose =  { commandEvent.reply("Finished.") }

            } else if (cargs[0].equals("clear", ignoreCase = true)) {
                savedMessages.remove(member.id)
                commandEvent.reply("DONE")
            } else if (cargs[0].equals("send", ignoreCase = true)) {
                commandEvent.reply(finalMessage.build())
            } else {
                commandEvent.reply(info.build())
            }
        } else {
            commandEvent.reply(info.build())
        }
        //commandEvent.getMessage().delete().queueAfter(1L, TimeUnit.SECONDS);
    }

    val info: EmbedBuilder
        get() {
            val eb = EmbedBuilder()
            eb.setTitle("Info:")
            eb.setColor(Color.magenta.brighter())
            eb.addField("Available subcommands",
                    """**send** - Sends message
**color** - Set the color of the message border
**addfield** - Add the field to message
**removefield <id>** - Remove field from message
**settitle** - Set the title of message
**clear** - Clears the current saved message
 **getfields** - Lists all the fields with their titles
**gettitle** - Get the current title
 **preview** - Sends the preview message""", false)
            return eb
        }

    init {
        name = "say"
        help = "Bot says stuff instead of you in embed."
        arguments = "<SUBCOMMAND>"
    }
}