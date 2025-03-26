package com.healthup.data.models;

import org.bson.types.ObjectId;

public class PatientBuilder {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public PatientBuilder() {
        this.id = setId();
    }

    private String setId() {
        return ObjectId.get().toHexString();
    }

    public PatientBuilder setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public PatientBuilder setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public PatientBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public PatientBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public Patient build() {
        return new Patient(id, firstName, lastName, email, password);
    }
}
