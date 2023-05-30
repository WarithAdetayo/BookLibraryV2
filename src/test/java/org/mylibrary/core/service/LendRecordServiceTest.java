package org.mylibrary.core.service;

import org.junit.jupiter.api.*;
import org.mylibrary.core.entity.Book;
import org.mylibrary.core.entity.LendRecord;
import org.mylibrary.core.entity.Student;

import static org.junit.jupiter.api.Assertions.*;

class LendRecordServiceTest {
    private static LendRecordService lendRecordService;
    private static StudentService studentService;
    private static BookService bookService;
    Student studentA;
    Student studentB;
    Student studentC;
    Book bookA;
    Book bookB;
    Book bookC;

    @BeforeAll
    static void beforeAll() throws Exception {
        lendRecordService = new LendRecordService("MyLibraryTest");
        studentService = new StudentService("MyLibraryTest");
        bookService = new BookService("MyLibraryTest");
    }

    @AfterAll
    static void afterAll() {
        lendRecordService.close();
        studentService.close();
        bookService.close();
    }

    @BeforeEach
    void setUp() {
        studentA = new Student("MatricA", "NameA", "lastNameA");
        studentB = new Student("MatricB", "NameB", "lastNameB");
        studentC = new Student("MatricC", "NameC", "lastNameC");

        bookA = new Book("4575", "Test Title 1", "TestAuthor 1", 4, "Some DescriptionA");
        bookB = new Book("6575", "Test Title 2", "TestAuthor 2", 4, "Some DescriptionB");
        bookC = new Book("7575", "Test Title 3", "TestAuthor 3", 4, "Some DescriptionC");
    }

    @Test
    void createAndAddLendRecord() {

        LendRecord record = lendRecordService.createAndAddLendRecord(bookB, studentB);
        assertNull(record);

        studentService.addStudent(studentB);
        bookService.addBook(bookB);

        record = lendRecordService.createAndAddLendRecord(bookB, studentB);
        assertNotNull(record);
    }

    @Test
    void getUnreturnedLendRecordByStudent() {
        studentService.addStudent(studentA);
        bookService.addBook(bookA);

        LendRecord record = lendRecordService.createAndAddLendRecord(bookA, studentA);

        LendRecord unreturned = lendRecordService.getUnreturnedLendRecordByStudent(studentA.getMatricNumber());

        assertNotNull(unreturned);
    }
}