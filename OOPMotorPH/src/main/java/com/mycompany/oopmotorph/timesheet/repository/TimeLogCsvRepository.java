/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.timesheet.repository;

import com.mycompany.oopmotorph.timesheet.model.TimeLog;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TimeLogCsvRepository implements TimeLogRepository {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("H:mm");

    private final Path csvPath;

    public TimeLogCsvRepository(Path csvPath) {
        this.csvPath = Objects.requireNonNull(csvPath);
    }

    @Override
    public List<TimeLog> findAll() {
        if (!Files.exists(csvPath)) return List.of();

        try (BufferedReader br = Files.newBufferedReader(csvPath)) {
            String header = br.readLine();
            if (header == null) return List.of();

            List<TimeLog> result = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] c = line.split(",", -1);

                // Employee #,Last Name,First Name,Date,Log In,Log Out
                String empNo = get(c, 0);
                String last = get(c, 1);
                String first = get(c, 2);
                LocalDate date = parseDate(get(c, 3));
                LocalTime in = parseTime(get(c, 4));
                LocalTime out = parseTime(get(c, 5));

                if (!empNo.isBlank() && date != null) {
                    result.add(new TimeLog(empNo, last, first, date, in, out));
                }
            }
            return result;

        } catch (IOException e) {
            throw new RuntimeException("Failed to read: " + csvPath, e);
        }
    }

    @Override
    public void upsertTimeIn(String employeeNo, String lastName, String firstName, LocalDate date, LocalTime timeIn) {
        ensureFileWithHeader();

        List<String> lines = readAllLinesSafe();
        String header = lines.get(0);
        List<String> body = new ArrayList<>(lines.subList(1, lines.size()));

        boolean updated = false;
        for (int i = 0; i < body.size(); i++) {
            String[] c = body.get(i).split(",", -1);
            if (matchRow(c, employeeNo, date)) {
                // set log in (col 4)
                c = ensureLen(c, 6);
                if (c[4] == null || c[4].isBlank()) {
                    c[4] = TIME_FMT.format(timeIn);
                } else {
                    // already timed in - keep existing
                }
                // optionally update names if empty
                if (c[1].isBlank()) c[1] = safe(lastName);
                if (c[2].isBlank()) c[2] = safe(firstName);

                body.set(i, String.join(",", c));
                updated = true;
                break;
            }
        }

        if (!updated) {
            String row = String.join(",",
                    safe(employeeNo),
                    safe(lastName),
                    safe(firstName),
                    DATE_FMT.format(date),
                    TIME_FMT.format(timeIn),
                    ""
            );
            body.add(row);
        }

        writeAll(header, body);
    }

    @Override
    public void upsertTimeOut(String employeeNo, LocalDate date, LocalTime timeOut) {
        ensureFileWithHeader();

        List<String> lines = readAllLinesSafe();
        String header = lines.get(0);
        List<String> body = new ArrayList<>(lines.subList(1, lines.size()));

        boolean updated = false;
        for (int i = 0; i < body.size(); i++) {
            String[] c = body.get(i).split(",", -1);
            if (matchRow(c, employeeNo, date)) {
                c = ensureLen(c, 6);
                c[5] = TIME_FMT.format(timeOut); // set log out
                body.set(i, String.join(",", c));
                updated = true;
                break;
            }
        }

        if (!updated) {
            // if no row exists yet, create one with empty names + empty log in
            String row = String.join(",",
                    safe(employeeNo),
                    "",
                    "",
                    DATE_FMT.format(date),
                    "",
                    TIME_FMT.format(timeOut)
            );
            body.add(row);
        }

        writeAll(header, body);
    }

    // ---------------- helpers ----------------

    private boolean matchRow(String[] c, String empNo, LocalDate date) {
        String rowEmp = get(c, 0);
        LocalDate rowDate = parseDate(get(c, 3));
        return rowEmp.equals(empNo) && Objects.equals(rowDate, date);
    }

    private void ensureFileWithHeader() {
        try {
            if (!Files.exists(csvPath)) {
                Files.createDirectories(csvPath.getParent());
                Files.writeString(csvPath, "Employee #,Last Name,First Name,Date,Log In,Log Out\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to init file: " + csvPath, e);
        }
    }

    private List<String> readAllLinesSafe() {
        try {
            return Files.readAllLines(csvPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read: " + csvPath, e);
        }
    }

    private void writeAll(String header, List<String> body) {
        try {
            List<String> out = new ArrayList<>();
            out.add(header);
            out.addAll(body);
            Files.write(csvPath, out);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write: " + csvPath, e);
        }
    }

    private String[] ensureLen(String[] c, int len) {
        if (c.length >= len) return c;
        String[] n = new String[len];
        System.arraycopy(c, 0, n, 0, c.length);
        for (int i = c.length; i < len; i++) n[i] = "";
        return n;
    }

    private String get(String[] c, int idx) {
        if (c == null || idx < 0 || idx >= c.length) return "";
        return c[idx] == null ? "" : c[idx].trim();
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s.trim(), DATE_FMT); } catch (Exception e) { return null; }
    }

    private LocalTime parseTime(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalTime.parse(s.trim(), TIME_FMT); } catch (Exception e) { return null; }
    }

    private String safe(String s) {
        return s == null ? "" : s.replace(",", " ").trim();
    }
}