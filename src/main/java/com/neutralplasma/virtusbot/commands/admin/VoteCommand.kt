package com.neutralplasma.virtusbot.commands.admin

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.commands.AdminCommand
import com.neutralplasma.virtusbot.storage.locale.LocaleHandler
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import java.awt.Color
import java.util.function.Consumer

class VoteCommand(localeHandler: LocaleHandler) : AdminCommand() {
    private val localeHandler: LocaleHandler
    override fun execute(commandEvent: CommandEvent) {
        val arg = commandEvent.args
        val args = arg.split(" ".toRegex()).toTypedArray()
        if (args.size >= 1) {
            commandEvent.message.delete().queue()
            val role = commandEvent.guild.getRoleById("687345987408232476")
            if(role != null && commandEvent.guild != null) {
                createVote(commandEvent.textChannel, arg, role, commandEvent.guild)
            }
        }
    }

    fun createVote(channel: TextChannel, suggestion: String, role: Role, guild: Guild) {
        val eb = EmbedBuilder()
        val field_title = localeHandler.getLocale(guild, "VOTE_FIELD_TITLE")
        val title = localeHandler.getLocale(guild, "VOTE_TITLE")
        eb.setTitle(title)
        eb.addField(field_title, "`$suggestion`", false)
        eb.addField("Tag", role.asMention, false)
        eb.setColor(Color.orange)
        val callback = Consumer { response: Message ->
            response.addReaction("\u2705").queue()
            response.addReaction("\u274C").queue()
        }
        channel.sendMessage(eb.build()).queue(callback)
    }

    init {
        name = "vote"
        help = "Dodas glasovanje"
        arguments = "<Text>"
        this.localeHandler = localeHandler
    }
}