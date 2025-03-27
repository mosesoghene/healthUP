package com.healthup.data.repositories;

import com.healthup.data.models.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientRepository<T> {
    Patient save(Patient patient);
    Optional<Patient> findById(long id);
    Optional<Patient> findByEmail(String email);
    List<Patient> findAll();
    void delete(long id);
    Patient update(Patient patient);
    void setDatabaseConfig(T config) throws Exception;
    long count() throws Exception;
}
