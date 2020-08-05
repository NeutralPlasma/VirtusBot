package com.neutralplasma.virtusbot.handlers.playerLeveling;

import com.neutralplasma.virtusbot.handlers.playerSettings.PlayerSettings;
import com.neutralplasma.virtusbot.handlers.playerSettings.PlayerSettingsHandler;
import com.neutralplasma.virtusbot.storage.dataStorage.StorageHandler;
import com.neutralplasma.virtusbot.utils.GraphicUtil;
import com.neutralplasma.virtusbot.utils.Resizer;
import com.neutralplasma.virtusbot.utils.TextUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PlayerLeveling {

    private final Random random = new Random();
    private StorageHandler storage;
    private PlayerSettingsHandler playerSettingsHandler;
    private final ArrayList<String> blackListed = new ArrayList<>();
    private final String tableName = "LevelingData";

    private int ACTIVE_MULTIPLIER = 1;

    public HashMap<String, PlayerData> users = new HashMap<>();


    public PlayerLeveling(StorageHandler storage, PlayerSettingsHandler playerSettingsHandler){
        this.storage = storage;
        this.playerSettingsHandler = playerSettingsHandler;
        blackListed.add("723303528780529677");

        try {
            storage.createTable(tableName,
                    "userID TEXT," +
                    "guildID TEXT," +
                    "xp LONG," +
                    "level INT");
        }catch (SQLException error){
            error.printStackTrace();
        }

        try {
            cacheUsers();
        }catch (SQLException error){
            error.printStackTrace();
        }

        userUpdater();
    }


    Runnable updatingTask = () -> {
        try {
            syncUsers();
        }catch (Exception ignored){}
    };

    /**
     * Runs Sync function every 1 minute.
     */
    public void userUpdater(){
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                updatingTask.run();
            }

        }, 100, 60000);
    }

    /**
     * Syncs HashMap of users to SQL.
     *
     * @throws SQLException if there is issue with SQL connection.
     */

    public void syncUsers() throws SQLException{
        HashMap<String, PlayerData> data = new HashMap<>(users);

        try(Connection connection = storage.getConnection()){
            String statement = "DELETE FROM " + tableName + ";";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                preparedStatement.execute();
            }
            for(String userinfo : data.keySet()){
                PlayerData udata = data.get(userinfo);
                String statement2 = "INSERT INTO " + tableName + "(" +
                        "userID," +
                        "guildID," +
                        "xp," +
                        "level) VALUES (?, ?, ?, ?)";
                try(PreparedStatement preparedStatement = connection.prepareStatement(statement2)){
                    preparedStatement.setLong(1, udata.getUserID());
                    preparedStatement.setLong(2, udata.getServerID());
                    preparedStatement.setLong(3, udata.getXp());
                    preparedStatement.setInt(4, udata.getLevel());
                    preparedStatement.execute();
                }
            }
        }
    }

    /**
     * Caches all users into HashMap for fast access.
     *
     * @throws SQLException if there's issue with SQL connection
     */

    public void cacheUsers() throws SQLException{
        try(Connection connection = storage.getConnection()){
            String statement = "SELECT * from " + tableName + ";";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                int amount = 0;
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    amount++;
                    try {
                        PlayerData data = new PlayerData(resultSet.getLong("userID"),
                                resultSet.getLong("guildID"),
                                resultSet.getLong("xp"),
                                resultSet.getInt("level"));
                        String info = data.getUserID() + ":" + data.getServerID();
                        users.put(info, data);
                    }catch (Exception ignored) {}
                }
                TextUtil.sendMessage("Loaded: " + amount + " users from database.");
            }
        }
    }

    /**
     *
     * @param user - User of which data to get.
     * @param guild - Guild from which to get data.
     * @return PlayerData.class
     */

    public PlayerData getUser(User user, Guild guild){
        String info = user.getId() + ":" + guild.getId();
        return users.get(info);
    }
    public void updateUser(PlayerData playerData){
        String info = playerData.getUserID() + ":" + playerData.getServerID();
        users.put(info, playerData);
    }
    public void addUser(PlayerData playerData){
        String info = playerData.getUserID() + ":" + playerData.getServerID();
        users.put(info, playerData);
    }

    /**
     *
     * @param user User of discord.
     * @param guild Guild in which you want to add xp.
     */
    @Deprecated
    public void addXp(User user, Guild guild){
        PlayerData data = getUser(user, guild);
        if(data == null){
            data = new PlayerData(user.getIdLong(), guild.getIdLong(), 0, 0);

        }
        data.setXp(data.getXp() + calcXpToAdd());
        updateUser(data);
        calcIfLevelUp(user, guild, null);
    }

    /**
     *
     * @param user User of discord.
     * @param guild Guild in which you want to add xp.
     * @param channel Text channel in which to send levelUP message
     */
    public void addXp(User user, Guild guild, TextChannel channel){
        PlayerData data = getUser(user, guild);
        if(data == null){
            data = new PlayerData(user.getIdLong(), guild.getIdLong(), 0, 0);

        }
        data.setXp(data.getXp() + calcXpToAdd());
        updateUser(data);
        calcIfLevelUp(user, guild, channel);
    }

    /**
     *
     * @param user User of discord.
     * @param guild Guild in which you want to add xp.
     */
    public void removeXp(User user, Guild guild){
        PlayerData data = getUser(user, guild);
        if(data == null){
            data = new PlayerData(user.getIdLong(), guild.getIdLong(), 0, 0);

        }
        data.setXp(data.getXp() - calcXpToAdd());
        updateUser(data);
    }

    /**
     *
     * @param data PlayerLeveling data.
     * @return returns needed xp for levelup.
     */
    public double getNeededXP(PlayerData data){
        int currentLevel = data.getLevel();
        double needed = 100 * Math.pow(2,(currentLevel-2));
        return needed;
    }

    /**
     *
     * @param data PlayerLeveling data.
     * @return Returns xp needed for 1 level below users level.
     */
    public double previous(PlayerData data){
        int prevLevel = data.getLevel() - 1;
        double needed = 100 * Math.pow(2,(prevLevel-2));
        return needed;
    }

    /**
     *
     * @param user User of discord.
     * @param guild Guild in which to calculate
     * @param channel Channel to which to send LevelUP message
     */
    public void calcIfLevelUp(User user, Guild guild, TextChannel channel){
        PlayerData data = getUser(user, guild);
        int currentLevel = data.getLevel();
        if(data.getXp() > (100 * Math.pow(2,(currentLevel-2)))){
            data.setLevel(data.getLevel() + 1);
            updateUser(data);
            // TODO: UPDATE LEVELING MESSAGE.
            if(channel != null){
                if(!blackListed.contains(channel.getId())) {
                    sendLevelUpMessage(user, data, channel);
                    //channel.sendMessage("Leveled up to: " + (currentLevel + 1)).queue();
                }
            }

        }
    }

    public int getACTIVE_MULTIPLIER() {
        return ACTIVE_MULTIPLIER;
    }
    public void setACTIVE_MULTIPLIER(int ACTIVE_MULTIPLIER) {
        this.ACTIVE_MULTIPLIER = ACTIVE_MULTIPLIER;
    }

    public void sendInfoImage(User user, PlayerData data, TextChannel channel){
        Thread thread = new Thread(() -> {
            try {
                String font = "Berlin Sans FB Demi";
                int height = 521;
                int width = 1250;
                boolean darkTheme = true;
                PlayerSettings playerSettings = playerSettingsHandler.getSettings(user);
                Color color1 = Color.orange;
                Color color2 = Color.red;
                if(playerSettings != null){
                    if(playerSettings.getColor1() != null){
                        color1 = playerSettings.getColor1();
                    }
                    if(playerSettings.getColor2() != null){
                        color2 = playerSettings.getColor2();
                    }
                }
                double progressBar = Math.max(Math.round(((data.getXp() - this.previous(data)) / (this.getNeededXP(data) - this.previous(data))) * 360), 0);
                double progress = Math.max(Math.round(((data.getXp() - this.previous(data)) / (this.getNeededXP(data) - this.previous(data))) * 100), 0);

                URL url = new URL("http://images.sloempire.eu/Developing/Level-Banner-01.png");
                BufferedImage background = ImageIO.read(url.openStream());
                background = Resizer.AVERAGE.resize(background, width, height);

                GradientPaint primary = new GradientPaint(
                        0f, 0f, color1, width, 0f, color2);

                background = GraphicUtil.dye(background, primary);

                url = new URL("http://images.sloempire.eu/Developing/level-banner-square-01.png");
                BufferedImage levelSquare = ImageIO.read(url.openStream());
                background = Resizer.AVERAGE.resize(background, width, height);


                url = new URL(user.getEffectiveAvatarUrl());
                BufferedImage avatar = ImageIO.read(url.openStream());
                avatar = Resizer.PROGRESSIVE_BILINEAR.resize(avatar, 400, 400);

                BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

                PlayerSettings settings = playerSettingsHandler.getSettings(user);
                if (settings != null) {
                    darkTheme = settings.isDarkTheme();
                }


                RenderingHints rh = new RenderingHints(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


                Graphics2D g2d = bufferedImage.createGraphics();
                g2d.addRenderingHints(rh);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Background
                g2d.setColor(darkTheme ? Color.darkGray : Color.white);
                g2d.fillRect(0, 0, width, height);

                // Background behind avatar
                g2d.drawImage(background, 0, 0, width, height, 0, 0, background.getWidth(), background.getHeight(), null);
                g2d.drawImage(levelSquare, 0, 0, width, height, 0, 0, background.getWidth(), background.getHeight(), null);


                // Drawing avatar
                g2d.drawImage(avatar, 30, 30, 350, 350, 0, 0, 400, 400, null);
                // User name
                g2d.setPaint(primary);
                g2d.setFont(new Font(font, Font.BOLD, 50));
                g2d.drawString(user.getName(), 30, 400);
                g2d.setColor(darkTheme ? Color.white : Color.darkGray);
                g2d.setFont(new Font(font, Font.BOLD, 40));
                g2d.drawString("#" + user.getDiscriminator(), 30, 450);

                //Level/exp text  + number
                g2d.setColor(darkTheme ? Color.white : Color.darkGray);
                g2d.setFont(new Font(font, Font.PLAIN, 40));
                g2d.drawString("Level:", 400, 90);
                g2d.drawString("Experience:", 400, 210);

                g2d.setFont(new Font(font, Font.BOLD, 70));
                g2d.drawString(data.getLevel() + "", 400, 160);
                g2d.drawString(data.getXp() + "", 400, 280);


                // Progress circle
                g2d.setPaint(primary);
                g2d.fillArc(1006 - 220 / 2, 258 - 220 / 2, 220, 220, 270, (int) progressBar);
                g2d.setColor(Color.white);
                g2d.fillOval(1006 - 190 / 2, 258 - 190 / 2, 190, 190);




                // Percent number.
                float textSize = 45;
                g2d.setFont(new Font(font, Font.BOLD, (int) textSize));
                String level = progress + "%";
                int size = g2d.getFontMetrics().stringWidth(level);
                g2d.setPaint(primary);
                g2d.drawString(level, (float) (1006.23 - (size / 2)), (float) 230.32 + (textSize / 4));
                // xp needed
                g2d.setColor(Color.gray);
                String needed = (this.getNeededXP(data) - data.getXp()) + "xp";
                size = g2d.getFontMetrics().stringWidth(needed);
                g2d.drawString(needed, (float) (1006.23 - (size / 2)), (float) 264.32 + (textSize / 4));

                String text = "to go";
                g2d.setFont(new Font(font, Font.PLAIN, (int) textSize));
                size = g2d.getFontMetrics().stringWidth(text);
                g2d.drawString(text, (float) (1006.23 - (size / 2)), (float) 300.32 + (textSize / 4));

                g2d.dispose();
                try {
                    File file = new File("myimage.png");
                    ImageIO.write(bufferedImage, "png", file);
                    channel.sendFile(file, "level.png").queue();
                } catch (IOException error) {
                    channel.sendMessage("ERROR!").queue();
                }
            } catch (Exception error) {
                error.printStackTrace();
            }
        });
        thread.start();
    }


    public void sendLevelUpMessage(User user, PlayerData data, TextChannel channel){
        Thread thread = new Thread(() -> {
            try {
                String font = "Berlin Sans FB Demi";
                int height = 521;
                int width = 1250;
                boolean darkTheme = true;

                GradientPaint primary = new GradientPaint(
                        0f, 0f, Color.ORANGE, width, 0f, new Color(0xFF6600));
                GradientPaint secondary = new GradientPaint(
                        0f, 0f, new Color(0xFF4800), width, 0f, new Color(0xFF6600));

                URL url = new URL("http://images.sloempire.eu/Developing/LevelUp-Left-01.png");
                BufferedImage left = ImageIO.read(url.openStream());
                left = Resizer.AVERAGE.resize(left, width, height);
                left = GraphicUtil.dye(left, primary);

                url = new URL("http://images.sloempire.eu/Developing/LevelUp-Right-01.png");
                BufferedImage right = ImageIO.read(url.openStream());
                right = Resizer.AVERAGE.resize(right, width, height);
                right = GraphicUtil.dye(right, secondary);

                url = new URL("http://images.sloempire.eu/Developing/levelup-overlay-01.png");
                BufferedImage overlay = ImageIO.read(url.openStream());
                overlay = Resizer.AVERAGE.resize(overlay, width, height);

                url = new URL("http://images.sloempire.eu/Developing/levelup-numberoverlay-01.png");
                BufferedImage overlaynumber = ImageIO.read(url.openStream());
                overlaynumber = Resizer.AVERAGE.resize(overlaynumber, width, height);






                url = new URL(user.getEffectiveAvatarUrl());
                BufferedImage avatar = ImageIO.read(url.openStream());
                avatar = Resizer.PROGRESSIVE_BILINEAR.resize(avatar, 300, 300);
                avatar = TextUtil.makeRoundedCorner(avatar, avatar.getWidth(), true);

                BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                PlayerSettings settings = playerSettingsHandler.getSettings(user);
                if(settings != null){
                    darkTheme = settings.isDarkTheme();
                }
                RenderingHints rh = new RenderingHints(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                Graphics2D g2d = bufferedImage.createGraphics();

                g2d.addRenderingHints(rh);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Background
                g2d.setColor(darkTheme ? Color.darkGray : Color.white);
                g2d.fillRect(0, 0, width, height);

                // Background behind avatar
                g2d.drawImage(left, 0, 0, width, height, 0, 0, left.getWidth(), left.getHeight(), null);
                g2d.drawImage(right, 0, 0, width, height, 0, 0, right.getWidth(), right.getHeight(), null);


                // Drawing avatar
                //g2d.drawImage(avatar, 140, 110, 400, 400, 0, 0, 400, 400, null);
                g2d.drawImage(avatar, 140, 110, null);
                // Overlay
                g2d.drawImage(overlay, 0, 0, width, height, 0, 0, overlay.getWidth(), overlay.getHeight(), null);
                g2d.drawImage(overlaynumber, 0, 0, width, height, 0, 0, overlaynumber.getWidth(), overlaynumber.getHeight(), null);



                //Level text  + number
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font(font, Font.BOLD, 100));
                g2d.drawString("Level Up!", 690, 120);
                g2d.setFont(new Font(font, Font.BOLD, 340));
                String levelText = data.getLevel() + "";
                int size = g2d.getFontMetrics().stringWidth(levelText);
                g2d.drawString(levelText, 899.98f - size/2, 293.74f + 100f);




                // Disposes of this graphics context and releases any system resources that it is using.
                g2d.dispose();
                try {
                    File file = new File("myimage.png");
                    ImageIO.write(bufferedImage, "png", file);
                    channel.sendFile(file, "level.png").queue();
                } catch (IOException error) {
                    channel.sendMessage("ERROR!").queue();
                }
            }catch (Exception error){
                error.printStackTrace();
            }
        });
        thread.start();
    }

    public long calcXpToAdd(){
        return (long) (random.nextInt(1) + 3) * ACTIVE_MULTIPLIER;
    }
}
