package com.neutralplasma.virtusbot.roleAutomation

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageReaction

class ReactionRoleHandler {
    val reactions: MutableList<ReactionData> = mutableListOf()

    init {
        val reactions2 = mutableMapOf(
                Pair("RE:U+1f346", "806902097386668092"),
                Pair("RE:U+1f58aU+fe0f", "806902678763470898"),
                Pair("RE:U+1f464", "804046517534851163"))

        val data2 = ReactionData(
                "804035274409377863",
                "807255601079910420",
                reactions2
        )

        this.reactions.add(data2)
    }


    fun addRoles(guild: Guild, emote: MessageReaction.ReactionEmote, message: String, member: Member?){
        if(member == null){
            return
        }
        for(data in reactions){
            if(data.serverID == guild.id && data.messageID == message){
                data.reactions.forEach { (reaction, role) ->
                    if(emote.toString() == reaction){
                        val role = guild.getRoleById(role)
                        if(role != null){
                            if(!member.roles.contains(role)){
                                guild.addRoleToMember(member, role).queue()
                            }
                        }
                    }
                }
            }
        }
    }

    fun removeRoles(guild: Guild, emote: MessageReaction.ReactionEmote, message: String, member: Member?){
        if(member == null){
            return
        }
        for(data in reactions){
            if(data.serverID == guild.id && data.messageID == message){
                data.reactions.forEach { (reaction, role) ->
                    if(emote.toString() == reaction){
                        val role = guild.getRoleById(role)
                        if(role != null){
                            if(member.roles.contains(role)){
                                guild.removeRoleFromMember(member, role).queue()
                            }
                        }
                    }
                }
            }
        }
    }
}