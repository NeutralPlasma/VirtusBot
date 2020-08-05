package com.neutralplasma.virtusbot.storage.dataStorage;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.neutralplasma.virtusbot.utils.FileUtil.getPath;

public class SQL extends Storage {
    private HikariDataSource hikari;
    private Gson gson = new Gson();


    /**
     * Open SQL connection to file.
     */
    @Override
    public void openConnection() {

        String database = "DataBase";
        File file = new File(getPath() + database + ".db");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception error) {

        }
        HikariConfig config = new HikariConfig();
        config.setPoolName("Storage");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:" + getPath() + "/" + database + ".db");
        config.setConnectionTestQuery("SELECT 1");
        config.setMaxLifetime(60000); // 60 Sec
        config.setMaximumPoolSize(10); // 50 Connections (including idle connections)

        hikari = new HikariDataSource(config);
    }

    /**
     * Close SQL connection.
     */
    @Override
    public void closeConnection() {
        hikari.close();
    }

    /**
     * Create table in database.
     *
     * @param tableName table name.
     * @param format    Table format.
     */
    @Override
    public void createTable(String tableName, String format) throws SQLException {
        try (Connection connection = hikari.getConnection()) {
            String statement = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + format + ");";
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.execute();
            }
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return hikari.getConnection();

    }
}

