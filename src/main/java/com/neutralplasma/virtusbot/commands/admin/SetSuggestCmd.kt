package com.neutralplasma.virtusbot.commands.admin

import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.commands.AdminCommand
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import com.neutralplasma.virtusbot.settings.SettingsList
import com.neutralplasma.virtusbot.storage.config.Info
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color

class SetSuggestCmd(private val newSettingsManager: NewSettingsManager) : AdminCommand() {
    override fun executeCommand(commandEvent: CommandEvent) {
        commandEvent.message.delete().queue()

        val eb = EmbedBuilder()
        eb.addField("Suggestion", "Type ${Info.PREFIX}suggest <suggestion> - to create a new suggestion.", false)
        eb.setColor(Color.orange)

        try {
            newSettingsManager.addStringData(commandEvent.guild, SettingsList.SUGGEST_CHANNEL, commandEvent.textChannel.id)
            //settingsManager.getSettings(commandEvent.getGuild()).setSuggestId(commandEvent.getChannel().getIdLong());
        } catch (error: NullPointerException) {
            error.printStackTrace()
        }
        commandEvent.channel.sendMessage(eb.build()).queue()
    }

    init {
        name = "setsuggest"
        help = "Sets suggestions channel"
    }
}