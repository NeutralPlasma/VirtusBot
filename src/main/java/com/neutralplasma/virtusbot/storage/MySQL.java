package com.neutralplasma.virtusbot.storage;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL {

    private HikariDataSource hikari;

    public void openConnection(){
        String address = "";
        String username = "";
        String database = "";
        String userPassword = "";
        long timeout = 1000L; // timeout
        int poolsize = 10; // pool size max

        String[] adresses = address.split(":");
        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.setMaximumPoolSize(poolsize);
        hikari.setConnectionTimeout(timeout);
        hikari.addDataSourceProperty("serverName", adresses[0]);
        hikari.addDataSourceProperty("port", adresses[1]);
        hikari.addDataSourceProperty("databaseName", database);
        hikari.addDataSourceProperty("user", username);
        hikari.addDataSourceProperty("password", userPassword);
        hikari.addDataSourceProperty("useSSL", false);

    }

    public void setup(){
        this.openConnection();
    }

    public int getInt(String steamID, String column) throws SQLException {
        try(Connection connection = hikari.getConnection()) {
            String statement = "SELECT " + column + " FROM users WHERE identifier = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                preparedStatement.setString(1, steamID);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    return resultSet.getInt(column);
                }
            }

        }
        return 0;
    }

    public int getIntByName(String name, String column) throws SQLException {
        try(Connection connection = hikari.getConnection()) {
            String statement = "SELECT " + column + " FROM users WHERE name = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                preparedStatement.setString(1, name);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    return resultSet.getInt(column);
                }
            }

        }
        return 0;
    }

    public String getString(String steamID, String column) throws SQLException {
        try(Connection connection = hikari.getConnection()) {
            String statement = "SELECT " + column + " FROM users WHERE identifier = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                preparedStatement.setString(1, steamID);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    return resultSet.getString(column);
                }
            }

        }
        return "NULL";
    }

    public String getStringByName(String name, String column) throws SQLException {
        try(Connection connection = hikari.getConnection()) {
            String statement = "SELECT " + column + " FROM users WHERE name = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                preparedStatement.setString(1, name);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    return resultSet.getString(column);
                }
            }

        }
        return "NULL";
    }


    public PlayerStorage getPlayer(String name) throws SQLException {
        try(Connection connection = hikari.getConnection()) {
            String statement = "SELECT * FROM users WHERE name = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                preparedStatement.setString(1, name);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    String names = resultSet.getString("name");
                    String identifier = resultSet.getString("identifier");
                    String license = resultSet.getString("license");
                    String firstname = resultSet.getString("firstname");
                    String lastname = resultSet.getString("lastname");
                    String phonenumber = resultSet.getString("phone_number");
                    int is_dead = resultSet.getInt("is_dead");
                    String group = resultSet.getString("group");
                    int permission_level = resultSet.getInt("permission_level");
                    int bank = resultSet.getInt("bank");
                    int money = resultSet.getInt("money");
                    String loadout = resultSet.getString("loadout");
                    String job = resultSet.getString("job");
                    int job_grade = resultSet.getInt("job_grade");

                    PlayerStorage player = new PlayerStorage(names, identifier, license, firstname, lastname, phonenumber, is_dead, group, permission_level, bank, money, loadout, job, job_grade);
                    return player;
                }
            }

        }
        return null;
    }


    public PlayerStorage getPlayerDefault(String discordid) throws SQLException {
        try(Connection connection = hikari.getConnection()) {
            String statement = "SELECT identifier FROM users WHERE discord_id = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                preparedStatement.setString(1, discordid);
                ResultSet resultSet = preparedStatement.executeQuery();
                String name = "";
                while(resultSet.next()) {
                    name = resultSet.getString("identifier");
                }

                String statement2 = "SELECT * FROM users WHERE identifier = ?";
                try(PreparedStatement preparedStatement2 = connection.prepareStatement(statement2)){
                    preparedStatement2.setString(1, name);
                    ResultSet resultSet2 = preparedStatement.executeQuery();
                    while(resultSet2.next()){
                        String names = resultSet2.getString("name");
                        String identifier = resultSet2.getString("identifier");
                        String license = resultSet2.getString("license");
                        String firstname = resultSet2.getString("firstname");
                        String lastname = resultSet2.getString("lastname");
                        String phonenumber = resultSet2.getString("phone_number");
                        int is_dead = resultSet2.getInt("is_dead");
                        String group = resultSet2.getString("group");
                        int permission_level = resultSet2.getInt("permission_level");
                        int bank = resultSet2.getInt("bank");
                        int money = resultSet2.getInt("money");
                        String loadout = resultSet2.getString("loadout");
                        String job = resultSet2.getString("job");
                        int job_grade = resultSet2.getInt("job_grade");

                        PlayerStorage player = new PlayerStorage(names, identifier, license, firstname, lastname, phonenumber, is_dead, group, permission_level, bank, money, loadout, job, job_grade);
                        return player;
                    }
                }
            }
        }
        return null;
    }

    public PlayerStorage getPlayerByID(String id) throws SQLException {
        try(Connection connection = hikari.getConnection()) {
            String statement = "SELECT * FROM users WHERE identifier = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)){
                preparedStatement.setString(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    String names = resultSet.getString("name");
                    String identifier = resultSet.getString("identifier");
                    String license = resultSet.getString("license");
                    String firstname = resultSet.getString("firstname");
                    String lastname = resultSet.getString("lastname");
                    String phonenumber = resultSet.getString("phone_number");
                    int is_dead = resultSet.getInt("is_dead");
                    String group = resultSet.getString("group");
                    int permission_level = resultSet.getInt("permission_level");
                    int bank = resultSet.getInt("bank");
                    int money = resultSet.getInt("money");
                    String loadout = resultSet.getString("loadout");
                    String job = resultSet.getString("job");
                    int jobgrade = resultSet.getInt("job_grade");

                    PlayerStorage player = new PlayerStorage(names, identifier, license, firstname, lastname, phonenumber, is_dead, group, permission_level, bank, money, loadout, job, jobgrade);
                    return player;
                }
            }

        }
        return null;
    }

    public void closeConnection() {
        hikari.close();
    }

}