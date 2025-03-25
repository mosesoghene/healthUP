package com.healthup.config;

public interface DatabaseConfig {
    public Object getConnection() throws Exception;
    public void closeConnection(Object connection) throws Exception;
}
