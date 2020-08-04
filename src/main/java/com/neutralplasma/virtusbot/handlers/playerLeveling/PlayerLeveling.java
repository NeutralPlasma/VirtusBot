package com.neutralplasma.virtusbot.handlers.playerLeveling;

import com.neutralplasma.virtusbot.handlers.playerSettings.PlayerSettings;
import com.neutralplasma.virtusbot.handlers.playerSettings.PlayerSettingsHandler;
import com.neutralplasma.virtusbot.storage.dataStorage.SQL;
import com.neutralplasma.virtusbot.storage.dataStorage.Storage;
import com.neutralplasma.virtusbot.storage.dataStorage.StorageHandler;
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
    private final String defaultURL = "https://htmlcolorcodes.com/assets/images/html-color-codes-color-tutorials-hero-00e10b1f.jpg";
    private final ArrayList<String> blackListed = new ArrayList<>();
    private final String tableName = "LevelingData";

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
     * @throws SQLException if theres issue with SQL connection
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

    public void sendInfoImage(User user, PlayerData data, TextChannel channel){
        Thread thread = new Thread(() -> {
            try {
                int height = 150;
                int width = 600;
                URL url = new URL(user.getEffectiveAvatarUrl());
                BufferedImage avatar = ImageIO.read(url.openStream());
                avatar = TextUtil.makeRoundedCorner(avatar, avatar.getHeight(), true);
                avatar = Resizer.AVERAGE.resize(avatar, 140, 140);
                boolean darkTheme = false;

                PlayerSettings settings = playerSettingsHandler.getSettings(user);
                if(settings != null){
                    if(settings.getAvatarBackgroundImage() != null){
                        url = new URL(settings.getAvatarBackgroundImage());
                    }else{
                        url = new URL(defaultURL);
                    }
                    darkTheme = settings.isDarkTheme();
                }else{
                    url = new URL(defaultURL);
                }


                BufferedImage avatarBackground = ImageIO.read(url.openStream());
                avatarBackground = Resizer.AVERAGE.resize(avatarBackground, height, height);
                avatarBackground = TextUtil.makeRoundedCorner(avatarBackground, 15, true);

                BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);


                RenderingHints rh = new RenderingHints(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Create a graphics which can be used to draw into the buffered image
                Graphics2D g2d = bufferedImage.createGraphics();
                g2d.addRenderingHints(rh);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Background
                g2d.setColor(darkTheme ? Color.darkGray : Color.white);
                g2d.fillRect(0, 0, width, height);

                // Background behind avatar
                g2d.drawImage(avatarBackground, 6, 6, height-10, height-10, 0, 0, avatarBackground.getWidth(), avatarBackground.getHeight(), null);

                // drawing avatar
                g2d.drawImage(avatar, 11, 11, height-20, height-20, 0, 0, 140, 140, null);


                // Level text
                g2d.setFont(new Font("Roboto", Font.BOLD, 32));
                //g2d.setColor(Color.lightGray);
                g2d.setColor(darkTheme ? Color.lightGray : Color.darkGray);
                int size = g2d.getFontMetrics().stringWidth("Level");
                g2d.drawString("Level", 210 - (size / 2), 60);

                // Level number
                g2d.setFont(new Font("Roboto", Font.BOLD, 42));
                String level = String.valueOf(data.getLevel());
                size = g2d.getFontMetrics().stringWidth(level);
                g2d.drawString(level, 210 - (size / 2), 110);


                // Experience text
                g2d.setFont(new Font("Roboto", Font.BOLD, 24));
                //g2d.setColor(Color.lightGray);
                g2d.setColor(darkTheme ? Color.lightGray : Color.darkGray);
                size = g2d.getFontMetrics().stringWidth("EXPERIENCE");
                g2d.drawString("EXPERIENCE", 570 - size, 60);

                // Experience number
                g2d.setFont(new Font("Roboto", Font.BOLD, 24));
                //g2d.setColor(Color.lightGray);
                g2d.setColor(darkTheme ? Color.lightGray : Color.darkGray);
                String exp = String.valueOf(data.getXp());
                size = g2d.getFontMetrics().stringWidth(exp);
                g2d.drawString(exp, 570 - size, 110);

                // Experience bar background
                //g2d.setColor(Color.lightGray);
                g2d.setColor(darkTheme ? Color.lightGray : Color.darkGray);
                g2d.fillRoundRect(270, 75, 300, 7, 6, 6);

                // Experience bar
                double progressBar = Math.max(Math.round(((data.getXp() - this.previous(data)) / (this.getNeededXP(data) - this.previous(data))) * 300), 6);
                g2d.setColor(new Color(255, 115, 0));
                g2d.fillRoundRect(272, 76, (int) progressBar, 5, 4, 4);


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


    public void sendLevelUpMessage(User user, PlayerData data, TextChannel channel){
        Thread thread = new Thread(() -> {
            try {
                String font = "Berlin Sans FB Demi Bold";
                int height = 250;
                int width = 600;
                boolean darkTheme = true;

                int spread = 8;
                int blurIntensity = 8;
                int blur = 30-blurIntensity;

                URL url = new URL("http://images.sloempire.eu/Developing/LevelUP-BANNER-01.png");
                BufferedImage background = ImageIO.read(url.openStream());
                background = Resizer.AVERAGE.resize(background, width, height);

                url = new URL(user.getEffectiveAvatarUrl());
                BufferedImage avatar = ImageIO.read(url.openStream());
                avatar = TextUtil.makeRoundedCorner(avatar, avatar.getHeight(), true);
                avatar = Resizer.PROGRESSIVE_BILINEAR.resize(avatar, 170, 170);

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
                g2d.drawImage(background, 0, 0, width, height, 0, 0, background.getWidth(), background.getHeight(), null);


                // Drawing avatar
                g2d.drawImage(avatar, 60, 60, 230, 230, 0, 0, 200, 200, null);






                // Level number
                g2d.setFont(new Font(font, Font.BOLD, 80));
                String level = String.valueOf(data.getLevel());
                int size = g2d.getFontMetrics().stringWidth(level);

                // shadow TODO: ADD SHADOW??

                g2d.setColor(new Color(0, 0, 0, 0.3f));
                g2d.drawString(level, 488 - (size / 2), 165);



                g2d.setColor(new Color(179, 179, 179));
                g2d.drawString(level, 485 - (size / 2), 160);





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
        return random.nextInt(1) + 3;
    }
}
