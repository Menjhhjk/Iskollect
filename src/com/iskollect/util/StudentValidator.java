package com.iskollect.util;

/**
 * STUB — Student & Device Registration Module not yet implemented
 *
 * This class stands in for real student existence validation.
 * Currently, exists() always returns true so that the Ingress /
 * Egress monitoring logic can run and be tested independently.
 *
 * WHEN the Student & Device Registration Module is ready:
 *  1. Inject or instantiate a real StudentDAO here.
 *  2. Replace the stub body with:
 *      return studentDAO.findById(studentId) != null;
 *  3. Remove this comment block.
 */
public class StudentValidator {

    /**
     * Returns true if the given studentId corresponds to a registered student.
     *
     * STUB: unconditionally returns true.
     * Replace with a real StudentDAO lookup when the registration module is active.
     *
     * @param studentId the student ID entered by staff
     * @return true if the student exists; false otherwise
     */
    public boolean exists(int studentId) {
        // ── STUB ──────────────────────────────────────────────────────────
        // TODO: replace with → return studentDAO.findById(studentId) != null;
        // ─────────────────────────────────────────────────────────────────
        return true;
    }
}