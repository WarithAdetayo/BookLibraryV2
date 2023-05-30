package org.mylibrary.core.entity;

import jakarta.persistence.*;
import org.mylibrary.core.dataTransfer.BookDetails;

@Entity
@Table
public class Book {
    @Id
    private String isbn;
    @Column(name="title", nullable = false)
    private String title;
    @Column(name = "author", nullable = false)
    private String author;
    @Column(name = "available_copies")
    private int availableCopies;
    @Column(name = "total_copies")
    private int totalCopies;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    public Book(String isbn, String title, String author, int copies, String description) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.availableCopies = copies;
        this.totalCopies = copies;
        this.description = description;
    }

    public Book(BookDetails bookDetails) {
        this.isbn = bookDetails.isbn();
        this.title = bookDetails.title();
        this.author = bookDetails.author();
        this.description = bookDetails.description();
        this.availableCopies = bookDetails.copies();
        this.totalCopies = bookDetails.copies();
    }

    public Book(){}

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        author = author;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BookDetails getDetails() {
        return new BookDetails(
                this.isbn, this.title, this.author,
                this.description, this.availableCopies, true
        );
    }

    public int addCopies(int copies) {
        this.availableCopies += copies;
        this.totalCopies += copies;

        return totalCopies;
    }

    public void getCopy() {
        this.availableCopies -= 1;
    }

    public void returnCopy() {
        this.availableCopies += 1;
    }
    @Override
    public String toString() {
        return String.format(
                "Book<ISBN=%s title=%s author=%s>",
                this.isbn, this.title, this.author
        );
    }
}
