package com.neutralplasma.virtusbot.storage.ticket

class TicketInfo(userID: String, channelID: String) {
    var userID = ""
    var channelID = ""

    init {
        this.userID = userID
        this.channelID = channelID
    }
}