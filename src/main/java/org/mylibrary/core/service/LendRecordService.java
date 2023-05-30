package org.mylibrary.core.service;

import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mylibrary.core.entity.Book;
import org.mylibrary.core.entity.LendRecord;
import org.mylibrary.core.entity.Student;

import java.util.Date;
import java.util.List;

public class LendRecordService {
    private final EntityManagerFactory emFactory;
    private final EntityManager entityManager;
    private final Logger logger;
    private final EntityTransaction transaction;
    private final String persistenceUnit;

    public LendRecordService() {
        this.persistenceUnit = "MyLibrary";
        emFactory = Persistence.createEntityManagerFactory(this.persistenceUnit);
        entityManager = emFactory.createEntityManager();
        transaction = entityManager.getTransaction();
        logger = LogManager.getLogger(LendRecordService.class);
    }

    public LendRecordService(String persistenceUnit) {
        this.persistenceUnit = persistenceUnit;
        emFactory = Persistence.createEntityManagerFactory(this.persistenceUnit);
        entityManager = emFactory.createEntityManager();
        transaction = entityManager.getTransaction();
        logger = LogManager.getLogger(LendRecordService.class);
    }

    public LendRecord addLendRecord(LendRecord lendRecord) {
        StudentService ss = new StudentService(this.persistenceUnit);
        BookService bs = new BookService(this.persistenceUnit);

        if (!ss.studentIsPresent(lendRecord.getStudent().getMatricNumber())) {
            logger.error(String.format(
                    "Student Not in database '%s'", lendRecord.getStudent()
            ));
            ss.close();
            bs.close();
            return null;
        }

        if (!bs.bookIsPresent(lendRecord.getBook().getIsbn())) {
            logger.error(String.format("Book is not in database '%s'", lendRecord.getBook()));
            ss.close();
            bs.close();
            return null;
        }

        ss.close();
        bs.close();

        this.transaction.begin();
        entityManager.persist(lendRecord);
        this.transaction.commit();
        logger.info(String.format(
                "LendRecord persisted into database (%s)",
                lendRecord
        ));
        return  lendRecord;
    }

    public LendRecord createAndAddLendRecord(Book book, Student student, Date dateBorrowed, boolean returned) {
        LendRecord lendRecord = new LendRecord(book, student, dateBorrowed, returned);
        logger.info(String.format(
                "New LendRecord created (%s)",
                lendRecord
        ));
        return  this.addLendRecord(lendRecord);
    }

    public LendRecord createAndAddLendRecord(Book book, Student student) {
        LendRecord lendRecord = new LendRecord(book, student);
        logger.info(String.format(
                "New LendRecord created (%s)",
                lendRecord
        ));
        return  this.addLendRecord(lendRecord);
    }

    public LendRecord getUnreturnedLendRecordByStudent(String matric) {
        Query query = entityManager.createQuery(
                "SELECT record FROM LendRecord record WHERE record.student.matricNumber = :matricNumber AND record.returned = false"
        );

        query.setParameter("matricNumber", matric);

        try {
            return (LendRecord) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public LendRecord findLendRecord(int id) {
        return entityManager.find(LendRecord.class, id);
    }

    public LendRecord updateLendRecord(LendRecord lendRecord) {
        LendRecord lendRecordToUpdate = this.findLendRecord(lendRecord.getId());

        if (lendRecordToUpdate != null) {
            this.transaction.begin();
            lendRecordToUpdate.setReturned(lendRecord.isReturned());
            lendRecordToUpdate.setBook(lendRecord.getBook());
            lendRecordToUpdate.setDateBorrowed(lendRecord.getDateBorrowed());
            lendRecordToUpdate.setStudent(lendRecord.getStudent());
            logger.info(String.format(
                    "LendRecord updated (%s)",
                    lendRecordToUpdate
            ));
            this.transaction.commit();
        } else {
            logger.warn(String.format(
                    "LendRecord with ID '%d' to update not found in the database",
                    lendRecord.getId()
            ));
        }

        return lendRecordToUpdate;
    }

    public LendRecord deleteLendRecord(int id) {
        LendRecord lendRecord = this.findLendRecord(id);

        if (lendRecord != null) {
            this.transaction.begin();
            this.entityManager.remove(lendRecord);
            this.transaction.commit();
            logger.info(String.format(
                    "LendRecord deleted from database (%s)",
                    lendRecord
            ));
        } else {
            logger.warn(String.format(
                    "LendRecord with ID '%d' to delete not found in the database",
                    id
            ));
        }

        return lendRecord;
    }

    public LendRecord deleteLendRecord(LendRecord lendRecord) {
        return this.deleteLendRecord(lendRecord.getId());
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void close() {
        entityManager.close();
        emFactory.close();
    }
}
