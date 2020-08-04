package com.neutralplasma.virtusbot.storage.dataStorage;

import com.neutralplasma.virtusbot.storage.Info;

import java.sql.Connection;
import java.sql.SQLException;

public class StorageHandler {
    Storage storage;

    public StorageHandler(){
        if(Info.USE_MYSQL){
            storage = new MySQL();
        }else{
            storage = new SQL();
        }
        storage.openConnection();
    }

    public Connection getConnection() throws SQLException {
        return storage.getConnection();
    }

    public void createTable(String tableName, String format) throws SQLException {
        storage.createTable(tableName, format);
    }

    public void closeConnection(){
        storage.closeConnection();
    }


}
