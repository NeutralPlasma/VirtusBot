package com.neutralplasma.virtusbot.fivem

data class ServerInfo(
        val ip: String,
        val port: String,
        val maxPlayers: Int,
        var players: MutableList<String>,
        val guild: Long,
        val channel: Long,
        var messageID: Long = 0L,
        val connectIP: String
) {
    fun getPlayers(): String{
        var data = "`"
        for(player in players){
            data += "$player, "

        }
        if (data.length > 3) {
            data = data.removeRange(data.lastIndex - 1, data.lastIndex + 1)
        }
        data += "`"
        return data
    }
}