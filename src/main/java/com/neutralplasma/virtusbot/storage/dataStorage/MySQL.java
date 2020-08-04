package com.neutralplasma.virtusbot.storage.dataStorage;

import com.neutralplasma.virtusbot.storage.Info;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL extends Storage{

    private HikariDataSource hikari;

    public MySQL(){
        openConnection();
    }

    @Override
    public void openConnection(){
        long timeout = 1000L; // timeout
        int poolsize = 10; // pool size max

        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
        hikari.setMaximumPoolSize(poolsize);
        hikari.setConnectionTimeout(timeout);
        hikari.addDataSourceProperty("serverName", Info.DATABASE_IP);
        hikari.addDataSourceProperty("port", Info.DATABASE_PORT);
        hikari.addDataSourceProperty("databaseName", Info.DATABASE_NAME);
        hikari.addDataSourceProperty("user", Info.DATABASE_USER);
        hikari.addDataSourceProperty("password", Info.DATABASE_PASSWORD);
        hikari.addDataSourceProperty("useSSL", Info.USE_SSL);

    }
    @Override
    public void closeConnection() {
        hikari.close();
    }


    /**
     * Create table in database.
     *
     * @param tableName table name.
     * @param format Table format.
     */
    @Override
    public void createTable(String tableName, String format) throws SQLException{
        try(Connection connection = hikari.getConnection()){
            String statement = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + format + ");";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                preparedStatement.execute();
            }
        }
    }
    @Override
    public Connection getConnection() throws SQLException{
        return hikari.getConnection();

    }

}