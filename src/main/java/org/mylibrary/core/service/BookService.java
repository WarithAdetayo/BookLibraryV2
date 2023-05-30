package org.mylibrary.core.service;

import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mylibrary.core.entity.Book;

import java.util.List;


public class BookService {
    private final EntityManagerFactory emFactory;
    private final EntityManager entityManager;
    private final Logger logger;
    private final EntityTransaction transaction;

    public BookService() {
        emFactory = Persistence.createEntityManagerFactory("MyLibrary");
        entityManager = emFactory.createEntityManager();
        transaction = entityManager.getTransaction();
        logger = LogManager.getLogger(BookService.class);
    }

    public BookService(String persistenceUnit) {
        emFactory = Persistence.createEntityManagerFactory(persistenceUnit);
        entityManager = emFactory.createEntityManager();
        transaction = entityManager.getTransaction();
        logger = LogManager.getLogger(BookService.class);
    }

    public Book addBook(Book book) {
        this.transaction.begin();
        entityManager.persist(book);
        this.transaction.commit();
        logger.info(String.format(
                "Book record persisted into database (%s)",
                book
        ));
        return  book;
    }

    public Book createAndAddBook(String isbn, String title, String author, int copies, String description) {
        Book book = new Book(isbn, title, author, copies, description);
        logger.info(String.format(
                "New Book record created (%s)",
                book
        ));

        return  this.addBook(book);
    }

    public Book findBook(String isbn) {
        return entityManager.find(Book.class, isbn);
    }

    public boolean bookIsPresent(String isbn) {
        return (this.findBook(isbn) != null);
    }

    public Book updateBook(Book book) {
        Book bookToUpdate = this.findBook(book.getIsbn());

        if (bookToUpdate != null) {
            this.transaction.begin();
            bookToUpdate.setAuthor(book.getAuthor());
            bookToUpdate.setAvailableCopies(book.getAvailableCopies());
            bookToUpdate.setTitle(book.getTitle());
            bookToUpdate.setTotalCopies(book.getTotalCopies());
            bookToUpdate.setDescription(book.getDescription());

            logger.info(String.format(
                    "Book Record updated (%s)",
                    bookToUpdate
            ));
            this.transaction.commit();
        } else {
            logger.error(String.format(
                    "Book with ISBN Number '%s' to update not found in the database",
                    book.getIsbn()
            ));
        }

        return bookToUpdate;
    }

    public int updateBookCopies(String isbn, int copies) {
        Book bookToUpdate = this.findBook(isbn);

        if (bookToUpdate != null) {
            this.transaction.begin();
            int totalCopies = bookToUpdate.addCopies(copies);

            logger.info(String.format(
                    "Book Record updated (%s)",
                    bookToUpdate
            ));
            this.transaction.commit();

            return totalCopies;
        } else {
            logger.error(String.format(
                    "Book with ISBN Number '%s' to update not found in the database",
                    isbn
            ));
        }

        return -1;
    }

    public Book findBookByTitleAndAuthor(String title, String author) {
        Query query = entityManager.createQuery(
                "SELECT book FROM Book book WHERE book.title = :title AND book.author = :author"
        );

        query.setParameter("title", title);
        query.setParameter("author", author);

        try {
            return (Book)query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    public List<Book> getAllAvailableBooks() {
        Query query = entityManager.createQuery(
                "SELECT book FROM Book book"
        );

        try {
            return  query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Book deleteBook(String isbn) {
        Book bookToDelete = this.findBook(isbn);

        if (bookToDelete != null) {
            this.transaction.begin();
            this.entityManager.remove(bookToDelete);
            this.transaction.commit();
            logger.info(String.format(
                    "Book record deleted from database (%s)",
                    bookToDelete
            ));
        } else {
            logger.warn(String.format(
                    "Book with ISBN Number '%s' to delete not found in the database",
                    isbn
            ));
        }

        return bookToDelete;
    }

    public Book deleteBook(Book book) {
        return  this.deleteBook(book.getIsbn());
    }

    public void close() {
        this.entityManager.close();
        this.emFactory.close();
    }

    public EntityManager getEntityManager() {
        return  this.entityManager;
    }
}
