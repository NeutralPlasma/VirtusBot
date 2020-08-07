package com.neutralplasma.virtusbot.storage.dataStorage;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Storage {

    public void openConnection(){

    }
    public void closeConnection(){

    }
    public void createTable(String tableName, String format) throws SQLException {

    }

    public Connection getConnection() throws SQLException{
        return null;
    }

}
