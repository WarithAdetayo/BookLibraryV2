package org.mylibrary;


import org.mylibrary.core.Library;
import org.mylibrary.core.dataTransfer.BookDetails;
import org.mylibrary.core.dataTransfer.LendDetails;
import org.mylibrary.core.entity.Book;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Library library;
    public static void main(String[] args) {

        // Todo Operations
        // Register Student
        // Update Library (Add new book, Add more copies to exiting library)
        // Borrow book
        // return book

        library = Library.getInstance();

        System.out.println(">>>>>>>>> MyLibrary <<<<<<<<<<");
        System.out.println("Welcome Back Librarian!!");
        System.out.println("Select an action to perform");

        boolean programShouldEnd = false;

        while (!programShouldEnd) {
            System.out.println("1. Register Student");
            System.out.println("2. Update Library Catalogue");
            System.out.println("3. Lend Student book");
            System.out.println("4. Accept return from Student");
            System.out.println("0. Quit Program");

            int choice = acceptUserChoice(0, 4);

            switch (choice) {
                case 1 -> registerStudent();
                case 2 -> updateBookCatalogue();
                case 3 -> lendStudentABook();
                case 4 -> returnBook();
                case 0 -> {
                    programShouldEnd = true;
                    library.close();
                }
            }
        }

    }

    public static void registerStudent() {
        System.out.println(">>>>>>>>>>> Student Registration <<<<<<<<<<<<");

        Scanner sc = new Scanner(System.in);
        System.out.print("Pls Enter your matric number: ");

        String matric = sc.nextLine();
        if (library.studentIsAlreadyRegistered(matric)) {
            System.out.format("Student with matric number '%s' is already registered\n", matric);
            System.out.println("Press Enter to return back");
            sc.nextLine();
            return;
        }

        System.out.print("Enter your First Name: ");
        String firstName = sc.nextLine();
        System.out.print("Enter your Last Name: ");
        String lastName = sc.nextLine();

        library.registerNewStudent(matric, firstName, lastName);
        System.out.format(
                "%s %s (%s) registered successfully\n",
                firstName, lastName, matric
        );

        System.out.println("Press Enter to return back");
        sc.nextLine();
    }

    public static void updateBookCatalogue() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Pls Enter the ISBN of the book: ");
        String isbn = sc.nextLine();

        BookDetails book = library.checkBook(isbn);

        if (book.isInCatalogue()) {
            System.out.format("ISBN: %s\nTitle: %s\nAuthor: %s\n",
                    book.isbn(), book.title(), book.author()
            );

            System.out.print("Enter number of copies to add: ");
            int copies = integerInput();

            int totalCopies = library.addBookCopies(isbn, copies);

            if (totalCopies >= copies) {
                System.out.format(
                        "%d copy(ies) of %s successfully added. A total of %d now in catalogue",
                        copies, book.title(), totalCopies
                );
            } else {
                System.out.println("Error adding copies to catalogue for some reason even I cannot tell");
            }
        } else {
            System.out.print("Enter the Title of the book: ");
            String title = sc.nextLine();

            System.out.print("Enter the name of the Author of the book: ");
            String author = sc.nextLine();

            System.out.print("Enter the Description of the book: ");
            String description = sc.nextLine();

            System.out.print("How many copies do you want to add: ");
            int noOfCopies = integerInput();

            library.addBook(new BookDetails(isbn, title, author, description, noOfCopies, null));
            System.out.println("Book added to catalogue");
        }

        System.out.println("Press enter to return back to the main menu");
        sc.nextLine();
    }

    public static void lendStudentABook() {
        System.out.println(">>>>>>>>>>>>>>> Lend Student a Book <<<<<<<<<<<<<<<");

        Scanner sc = new Scanner(System.in);

        System.out.println("So you want to borrow book?");
        System.out.print("What is your matric number: ");

        String matric = sc.nextLine();

        if (!library.studentIsAlreadyRegistered(matric)) {
            System.out.println("Your matric number is not found in the record");
            System.out.println("Are you sure you are a registered student?");
            System.out.println("Comeback when you are a registered student!!! Getatofhere (-;-)");

            return;
        }

        if (library.studentHasUnreturnedBook(matric)) {
            System.out.println("Sorry! You are not eligible to borrow book");
            System.out.println("Pls return the book you borrowed before, before you can borrow another.");

            System.out.println("\nPress enter to go back to main menu");
            sc.nextLine();
            return;
        }

        System.out.println("1. Enter the Title and Author of the book you want to borrow");
        System.out.println("2. Browse catalogue and select a book you want to borrow");

        int choice = acceptUserChoice(1, 2);

        if (choice == 1) {
            System.out.print("What is the title of the book: ");
            String title = sc.nextLine();
            System.out.print("Who is the author of the book: ");
            String author = sc.nextLine();

            BookDetails book = library.searchBook(title, author);

            if (!book.isInCatalogue()) {
                System.out.println("Sorry, We don't have that book in our catalogue");

                System.out.println("\nPress enter to go back to main menu");
                sc.nextLine();
                return;
            }

            if (book.copies() == 0) {
                System.out.println("Sorry, all the copies of this book already borrowed");

                System.out.println("\nPress enter to go back to main menu");
                sc.nextLine();
                return;
            }

            lendOut(book.isbn(), matric);
        } else {
            List<BookDetails> allBooks = library.getAvailableBooks();

            System.out.println("SN. \t\tTitle\t\tAuthor\t\tDescription");
            for (int i = 0; i < allBooks.size(); i++) {

                BookDetails b = allBooks.get(i);
                System.out.format("%d. %s\t\t%s\t\t%s\n", i + 1,
                        b.title(), b.author(), b.description()
                );
            }

            choice = acceptUserChoice(1, allBooks.size());
            BookDetails selectedBook = allBooks.get(choice - 1);
            lendOut(selectedBook.isbn(), matric);
        }

        System.out.println("\nPress enter to go back to main menu");
        sc.nextLine();
    }

    private static void lendOut(String isbn, String matric) {
        LendDetails lendDetails = library.lendBook(isbn, matric);

        System.out.println("Lend Information");
        System.out.format("Book Title: %s\n", lendDetails.book().title());
        System.out.format("Book Author: %s\n", lendDetails.book().author());
        System.out.format("Book ISBN: %s\n", lendDetails.book().isbn());
        System.out.format("Date Borrowed: %s\n", lendDetails.date());
        System.out.format("Tracking ID: %d\n", lendDetails.id());
    }

    public static void returnBook() {
        System.out.println(">>>>>>>>>> Book Return <<<<<<<<<<<");

        Scanner sc = new Scanner(System.in);

        System.out.print("What is your matric Number: ");
        String matric = sc.nextLine();
        System.out.print("What is the isbn of the book you borrowed: ");
        String isbn = sc.nextLine();

        LendDetails lendDetails = library.returnBook(matric, isbn);
        System.out.println("Lend Information");
        System.out.format("Book Title: %s\n", lendDetails.book().title());
        System.out.format("Book Author: %s\n", lendDetails.book().author());
        System.out.format("Date Borrowed: %s\n", lendDetails.date());
        System.out.println("Status: Returned");

        System.out.println("\nPress enter to go back to main menu");
        sc.nextLine();
    }

    public static int integerInput() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print(": ");
            try {
                return sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Pls enter an integer");
                sc.nextLine();
            }
        }
    }

    public static int acceptUserChoice(int minChoice, int maxChoice) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print(": ");

            try {
                int choice = sc.nextInt();

                if (choice >= minChoice && choice <= maxChoice ) {
                    return choice;
                } else {
                    System.out.format("Choice number between (%d - %d)\n", minChoice, maxChoice);
                }
            } catch (Exception e) {
                System.out.format("Pls enter a number from list (%d - %d)\n", minChoice, maxChoice);
                sc.nextLine();
            }
        }
    }
}
