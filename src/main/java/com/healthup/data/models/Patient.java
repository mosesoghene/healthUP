package com.healthup.data.models;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Patient {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public Patient(String id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Document toDocument() {
        return new Document("_id", new ObjectId(id))
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("email", email)
                .append("password", password);
    }
}
