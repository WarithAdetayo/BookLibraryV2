package org.mylibrary.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class Student {
    @Id
    private String matricNumber;
    @Column(name = "firstname")
    private String firstName;
    @Column(name = "lastname")
    private String lastName;

    public Student(String matricNumber, String firstName, String lastName) {
        this.matricNumber = matricNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Student() {}

    public String getMatricNumber() {
        return matricNumber;
    }

    public void setMatricNumber(String matricNumber) {
        this.matricNumber = matricNumber;
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

    @Override
    public String toString() {
        return String.format(
                "Student<matric='%s', firstname='%s' lastname='%s'>",
                this.matricNumber, this.firstName, this.lastName
        );
    }
}
