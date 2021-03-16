package com.neutralplasma.virtusbot.handlers.timedSending

data class TimedMessageData(
    val createdOn: Long,
    val guildID: Long,
    val message: String,
    val filePath: String,
    var sent: Boolean,
    val sentTo: MutableList<String>
)
