package com.healthup.data.repositories;

import com.healthup.auth.exceptions.DuplicateEmailException;
import com.healthup.config.MongoDBConfig;
import com.healthup.data.models.Patient;
import com.healthup.data.models.PatientBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class MongoDBPatientRepositoryTest {

    private MongoDBPatientRepository repository;
    private static MongoDBConfig config;

    @AfterEach
    void clearDatabase() {
        try (MongoClient client = (MongoClient) config.getConnection()) {
            // Drop the entire database
            // client.getDatabase(config.getDatabaseName()).drop();

             client.getDatabase(config.getDatabaseName())
                 .getCollection("patients").drop();
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear database", e);
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        config = new MongoDBConfig() {
            @Override
            public Object getConnection() {
                return MongoClients.create("mongodb://localhost:27017");
            }

            @Override
            public String getDatabaseName() {
                return "test_healthup";
            }
        };
        repository = new MongoDBPatientRepository();
        repository.setDatabaseConfig(config);
    }

    @Test
    void save_shouldPersistPatientDocument_PatientRepositoryTest() {
        Patient patient = new PatientBuilder()
                .setFirstName("John")
                .setLastName("Doe")
                .setEmail("john.doe@test.com")
                .setPassword("secure123")
                .build();

        Patient saved = repository.save(patient);

        assertNotNull(saved.getId());
        assertEquals(patient.getId(), saved.getId());
        assertEquals(patient.getFirstName(), saved.getFirstName());
        assertEquals(patient.getLastName(), saved.getLastName());
        assertEquals(patient.getEmail(), saved.getEmail());
        assertEquals(patient.getPassword(), saved.getPassword());
    }

    @Test
    void save_shouldPersistPatientDocument_DocumentIsFoundInDatabase_PatientRepositoryTest() throws Exception {
        Patient patient = new PatientBuilder()
                .setFirstName("John")
                .setLastName("Doe")
                .setEmail("john.doe2@test.com")
                .setPassword("secure123")
                .build();

        Patient saved = repository.save(patient);

        assertNotNull(saved.getId());

        try (MongoClient client = (MongoClient) config.getConnection()) {
            Document doc = client.getDatabase("test_healthup")
                    .getCollection("patients")
                    .find(new Document("_id", new ObjectId(saved.getId())))
                    .first();

            assertNotNull(doc);
            assertEquals("John", doc.getString("firstName"));
            assertEquals("Doe", doc.getString("lastName"));
            assertEquals("john.doe2@test.com", doc.getString("email"));
        }
    }

    @Test
    void saveDuplicateEmail_ShouldNotIncreaseCount() throws Exception {

        Patient patient = new PatientBuilder()
                .setFirstName("John")
                .setLastName("Doe")
                .setEmail("john.doe2@test.com")
                .setPassword("secure123")
                .build();

        Patient saved = repository.save(patient);
        long initialCount = repository.count();

        Patient newPatient = new PatientBuilder()
                .setFirstName("Jane")
                .setLastName("Doe")
                .setEmail("john.doe2@test.com")
                .setPassword("secure123")
                .build();

        assertThrows(DuplicateEmailException.class, () -> repository.save(newPatient));

        long finalCount = repository.count();
        assertEquals(initialCount, finalCount);
    }
}
