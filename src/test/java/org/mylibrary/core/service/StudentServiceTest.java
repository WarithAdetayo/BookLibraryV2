package org.mylibrary.core.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mylibrary.core.entity.Student;

import static org.junit.jupiter.api.Assertions.*;

class StudentServiceTest {
    private static StudentService studentService;

    @BeforeAll
    static void beforeAll() throws Exception {
        studentService = new StudentService("MyLibraryTest");
    }

    @AfterAll
    static void afterAll() {
        studentService.close();
    }

    @Test
    void addStudent() {
        Student student = new Student(
                "7455wf", "Joshua", "Hathaway"
        );

        Student s = studentService.addStudent(student);

        assertNotNull(s);
        assertEquals(student.getMatricNumber(), s.getMatricNumber());
        assertEquals(student.getFirstName(), s.getFirstName());
        assertEquals(student.getLastName(), s.getLastName());

        assertNotNull(studentService.getEntityManager().find(Student.class, student.getMatricNumber()));
    }

    @Test
    void createAndAddStudent() {
        Student s = studentService.createAndAddStudent(
                "6763d", "Michael", "Klingon"
        );
        assertNotNull(s);
        assertEquals("6763d", s.getMatricNumber());
        assertEquals("Michael", s.getFirstName());
        assertEquals("Klingon", s.getLastName());
    }

    @Test
    void findStudent() {
        Student student = new Student(
                "2347gf", "Emmanuel", "Stones"
        );

        studentService.addStudent(student);

        Student s = studentService.findStudent(student.getMatricNumber());

        assertNotNull(s);
        assertNull(studentService.findStudent("64656vd")); // Non-existence matric number
    }

    @Test
    void updateStudent() {
        Student student = new Student(
                "557gf", "Emmanuel", "Stones"
        );

        studentService.addStudent(student);

        Student s = studentService.findStudent(student.getMatricNumber());
        assertEquals(student.getFirstName(), s.getFirstName());

        Student studentUpdate= new Student(
                student.getMatricNumber(), "Laura", "Stones"
        );

        s = studentService.findStudent(studentUpdate.getMatricNumber());
        assertNotEquals(studentUpdate.getFirstName(), s.getFirstName());

        studentService.updateStudent(studentUpdate);
        s = studentService.findStudent(student.getMatricNumber());
        assertEquals(studentUpdate.getFirstName(), s.getFirstName());
        assertEquals(studentUpdate.getFirstName(), student.getFirstName());
    }

    @Test
    void deleteStudent() {
        Student student = new Student(
                "467gf", "Emmanuel", "Stones"
        );

        studentService.addStudent(student);
        Student s = studentService.findStudent(student.getMatricNumber());
        assertNotNull(s);

        studentService.deleteStudent(student);
        s = studentService.findStudent(student.getMatricNumber());
        assertNull(s);

        student = new Student(
                "t4567gf", "NewStudent", "Surname"
        );
        studentService.addStudent(student);
        s = studentService.findStudent(student.getMatricNumber());
        assertNotNull(s);

        studentService.deleteStudent(student.getMatricNumber());
        s = studentService.findStudent(student.getMatricNumber());
        assertNull(s);
    }
}
