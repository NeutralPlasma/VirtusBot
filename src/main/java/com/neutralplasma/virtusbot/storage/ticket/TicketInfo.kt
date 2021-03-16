package com.neutralplasma.virtusbot.storage.ticket

class TicketInfo(userID: String, channelID: String, closed: Int, guildID: String) {
    var userID = ""
    var channelID = ""
    var guildID = ""
    var closed = 0

    init {
        this.userID = userID
        this.channelID = channelID
        this.closed = closed
        this.guildID = guildID
    }
}