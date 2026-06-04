package com.iskollect.service;

import com.iskollect.dao.StudentDAO;
import com.iskollect.exception.DatabaseException;
import com.iskollect.model.Student;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class StreakService {
    private final StudentDAO studentDAO;

    public StreakService() {
        this(new StudentDAO());
    }

    public StreakService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    public double evaluateStreak(Student student, int bottles) throws DatabaseException {
        LocalDate today = LocalDate.now();
        LocalDate lastSubmitDate = student.getLastSubmitDate();
        int streak = student.getStreak();

        if (lastSubmitDate == null) {
            streak = 1;
        } else {
            long gap = ChronoUnit.DAYS.between(lastSubmitDate, today);
            if (gap == 1) {
                streak++;
            } else if (gap > 1) {
                streak = 1;
            }
        }

        double bonus = 0;
        if (streak == 5) {
            bonus = bottles * 1.0;
        } else if (streak == 3) {
            bonus = bottles * 0.50;
        }

        student.setStreak(streak);
        student.setWeeklyBottles(student.getWeeklyBottles() + bottles);
        student.setLastSubmitDate(today);
        studentDAO.updateWeeklyStats(student.getStudentId(), student.getWeeklyBottles(), streak, today);
        return bonus;
    }

    public int getStreakCount(int studentId) {
        try {
            Student student = studentDAO.findById(studentId);
            return student == null ? 0 : student.getStreak();
        } catch (DatabaseException e) {
            return 0;
        }
    }
}
