package com.healthup.data.repositories;

import com.healthup.config.MongoDBConfig;
import com.healthup.data.models.Patient;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;
import java.util.Optional;

public class MongoDBPatientRepository implements PatientRepository<MongoDBConfig> {
    private MongoDBConfig databaseConfig;

    @Override
    public void setDatabaseConfig(MongoDBConfig config) {
        this.databaseConfig = config;
    }

    @Override
    public Patient save(Patient patient) {
        try (MongoClient client = (MongoClient) databaseConfig.getConnection()) {
            MongoDatabase database = client.getDatabase(databaseConfig.getDatabaseName());
            MongoCollection<Document> collection = database.getCollection("patients");

            Document doc = patient.toDocument();
            insertInto(collection, doc);
            return patient;
        } catch (Exception e) {
            throw new RuntimeException("Error saving patient to MongoDB", e);
        }
    }

    private void insertInto(MongoCollection<Document> collection, Document doc) {
        collection.insertOne(doc);
    }

    @Override
    public Optional<Patient> findById(long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Patient> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public List<Patient> findAll() {
        return List.of();
    }

    @Override
    public void delete(long id) {

    }

    @Override
    public Patient update(Patient patient) {
        return null;
    }

}
