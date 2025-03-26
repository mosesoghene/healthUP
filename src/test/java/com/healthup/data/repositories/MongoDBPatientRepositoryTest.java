package com.healthup.data.repositories;

import com.healthup.config.MongoDBConfig;
import com.healthup.data.models.Patient;
import com.healthup.data.models.PatientBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class MongoDBPatientRepositoryTest {

    private MongoDBPatientRepository repository;
    private MongoDBConfig config;

    @BeforeEach
    void setUp() {
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
                .setEmail("john.doe@test.com")
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
            assertEquals("john.doe@test.com", doc.getString("email"));
        }
    }
}
