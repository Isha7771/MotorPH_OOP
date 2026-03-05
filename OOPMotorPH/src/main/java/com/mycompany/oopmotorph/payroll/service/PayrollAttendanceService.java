/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.payrollstaff.service;

import com.mycompany.oopmotorph.attendance.model.AttendanceRecord;
import com.mycompany.oopmotorph.attendance.model.AttendanceStatus;
import com.mycompany.oopmotorph.attendance.service.AttendanceService;
import com.mycompany.oopmotorph.payrollstaff.model.PayrollAttendanceSummary;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class PayrollAttendanceService {

    private final AttendanceService attendanceService;

    public PayrollAttendanceService(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

   public List<PayrollAttendanceSummary> summarize(LocalDate from, LocalDate to, String search) throws IOException {

    // Load all attendance (rules applied) + apply search
    List<AttendanceRecord> all = attendanceService.getAttendance(search, null, (AttendanceStatus) null);

    // If no date range, show ALL (start to finish)
    List<AttendanceRecord> scope;
    if (from == null || to == null) {
        scope = all;
    } else {
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("To date must be after (or equal to) From date.");
        }

        scope = all.stream()
                .filter(r -> r.getDate() != null)
                .filter(r -> !r.getDate().isBefore(from) && !r.getDate().isAfter(to))
                .collect(Collectors.toList());
    }

    Map<String, List<AttendanceRecord>> byEmp = scope.stream()
            .collect(Collectors.groupingBy(r -> safe(r.getEmployeeId())));

    List<PayrollAttendanceSummary> out = new ArrayList<>();

    for (Map.Entry<String, List<AttendanceRecord>> e : byEmp.entrySet()) {
        String empId = e.getKey();
        List<AttendanceRecord> rows = e.getValue();

        String empName = rows.stream()
                .map(AttendanceRecord::getEmployeeName)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("");

        int present = (int) rows.stream().filter(r -> r.getStatus() == AttendanceStatus.PRESENT).count();
        int late = (int) rows.stream().filter(r -> r.getStatus() == AttendanceStatus.LATE).count();
        int absent = (int) rows.stream().filter(r -> r.getStatus() == AttendanceStatus.ABSENT).count();

        double totalHours = rows.stream().mapToDouble(AttendanceRecord::getTotalHours).sum();

        out.add(new PayrollAttendanceSummary(empId, empName, present, late, absent, totalHours));
    }

    out.sort(Comparator.comparing(PayrollAttendanceSummary::getEmployeeId, String.CASE_INSENSITIVE_ORDER));
    return out;
}

private String safe(String s) {
    return (s == null) ? "" : s.trim();
}
}
