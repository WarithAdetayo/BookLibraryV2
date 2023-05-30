package org.mylibrary.core.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mylibrary.core.entity.Book;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {
    private static BookService bookService;

    @BeforeAll
    static void beforeAll() {
        bookService = new BookService("MyLibraryTest");
    }

    @AfterAll
    static void afterAll() {
        bookService.close();
    }

    @Test
    void addBook() {
        Book b = new Book(
                "435443", "TestTitle",
                "TestAuthor", 5, "Some Description"
        );
        bookService.addBook(b);

        assertNotNull(bookService.getEntityManager().find(Book.class, b.getIsbn()));
    }

    @Test
    void createAndAddBook() {
        Book b = bookService.createAndAddBook(
                "testISBN", "TestTitle", "TestAuthor",
                4, "SomeDescription");
        assertNotNull(b);
        assertEquals("testISBN", b.getIsbn());
        assertEquals("TestTitle", b.getTitle());
        assertEquals("TestAuthor", b.getAuthor());
        assertEquals(4, b.getAvailableCopies());
        assertEquals(4, b.getTotalCopies());
    }

    @Test
    void findBook() {
        Book b = new Book(
                "testISBN2", "TestTitle2", "TestAuthor2",
                4, "Some Other Description");

        assertNull(bookService.findBook(b.getIsbn()));
        bookService.addBook(b);
        assertNotNull(bookService.findBook(b.getIsbn()));

    }

    @Test
    void bookIsPresent() {
        Book b = new Book(
                "testISBN3", "TestTitle3", "TestAuthor3",
                4, "Some More Other Description");

        assertFalse(bookService.bookIsPresent(b.getIsbn()));
        bookService.addBook(b);
        assertTrue(bookService.bookIsPresent(b.getIsbn()));
    }

    @Test
    void updateBook() {
        Book b = bookService.createAndAddBook(
                "testISBN3", "TestTitle3", "TestAuthor3",
                4, "Some More Other Description");


        Book bookUpdate = new Book();
        bookUpdate.setIsbn(b.getIsbn());
        bookUpdate.setAvailableCopies(b.getAvailableCopies());
        bookUpdate.setTotalCopies(b.getTotalCopies());
        bookUpdate.setAuthor(b.getAuthor());
        bookUpdate.setTitle("New Title Update");

        assertNotEquals(bookUpdate.getTitle(), bookService.findBook(b.getIsbn()).getTitle());

        bookService.updateBook(bookUpdate);
    }

    @Test
    void deleteBook() {
        Book b = new Book(
                "testISBN4", "TestTitle4", "TestAuthor4",
                4, "Some New Description");

        Book deletedBook = bookService.deleteBook(b);

        assertNull(deletedBook);

        bookService.addBook(b);

        deletedBook = bookService.deleteBook(b);

        assertNotNull(deletedBook);
        assertFalse(bookService.bookIsPresent(b.getIsbn()));
        assertFalse(bookService.bookIsPresent(deletedBook.getIsbn()));

    }

    @Test
    void findBookByTitleAndAuthor() {

        Book b = bookService.createAndAddBook(
                "testISBN4", "TestTitle34", "TestAuthor4",
                4, "Some More Other Description");

        Book searchedBook = bookService.findBookByTitleAndAuthor("UnknownTitle", "TestAuthor4");
        assertNull(searchedBook);

        searchedBook = bookService.findBookByTitleAndAuthor(b.getTitle(), b.getAuthor());

        assertNotNull(searchedBook);

    }

    @Test
    void getAllAvailableBooks() {
        Book b = bookService.createAndAddBook(
                "47668904", "SomeBookTitle", "Some Author",
                4, "Just some book for test");

        Book b2 = bookService.createAndAddBook(
                "65476904", "SomeTestBookTitle", "Test Author",
                2, "Just another book for test");

        List<Book> books = bookService.getAllAvailableBooks();

        assertNotNull(books);

        books.forEach(System.out::println);

    }
}