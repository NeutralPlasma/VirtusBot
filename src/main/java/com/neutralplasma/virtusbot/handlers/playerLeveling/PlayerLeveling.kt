package com.neutralplasma.virtusbot.handlers.playerLeveling

import com.neutralplasma.virtusbot.handlers.playerSettings.PlayerSettingsHandler
import com.neutralplasma.virtusbot.settings.NewSettingsManager
import com.neutralplasma.virtusbot.storage.dataStorage.StorageHandler
import com.neutralplasma.virtusbot.utils.GraphicUtil.dye
import com.neutralplasma.virtusbot.utils.Resizer
import com.neutralplasma.virtusbot.utils.TextUtil
import com.neutralplasma.virtusbot.utils.TextUtil.makeRoundedCorner
import com.neutralplasma.virtusbot.utils.TextUtil.sendMessage
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import java.awt.Color
import java.awt.Font
import java.awt.GradientPaint
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.net.URL
import java.sql.SQLException
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.roundToInt

class PlayerLeveling(private val storage: StorageHandler, private val playerSettingsHandler: PlayerSettingsHandler, private val settings: NewSettingsManager) {
    private val random = Random()
    private val blackListed = ArrayList<String>()
    private val tableName = "LevelingData"
    private val multipliers = HashMap<String, MultiplierData?>()
    var users = HashMap<String, PlayerData>()
    var updatingTask = Runnable {
        try {
            syncUsers()
        } catch (ignored: Exception) {
        }
    }

    /**
     * Runs Sync function every 1 minute.
     */
    fun userUpdater() {
        val t = Timer()
        t.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                updatingTask.run()
            }
        }, 100, 60000)
    }

    /**
     * Syncs HashMap of users to SQL.
     *
     * @throws SQLException if there is issue with SQL connection.
     */
    @Throws(SQLException::class)
    fun syncUsers() {
        val data = HashMap(users)
        storage.connection.use { connection ->
            val statement = "DELETE FROM $tableName;"
            connection!!.prepareStatement(statement).use { preparedStatement -> preparedStatement.execute() }
            for (userinfo in data.keys) {
                val udata = data[userinfo]
                val statement2 = "INSERT INTO " + tableName + "(" +
                        "userID," +
                        "guildID," +
                        "xp," +
                        "level) VALUES (?, ?, ?, ?)"
                connection.prepareStatement(statement2).use { preparedStatement ->
                    preparedStatement.setLong(1, udata!!.userID)
                    preparedStatement.setLong(2, udata.serverID)
                    preparedStatement.setLong(3, udata.xp)
                    preparedStatement.setInt(4, udata.level)
                    preparedStatement.execute()
                }
            }
        }
    }

    /**
     * Caches all users into HashMap for fast access.
     *
     * @throws SQLException if there's issue with SQL connection
     */
    @Throws(SQLException::class)
    fun cacheUsers() {
        storage.connection.use { connection ->
            val statement = "SELECT * from $tableName;"
            connection!!.prepareStatement(statement).use { preparedStatement ->
                var amount = 0
                val resultSet = preparedStatement.executeQuery()
                while (resultSet.next()) {
                    amount++
                    try {
                        val data = PlayerData(resultSet.getLong("userID"),
                                resultSet.getLong("guildID"),
                                resultSet.getLong("xp"),
                                resultSet.getInt("level"))
                        val info = data.userID.toString() + ":" + data.serverID
                        users[info] = data
                    } catch (ignored: Exception) {
                    }
                }
                sendMessage("Loaded: $amount users from database.")
            }
        }
    }

    /**
     *
     * @param user - User of which data to get.
     * @param guild - Guild from which to get data.
     * @return PlayerData.class
     */
    fun getUser(user: User, guild: Guild): PlayerData? {
        val info = user.id + ":" + guild.id
        return users[info]
    }

    fun updateUser(data: PlayerData) {
        val info = data.userID.toString() + ":" + data.serverID
        users[info] = data
    }

    fun addUser(playerData: PlayerData) {
        val info = playerData.userID.toString() + ":" + playerData.serverID
        users[info] = playerData
    }

    /**
     *
     * @param user User of discord.
     * @param guild Guild in which you want to add xp.
     */

    fun addXp(user: User, guild: Guild) {
        var data = getUser(user, guild)
        if (data == null) {
            data = PlayerData(user.idLong, guild.idLong, 0, 0)
        }
        data.xp = data.xp + calcXpToAdd(guild)
        updateUser(data)
        calcIfLevelUp(user, guild, data)
    }


    /**
     *
     * @param user User of discord.
     * @param guild Guild in which you want to add xp.
     */
    fun removeXp(user: User, guild: Guild) {
        var data = getUser(user, guild)
        if (data == null) {
            data = PlayerData(user.idLong, guild.idLong, 0, 0)
        }
        data.xp = data.xp - calcXpToAdd(guild)
        updateUser(data)
    }


    /**
     * @param level Level the user is currently at.
     */
    private fun getNeededXP(level: Int): Double{
        return ((level-1).toDouble().pow(2.0) * 100).coerceAtLeast(0.0)
    }



    /**
     *
     * @param user User of discord.
     * @param guild Guild in which to calculate
     * @param data PlayerData
     */
    fun calcIfLevelUp(user: User, guild: Guild?, data: PlayerData) {
        var leveledUp = false
        while(data.xp > getNeededXP(data.level)){
            data.level = data.level + 1
            leveledUp = true
        }
        if(leveledUp && guild != null){
            updateUser(data)
            val channel = settings.getTextChannel(guild, "LEVELUP_MESSAGES")
            if (channel != null) {
                if (!blackListed.contains(channel.id)) {
                    sendLevelUpMessage(user, data, channel)
                }
            }
        }
    }

    fun getMultiplier(guild: Guild): Int {
        return if (multipliers.containsKey(guild.id)) {
            multipliers[guild.id]!!.getActiveMultiplier()
        } else {
            1
        }
    }

    fun getMultiplierData(guild: Guild): MultiplierData? {
        return multipliers[guild.id]
    }

    fun setMultiplier(guild: Guild, multiplierData: MultiplierData?) {
        multipliers[guild.id] = multiplierData
    }

    fun sendInfoImage(user: User, data: PlayerData, channel: TextChannel) {
        val thread = Thread(Runnable {
            try {
                val font = "Berlin Sans FB Demi"
                val height = 521
                val width = 1250
                var darkTheme = true
                val playerSettings = playerSettingsHandler.getSettings(user)
                var color1 = Color.orange
                var color2 = Color.red
                if (playerSettings != null) {
                    if (playerSettings.getColor1() != null) {
                        color1 = playerSettings.getColor1()
                    }
                    if (playerSettings.getColor2() != null) {
                        color2 = playerSettings.getColor2()
                    }
                }
                val progressBar = ((data.xp - getNeededXP(data.level-1)) / (getNeededXP(data.level) - getNeededXP(data.level-1)) * 360).roundToInt().coerceAtLeast(0).toDouble()
                val progress = ((data.xp - getNeededXP(data.level-1)) / (getNeededXP(data.level) - getNeededXP(data.level-1)) * 100).roundToInt().coerceAtLeast(0).toDouble()

                var url = URL("http://images.sloempire.eu/Developing/Level-Banner-01.png")
                var background = ImageIO.read(url.openStream())
                background = Resizer.AVERAGE.resize(background, width, height)
                val primary = GradientPaint(
                        0f, 0f, color1, width.toFloat(), 0f, color2)
                background = dye(background, primary)
                url = URL("http://images.sloempire.eu/Developing/level-banner-square-01.png")
                val levelSquare = ImageIO.read(url.openStream())
                background = Resizer.AVERAGE.resize(background, width, height)
                url = URL(user.effectiveAvatarUrl)
                var avatar = ImageIO.read(url.openStream())
                avatar = Resizer.PROGRESSIVE_BILINEAR.resize(avatar!!, 400, 400)
                val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
                val settings = playerSettingsHandler.getSettings(user)
                if (settings != null) {
                    darkTheme = settings.isDarkTheme()
                }
                val rh = RenderingHints(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
                val g2d = bufferedImage.createGraphics()
                g2d.addRenderingHints(rh)
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                // Background
                g2d.color = if (darkTheme) Color.darkGray else Color.white
                g2d.fillRect(0, 0, width, height)

                // Background behind avatar
                g2d.drawImage(background, 0, 0, width, height, 0, 0, background.width, background.height, null)
                g2d.drawImage(levelSquare, 0, 0, width, height, 0, 0, background.width, background.height, null)


                // Drawing avatar
                g2d.drawImage(avatar, 30, 30, 350, 350, 0, 0, 400, 400, null)
                // User name
                g2d.paint = primary
                g2d.font = Font(font, Font.BOLD, 50)
                g2d.drawString(user.name, 30, 400)
                g2d.color = if (darkTheme) Color.white else Color.darkGray
                g2d.font = Font(font, Font.BOLD, 40)
                g2d.drawString("#" + user.discriminator, 30, 450)

                //Level/exp text  + number
                g2d.color = if (darkTheme) Color.white else Color.darkGray
                g2d.font = Font(font, Font.PLAIN, 40)
                g2d.drawString("Level:", 400, 90)
                g2d.drawString("Experience:", 400, 210)
                g2d.font = Font(font, Font.BOLD, 70)
                g2d.drawString(data.level.toString() + "", 400, 160)
                g2d.drawString(data.xp.toString() + "", 400, 280)


                // Progress circle
                g2d.paint = primary
                g2d.fillArc(1006 - 220 / 2, 258 - 220 / 2, 220, 220, 270, progressBar.toInt())
                g2d.color = Color.white
                g2d.fillOval(1006 - 190 / 2, 258 - 190 / 2, 190, 190)


                // Percent number.
                val textSize = 45f
                g2d.font = Font(font, Font.BOLD, textSize.toInt())
                val level = "$progress%"
                var size = g2d.fontMetrics.stringWidth(level)
                g2d.paint = primary
                g2d.drawString(level, (1006.23 - size / 2).toFloat(), 230.32.toFloat() + textSize / 4)
                // xp needed
                g2d.color = Color.gray
                val needed = (getNeededXP(data.level) - data.xp).toString() + "xp"
                size = g2d.fontMetrics.stringWidth(needed)
                g2d.drawString(needed, (1006.23 - size / 2).toFloat(), 264.32.toFloat() + textSize / 4)
                val text = "to go"
                g2d.font = Font(font, Font.PLAIN, textSize.toInt())
                size = g2d.fontMetrics.stringWidth(text)
                g2d.drawString(text, (1006.23 - size / 2).toFloat(), 300.32.toFloat() + textSize / 4)
                g2d.dispose()
                try {
                    val file = File("myimage.png")
                    ImageIO.write(bufferedImage, "png", file)
                    channel.sendFile(file, "level.png").queue()
                } catch (error: IOException) {
                    channel.sendMessage("ERROR!").queue()
                }
            } catch (error: Exception) {
                error.printStackTrace()
            }
        })
        thread.start()
    }

    fun sendLevelUpMessage(user: User, data: PlayerData, channel: TextChannel) {
        val thread = Thread(Runnable {
            try {
                val font = "Berlin Sans FB Demi"
                val height = 521
                val width = 1250
                var darkTheme = true
                val primary = GradientPaint(
                        0f, 0f, Color.ORANGE, width.toFloat(), 0f, Color(0xFF6600))
                val secondary = GradientPaint(
                        0f, 0f, Color(0xFF4800), width.toFloat(), 0f, Color(0xFF6600))
                var url = URL("http://images.sloempire.eu/Developing/LevelUp-Left-01.png")
                var left = ImageIO.read(url.openStream())
                left = Resizer.AVERAGE.resize(left, width, height)
                left = dye(left, primary)
                url = URL("http://images.sloempire.eu/Developing/LevelUp-Right-01.png")
                var right = ImageIO.read(url.openStream())
                right = Resizer.AVERAGE.resize(right, width, height)
                right = dye(right, secondary)
                url = URL("http://images.sloempire.eu/Developing/levelup-overlay-01.png")
                var overlay = ImageIO.read(url.openStream())
                overlay = Resizer.AVERAGE.resize(overlay, width, height)
                url = URL("http://images.sloempire.eu/Developing/levelup-numberoverlay-01.png")
                var overlaynumber = ImageIO.read(url.openStream())
                overlaynumber = Resizer.AVERAGE.resize(overlaynumber, width, height)
                url = URL(user.effectiveAvatarUrl)
                var avatar = ImageIO.read(url.openStream())
                avatar = Resizer.PROGRESSIVE_BILINEAR.resize(avatar, 300, 300)
                avatar = makeRoundedCorner(avatar, avatar.width, true)
                val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
                val settings = playerSettingsHandler.getSettings(user)
                if (settings != null) {
                    darkTheme = settings.isDarkTheme()
                }
                val rh = RenderingHints(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
                val g2d = bufferedImage.createGraphics()
                g2d.addRenderingHints(rh)
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                // Background
                g2d.color = if (darkTheme) Color.darkGray else Color.white
                g2d.fillRect(0, 0, width, height)

                // Background behind avatar
                g2d.drawImage(left, 0, 0, width, height, 0, 0, left.width, left.height, null)
                g2d.drawImage(right, 0, 0, width, height, 0, 0, right.width, right.height, null)


                // Drawing avatar
                //g2d.drawImage(avatar, 140, 110, 400, 400, 0, 0, 400, 400, null);
                g2d.drawImage(avatar, 140, 110, null)
                // Overlay
                g2d.drawImage(overlay, 0, 0, width, height, 0, 0, overlay.width, overlay.height, null)
                g2d.drawImage(overlaynumber, 0, 0, width, height, 0, 0, overlaynumber.width, overlaynumber.height, null)


                //Level text  + number
                g2d.color = Color.WHITE
                g2d.font = Font(font, Font.BOLD, 100)
                g2d.drawString("Level Up!", 690, 120)
                g2d.font = Font(font, Font.BOLD, 340)
                val levelText = data.level.toString() + ""
                val size = g2d.fontMetrics.stringWidth(levelText)
                g2d.drawString(levelText, 899.98f - size / 2, 293.74f + 100f)


                // Disposes of this graphics context and releases any system resources that it is using.
                g2d.dispose()
                try {
                    val file = File("myimage.png")
                    ImageIO.write(bufferedImage, "png", file)
                    channel.sendFile(file, "level.png").queue()
                } catch (error: IOException) {
                    channel.sendMessage("ERROR!").queue()
                }
            } catch (error: Exception) {
                error.printStackTrace()
            }
        })
        thread.start()
    }

    fun calcXpToAdd(guild: Guild): Long {
        return (random.nextInt(1) + 3).toLong() * getMultiplier(guild)
    }

    init {
        blackListed.add("723303528780529677")
        try {
            storage.createTable(tableName,
                    "userID TEXT," +
                            "guildID TEXT," +
                            "xp LONG," +
                            "level INT")
        } catch (error: SQLException) {
            error.printStackTrace()
        }
        try {
            cacheUsers()
        } catch (error: SQLException) {
            error.printStackTrace()
        }
        userUpdater()
    }
}