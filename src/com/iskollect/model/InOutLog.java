package com.iskollect.model;

import java.time.LocalDateTime;

/**
 * Represents a single ingress or egress event logged by a staff member.
 *
 * NOTE — student_id is stored as a plain int with no FK enforcement yet.
 * The foreign key constraint to the students table will be added once
 * the Student & Device Registration Module is complete.
 */
public class InOutLog {

    // ── Fields ────────────────────────────────────────────────────────────

    private int logId;               // PK — auto-incremented by DB
    private int studentId;           // FK stub → students.student_id (unenforced until registration module)
    private EventType eventType;     // INGRESS or EGRESS
    private EntryMethod entryMethod; // How the event was recorded (currently: MANUAL)
    private LocalDateTime timestamp; // Exact date-time of the event
    private String staffNote;        // Optional note entered by staff (nullable)
    private LogStatus status;        // VALID, DUPLICATE, UNRESOLVED

    // ── Enums ─────────────────────────────────────────────────────────────

    public enum EventType {
        INGRESS,   // Student entering the premises
        EGRESS     // Student exiting the premises
    }

    public enum EntryMethod {
        MANUAL     // Staff manually typed the student ID
        // BARCODE, QR_SCAN, RFID — reserved for future input modules
    }

    public enum LogStatus {
        VALID,       // Normal, accepted event
        DUPLICATE,   // Same student logged same event type within the duplicate window
        UNRESOLVED   // Student ID not found in DB (registration module not yet active)
    }

    // ── Constructors ──────────────────────────────────────────────────────

    public InOutLog() {}

    /** Full constructor used when loading from the database. */
    public InOutLog(int logId, int studentId, EventType eventType,
                    EntryMethod entryMethod, LocalDateTime timestamp,
                    String staffNote, LogStatus status) {
        this.logId       = logId;
        this.studentId   = studentId;
        this.eventType   = eventType;
        this.entryMethod = entryMethod;
        this.timestamp   = timestamp;
        this.staffNote   = staffNote;
        this.status      = status;
    }

    /** Creation constructor — logId not yet assigned (DB will auto-increment). */
    public InOutLog(int studentId, EventType eventType, EntryMethod entryMethod,
                    LocalDateTime timestamp, String staffNote, LogStatus status) {
        this.studentId   = studentId;
        this.eventType   = eventType;
        this.entryMethod = entryMethod;
        this.timestamp   = timestamp;
        this.staffNote   = staffNote;
        this.status      = status;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────

    public int getLogId()                    { return logId; }
    public void setLogId(int logId)          { this.logId = logId; }

    public int getStudentId()                { return studentId; }
    public void setStudentId(int studentId)  { this.studentId = studentId; }

    public EventType getEventType()                   { return eventType; }
    public void setEventType(EventType eventType)     { this.eventType = eventType; }

    public EntryMethod getEntryMethod()                    { return entryMethod; }
    public void setEntryMethod(EntryMethod entryMethod)    { this.entryMethod = entryMethod; }

    public LocalDateTime getTimestamp()                   { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp)     { this.timestamp = timestamp; }

    public String getStaffNote()                  { return staffNote; }
    public void setStaffNote(String staffNote)    { this.staffNote = staffNote; }

    public LogStatus getStatus()                  { return status; }
    public void setStatus(LogStatus status)       { this.status = status; }

    // ── Utility ───────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format("InOutLog{logId=%d, studentId=%d, type=%s, method=%s, time=%s, status=%s}",
                logId, studentId, eventType, entryMethod, timestamp, status);
    }
}