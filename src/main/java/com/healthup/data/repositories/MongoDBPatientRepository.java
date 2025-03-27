package com.healthup.data.repositories;

import com.healthup.auth.exceptions.DataAccessException;
import com.healthup.auth.exceptions.DuplicateEmailException;
import com.healthup.config.MongoDBConfig;
import com.healthup.data.models.Patient;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;

import java.util.List;
import java.util.Optional;

public class MongoDBPatientRepository implements PatientRepository<MongoDBConfig> {
    private MongoDBConfig databaseConfig;


    @Override
    public void setDatabaseConfig(MongoDBConfig config) throws Exception {
        this.databaseConfig = config;
        createEmailIndex();
    }

    @Override
    public long count() throws Exception {
        try (MongoClient client = (MongoClient) databaseConfig.getConnection()) {
            MongoDatabase database = client.getDatabase(databaseConfig.getDatabaseName());
            MongoCollection<Document> collection = database.getCollection("patients");
            return collection.countDocuments();
        } catch (Exception e) {
            throw new DataAccessException("The following error occurred: " + e.getMessage());
        }
    }

    @Override
    public Patient save(Patient patient) {
        try (MongoClient client = (MongoClient) databaseConfig.getConnection()) {
            MongoDatabase database = client.getDatabase(databaseConfig.getDatabaseName());
            MongoCollection<Document> collection = database.getCollection("patients");

            Document doc = patient.toDocument();
            insertInto(collection, doc);
            return patient;
        } catch (MongoWriteException exception) {
            if (userEmailExist(exception)) {
                throw new DuplicateEmailException("Email already exists: " + patient.getEmail());
            }
            throw new DataAccessException("The following error occurred: " + exception.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleSavingException(Patient patient, MongoWriteException exception) throws DuplicateEmailException {
        if (userEmailExist(exception)) {
            throw new DuplicateEmailException("Email already exists: " + patient.getEmail());
        }
        throw new DataAccessException("Error saving patient", exception);
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

    private void createEmailIndex() throws Exception {
        try (MongoClient client = (MongoClient) databaseConfig.getConnection()) {
            MongoCollection<Document> collection = client
                    .getDatabase(databaseConfig.getDatabaseName())
                    .getCollection("patients");

            collection.createIndex(
                    new Document("email", 1),
                    new IndexOptions().unique(true)
            );
        }
    }

    private static boolean userEmailExist(MongoWriteException e) {
        return e.getError().getCode() == 11000;
    }
}
