package com.iskollect.service;

import com.iskollect.dao.StudentDAO;
import com.iskollect.dao.TransactionDAO;
import com.iskollect.exception.DatabaseException;
import com.iskollect.model.Student;

public class PointsService {
    private final StudentDAO studentDAO;
    private final TransactionDAO transactionDAO;

    public PointsService() {
        this(new StudentDAO(), new TransactionDAO());
    }

    public PointsService(StudentDAO studentDAO, TransactionDAO transactionDAO) {
        this.studentDAO = studentDAO;
        this.transactionDAO = transactionDAO;
    }

    public double calculateBasePoints(int bottles) {
        return bottles * 0.5;
    }

    public double getTotalPoints(int studentId) {
        try {
            Student student = studentDAO.findById(studentId);
            return student == null ? 0 : student.getTotalPoints();
        } catch (DatabaseException e) {
            return 0;
        }
    }

    public boolean deductPoints(int studentId, double amount) {
        if (amount < 0) {
            return false;
        }
        try {
            return studentDAO.deductPointsAtomic(studentId, amount);
        } catch (DatabaseException e) {
            return false;
        }
    }

    public void recalculatePoints(int studentId) {
        try {
            Student student = studentDAO.findById(studentId);
            if (student == null) {
                return;
            }
            double earned = transactionDAO.getTotalPoints(studentId);
            studentDAO.updatePoints(studentId, earned);
        } catch (DatabaseException e) {
            System.err.println("recalculatePoints failed: " + e.getMessage());
        }
    }
}
