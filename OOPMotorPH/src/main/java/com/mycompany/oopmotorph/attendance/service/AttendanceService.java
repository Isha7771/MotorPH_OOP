/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.attendance.service;

import com.mycompany.oopmotorph.attendance.model.AttendanceRecord;
import com.mycompany.oopmotorph.attendance.model.AttendanceStatus;
import com.mycompany.oopmotorph.attendance.repository.AttendanceRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AttendanceService {

    private final AttendanceRepository attendanceRepo;

    // Late rule: after 9:00 AM
    private static final LocalTime LATE_CUTOFF = LocalTime.of(9, 0);

    public AttendanceService(AttendanceRepository attendanceRepo) {
        this.attendanceRepo = attendanceRepo;
    }

    /**
     * Read-only attendance list with filters.
     * - search: matches employeeId, employeeName, position
     * - date: exact match (nullable)
     * - statusOrNull: nullable (null means "All")
     */
    public List<AttendanceRecord> getAttendance(String search, LocalDate date, AttendanceStatus statusOrNull) {
        List<AttendanceRecord> raw = attendanceRepo.findAll();

        return raw.stream()
                // date filter (if chosen)
                .filter(r -> date == null || date.equals(r.getDate()))
                // compute status/remarks based on time-in/out
                .map(this::applyRules)
                // search filter
                .filter(r -> matchesSearch(r, search))
                // status filter
                .filter(r -> matchesStatus(r, statusOrNull))
                // stable sorting
                .sorted(Comparator
                        .comparing(AttendanceRecord::getDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(AttendanceRecord::getEmployeeId, Comparator.nullsLast(String::compareToIgnoreCase)))
                .collect(Collectors.toList());
    }

    /**
     * Convenience overload for HR UI that passes status as a String from combo box:
     * "All", "Present", "Late", "Absent"
     */
    public List<AttendanceRecord> getAttendance(String search, LocalDate date, String statusTextOrAll) {
        AttendanceStatus st = parseStatus(statusTextOrAll);
        return getAttendance(search, date, st);
    }

    private AttendanceStatus parseStatus(String statusTextOrAll) {
        if (statusTextOrAll == null) return null;
        String s = statusTextOrAll.trim().toUpperCase(Locale.ROOT);
        if (s.isEmpty() || s.equals("ALL")) return null;

        // map UI strings to enum safely
        try {
            return AttendanceStatus.valueOf(s);
        } catch (Exception ignored) {
            // if UI passes "Present" etc but enum is PRESENT, this handles it.
            if (s.equals("PRESENT")) return AttendanceStatus.PRESENT;
            if (s.equals("LATE")) return AttendanceStatus.LATE;
            if (s.equals("ABSENT")) return AttendanceStatus.ABSENT;
            return null;
        }
    }

    private AttendanceRecord applyRules(AttendanceRecord r) {
        AttendanceStatus status;
        String remarks;

        if (r.getTimeIn() == null || r.getTimeOut() == null) {
            status = AttendanceStatus.ABSENT;
            remarks = "No time in/out";
        } else if (r.getTimeIn().isAfter(LATE_CUTOFF)) {
            status = AttendanceStatus.LATE;
            remarks = "Late time-in";
        } else {
            status = AttendanceStatus.PRESENT;
            remarks = "On time";
        }

        return new AttendanceRecord(
                r.getEmployeeId(),
                r.getEmployeeName(),
                r.getDate(),
                r.getPosition(),
                r.getTimeIn(),
                r.getTimeOut(),
                status,
                remarks
        );
    }

    private boolean matchesSearch(AttendanceRecord r, String search) {
        if (search == null || search.isBlank()) return true;
        String q = search.trim().toLowerCase(Locale.ROOT);

        return contains(r.getEmployeeId(), q)
                || contains(r.getEmployeeName(), q)
                || contains(r.getPosition(), q);
    }

    private boolean matchesStatus(AttendanceRecord r, AttendanceStatus statusOrNull) {
        if (statusOrNull == null) return true;
        return statusOrNull == r.getStatus();
    }

    private boolean contains(String field, String q) {
        return field != null && field.toLowerCase(Locale.ROOT).contains(q);
    }
}