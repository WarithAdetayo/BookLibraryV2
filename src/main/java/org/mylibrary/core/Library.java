package org.mylibrary.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mylibrary.core.dataTransfer.BookDetails;
import org.mylibrary.core.dataTransfer.LendDetails;
import org.mylibrary.core.entity.Book;
import org.mylibrary.core.entity.LendRecord;
import org.mylibrary.core.entity.Student;
import org.mylibrary.core.service.BookService;
import org.mylibrary.core.service.LendRecordService;
import org.mylibrary.core.service.StudentService;

import java.util.List;
import java.util.stream.Collectors;

public class Library {

    private static Library instance = null;
    private final BookService bookService;
    private final StudentService studentService;
    private final LendRecordService lendRecordService;
    private final Logger logger;

    private Library(){
        bookService = new BookService();
        studentService = new StudentService();
        lendRecordService = new LendRecordService();
        logger = LogManager.getLogger(Library.class);
    }

    public static Library getInstance(){
        if (instance == null) {
            instance = new Library();
        }
        return instance;
    }

    public boolean studentIsAlreadyRegistered(String matric) {
        return studentService.studentIsPresent(matric);
    }

    public void registerNewStudent(String matric, String firstName, String lastName) {
        studentService.createAndAddStudent(matric, firstName, lastName);
        logger.info("New Student Registered in Library");
    }

    public boolean bookIsAlreadyInLibrary(String isbn) {
        return  bookService.bookIsPresent(isbn);
    }

    public BookDetails checkBook(String isbn) {
        Book book = bookService.findBook(isbn);

        if (book == null)
            return  new BookDetails(null, null, null, null, 0, false);
        return book.getDetails();
    }
    public void addBook(BookDetails bookDetails) {
        bookService.addBook(new org.mylibrary.core.entity.Book(bookDetails));
        logger.info("New Book Added to library's catalolgue");
    }

    public int addBookCopies(String isbn, int copies) {
        return bookService.updateBookCopies(isbn, copies);
    }

    public boolean studentHasUnreturnedBook(String matric) {
        LendRecord record = lendRecordService.getUnreturnedLendRecordByStudent(matric);

        return  (record != null);
    }

    public BookDetails searchBook(String title, String author) {
        Book book = bookService.findBookByTitleAndAuthor(title, author);
        if (book == null) {
            return new BookDetails(null, null, null, null, 0, false);
        }
        return book.getDetails();
    }

    public LendDetails lendBook(String isbn, String matric) {
        Book book = bookService.findBook(isbn);
        Student student = studentService.findStudent(matric);

        if (book.getAvailableCopies() < 1) {
            logger.error(String.format("No more copy is available for %s", book));
            return null;
        }

        book.getCopy();
        LendRecord record = lendRecordService.createAndAddLendRecord(book, student);
        logger.info(String.format("%s lent to %s", book, student));
        return new LendDetails(record.getBook().getDetails(), record.getDateBorrowed(), record.getId());
    }

    public List<BookDetails> getAvailableBooks() {
        return bookService.getAllAvailableBooks()
                .stream().map(Book::getDetails)
                .toList();
    }

    public LendDetails returnBook(String matric, String isbn) {
        LendRecord lendRecord = lendRecordService.getUnreturnedLendRecordByStudent(matric);

        assert(lendRecord.getBook().getIsbn().equals(isbn));

        lendRecordService.getEntityManager().getTransaction().begin();

        lendRecord.getBook().returnCopy();

        lendRecord.setReturned(true);

        lendRecordService.getEntityManager().getTransaction().commit();
        logger.info(String.format(
                "%s borrowed by %s returned\n",
                lendRecord.getBook(), lendRecord.getStudent()
        ));
        return new LendDetails(lendRecord.getBook().getDetails(), lendRecord.getDateBorrowed(), lendRecord.getId());
    }

    public void close() {
        bookService.close();
        studentService.close();
        lendRecordService.close();
    }
}
