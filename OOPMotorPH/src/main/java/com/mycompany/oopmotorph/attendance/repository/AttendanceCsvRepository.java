/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.attendance.repository;

import com.mycompany.oopmotorph.attendance.model.AttendanceRecord;
import com.mycompany.oopmotorph.attendance.model.AttendanceStatus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AttendanceCsvRepository implements AttendanceRepository {

    private final Path csvPath;

    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("M/d/yyyy");
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("H:mm");

    public AttendanceCsvRepository(Path csvPath) {
        this.csvPath = csvPath;
    }

    @Override
    public List<AttendanceRecord> findAll() {
        List<AttendanceRecord> records = new ArrayList<>();
        if (!csvPath.toFile().exists()) return records;

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath.toFile()))) {
            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; } // header
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",", -1);

                String empId = safe(p, 0);
                String empName = safe(p, 1);
                LocalDate date = parseDate(safe(p, 2));
                String position = safe(p, 3);
                LocalTime timeIn = parseTime(safe(p, 4));
                LocalTime timeOut = parseTime(safe(p, 5));

                // Ignore CSV TotalHours/Status/Remarks -> Service computes
                records.add(new AttendanceRecord(
                        empId, empName, date, position, timeIn, timeOut,
                        AttendanceStatus.PRESENT, "" // placeholder
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return records;
    }

    private String safe(String[] p, int i) {
        return (p != null && i < p.length) ? p[i].trim() : "";
    }

    private LocalDate parseDate(String s) {
    if (s == null) return null;
    s = s.trim();
    if (s.isEmpty()) return null;

    // remove quotes if CSV has them
    if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
        s = s.substring(1, s.length() - 1).trim();
    }

    // try multiple common formats
    String[] patterns = {
        "M/d/yyyy", "MM/dd/yyyy",
        "M/d/yy", "MM/dd/yy",
        "yyyy-MM-dd"
    };

    for (String p : patterns) {
        try {
            return java.time.LocalDate.parse(s, java.time.format.DateTimeFormatter.ofPattern(p));
        } catch (Exception ignored) {}
    }
    return null;
}

    private LocalTime parseTime(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalTime.parse(s, timeFmt); }
        catch (Exception e) { return null; }
    }
}