package com.neutralplasma.virtusbot.storage.ticket

class TicketInfo(userID: String, channelID: String, closed: Int) {
    var userID = ""
    var channelID = ""
    var closed = 0

    init {
        this.userID = userID
        this.channelID = channelID
        this.closed = closed
    }
}