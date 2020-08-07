package com.neutralplasma.virtusbot.handlers.playerLeveling

data class PlayerData(
        var userID: Long,
        var serverID: Long,
        var xp: Long,
        var level: Int
)