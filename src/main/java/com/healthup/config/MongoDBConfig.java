package com.healthup.config;

import com.mongodb.client.MongoClients;

public class MongoDBConfig implements DatabaseConfig {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "health_management";

    @Override
    public Object getConnection() throws Exception {
        return MongoClients.create(CONNECTION_STRING);
    }

    @Override
    public void closeConnection(Object connection) throws Exception {

    }
}
