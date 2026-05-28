package com.iskollect.model;

import java.time.LocalDateTime;

/**
 * Value object returned by InOutService to the controller after every
 * log attempt. Carries enough information for the UI to display
 * a meaningful confirmation or error message without exposing raw exceptions.
 */
public class LogResult {

    public enum Outcome {
        SUCCESS,       // Event recorded normally
        DUPLICATE,     // Same event type already logged within the duplicate window
        STUDENT_NOT_FOUND, // student_id has no matching record (stub: always bypassed until registration module)
        INVALID_INPUT, // studentId <= 0 or eventType is null
        DB_ERROR       // Unexpected database failure
    }

    private final Outcome outcome;
    private final InOutLog log;        // The persisted log (null if outcome != SUCCESS / DUPLICATE)
    private final String message;      // Human-readable summary for the UI
    private final LocalDateTime at;    // Timestamp of this result

    // ── Constructors ──────────────────────────────────────────────────────

    public LogResult(Outcome outcome, InOutLog log, String message) {
        this.outcome = outcome;
        this.log     = log;
        this.message = message;
        this.at      = LocalDateTime.now();
    }

    // ── Static factories ──────────────────────────────────────────────────

    public static LogResult success(InOutLog log) {
        return new LogResult(
            Outcome.SUCCESS,
            log,
            String.format("Logged: Student %d — %s at %s",
                log.getStudentId(), log.getEventType(), log.getTimestamp())
        );
    }

    public static LogResult duplicate(InOutLog existing) {
        return new LogResult(
            Outcome.DUPLICATE,
            existing,
            String.format("Duplicate: Student %d already has an active %s log (ID %d).",
                existing.getStudentId(), existing.getEventType(), existing.getLogId())
        );
    }

    public static LogResult studentNotFound(int studentId) {
        return new LogResult(
            Outcome.STUDENT_NOT_FOUND,
            null,
            // ── STUB NOTE ────────────────────────────────────────────────
            // This outcome is never returned in the current build.
            // StudentValidator.exists() always returns true until the
            // Student & Device Registration Module is wired in.
            // ─────────────────────────────────────────────────────────────
            "Student ID " + studentId + " not found. Registration module not active."
        );
    }

    public static LogResult invalidInput(String reason) {
        return new LogResult(Outcome.INVALID_INPUT, null, "Invalid input: " + reason);
    }

    public static LogResult dbError(String detail) {
        return new LogResult(Outcome.DB_ERROR, null, "Database error: " + detail);
    }

    // ── Getters ───────────────────────────────────────────────────────────

    public Outcome getOutcome()    { return outcome; }
    public InOutLog getLog()       { return log; }
    public String getMessage()     { return message; }
    public LocalDateTime getAt()   { return at; }

    public boolean isSuccess()     { return outcome == Outcome.SUCCESS; }
}