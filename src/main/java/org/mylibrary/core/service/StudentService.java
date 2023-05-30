package org.mylibrary.core.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mylibrary.core.entity.Student;


public class StudentService {
    private final EntityManagerFactory emFactory;
    private final EntityManager entityManager;
    private final Logger logger;
    private final EntityTransaction transaction;

    public StudentService() {
        emFactory = Persistence.createEntityManagerFactory("MyLibrary");
        entityManager = emFactory.createEntityManager();
        transaction = entityManager.getTransaction();
        logger = LogManager.getLogger(StudentService.class);
    }

    public StudentService(String persistenceUnit) {
        emFactory = Persistence.createEntityManagerFactory(persistenceUnit);
        entityManager = emFactory.createEntityManager();
        transaction = entityManager.getTransaction();
        logger = LogManager.getLogger(StudentService.class);
    }

    public Student addStudent(Student student) {
        this.transaction.begin();
        entityManager.persist(student);
        this.transaction.commit();

        logger.info(String.format(
                "Student record persisted into database (%s)",
                student
        ));
        return  student;
    }

    public Student createAndAddStudent(String matric, String firstname, String lastname) {
        Student student = new Student(matric, firstname, lastname);
        logger.info(String.format(
                "New student record created (%s)",
                student
        ));
        return  this.addStudent(student);
    }

    public Student findStudent(String matric) {
        return entityManager.find(Student.class, matric);
    }

    public boolean studentIsPresent(String matric) {
        return (this.findStudent(matric) != null);
    }

    public Student updateStudent(Student student) {
        Student studentToUpdate = this.findStudent(student.getMatricNumber());

        if (studentToUpdate != null) {
            this.transaction.begin();
            studentToUpdate.setFirstName(student.getFirstName());
            studentToUpdate.setLastName(studentToUpdate.getLastName());
            logger.info(String.format(
                    "Student Record updated (%s)",
                    student
                    ));
            this.transaction.commit();
        } else {
            logger.warn(String.format(
                    "Student with Matric Number '%s' to update not found in the database",
                    student.getMatricNumber()
                    ));
        }

        return studentToUpdate;
    }

    public Student deleteStudent(String matricNumber) {
        Student studentToDelete = this.findStudent(matricNumber);

        if (studentToDelete != null) {
            this.transaction.begin();
            this.entityManager.remove(studentToDelete);
            this.transaction.commit();
            logger.info(String.format(
                    "Student record deleted from database (%s)",
                    studentToDelete
            ));
        } else {
            logger.warn(String.format(
                    "Student with Matric Number '%s' to delete not found in the database",
                    matricNumber
            ));
        }

        return studentToDelete;
    }

    public Student deleteStudent(Student student) {
        return this.deleteStudent(student.getMatricNumber());
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void close() {
        entityManager.close();
        emFactory.close();
    }
}
