package com.iskollect.model;

import java.time.LocalDateTime;

public class InOutLog2 {
    private int studentId;
    private LocalDateTime timestamp;

    public enum EventType {
        INGRESS, EGRESS
    }
    public enum EntryMethod {
        MANUAL
    }
    public enum LogStatus {
        VALID, DUPLICATE, UNRESOLVED
    }

    public InOutLog2(int studentId, LocalDateTime timestamp) {
        this.studentId = studentId;
        this.timestamp = timestamp;
    }

    //getters and setters
    public int getStudentId() {
        return studentId;
    }
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}