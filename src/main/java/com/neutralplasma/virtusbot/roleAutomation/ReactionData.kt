package com.neutralplasma.virtusbot.roleAutomation

data class ReactionData(
        val serverID: String,
        val messageID: String,
        var reactions: MutableMap<String, String>
)