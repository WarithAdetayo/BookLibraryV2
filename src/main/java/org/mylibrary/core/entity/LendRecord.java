package org.mylibrary.core.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table
public class LendRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    private Book book;
    private Student student;
    private Date dateBorrowed;
    private boolean returned;

    public LendRecord(Book book, Student student, Date dateBorrowed, boolean returned) {
        this.book = book;
        this.student = student;
        this.dateBorrowed = dateBorrowed;
        this.returned = returned;
    }

    public LendRecord(Book book, Student student) {
        this.book = book;
        this.student = student;
        this.returned = false;
        this.dateBorrowed = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Date getDateBorrowed() {
        return dateBorrowed;
    }

    public void setDateBorrowed(Date dateBorrowed) {
        this.dateBorrowed = dateBorrowed;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }
}
