package com.neutralplasma.virtusbot.utils

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import java.text.MessageFormat

object PermissionUtil {

    @JvmStatic
    fun updatePermsMember(member: Member, channel: TextChannel, allow: Boolean) {
        if (allow) {
            channel.putPermissionOverride(member).setAllow(
                Permission.VIEW_CHANNEL,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_READ,
                Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS,
                Permission.MESSAGE_ATTACH_FILES,
                Permission.MESSAGE_ADD_REACTION,
                Permission.MESSAGE_EXT_EMOJI
            ).queue()
        } else {
            channel.putPermissionOverride(member).setDeny(
                Permission.VIEW_CHANNEL,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_READ,
                Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS,
                Permission.MESSAGE_ATTACH_FILES,
                Permission.MESSAGE_ADD_REACTION,
                Permission.MESSAGE_EXT_EMOJI
            ).queue()
        }
    }

    @JvmStatic
    fun updatePermsRole(owner: Role?, channel: TextChannel, allow: Boolean) {
        if (allow) {
            channel.putPermissionOverride(owner!!).setAllow(
                Permission.VIEW_CHANNEL,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_READ,
                Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS,
                Permission.MESSAGE_ATTACH_FILES,
                Permission.MESSAGE_ADD_REACTION,
                Permission.MESSAGE_EXT_EMOJI
            ).queue()
        } else {
            channel.putPermissionOverride(owner!!).setDeny(
                Permission.VIEW_CHANNEL,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_READ,
                Permission.MESSAGE_HISTORY,
                Permission.MESSAGE_EMBED_LINKS,
                Permission.MESSAGE_ATTACH_FILES,
                Permission.MESSAGE_ADD_REACTION,
                Permission.MESSAGE_EXT_EMOJI
            ).queue()
        }
    }
}