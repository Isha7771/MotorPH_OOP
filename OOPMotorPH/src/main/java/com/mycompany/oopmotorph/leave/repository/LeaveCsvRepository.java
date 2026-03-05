/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.leave.repository;

import com.mycompany.oopmotorph.leave.model.LeaveRequest;
import com.mycompany.oopmotorph.leave.model.LeaveStatus;
import com.mycompany.oopmotorph.leave.model.LeaveType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LeaveCsvRepository implements LeaveRepository {

    private final Path csvPath;

    private static final String HEADER =
            "Request ID,Employee Name,Date Request,Leave Type,Date Filed,Start Date,End Date,Status,View";

    public LeaveCsvRepository(Path csvPath) {
        this.csvPath = csvPath;
        ensureFile();
    }

    @Override
    public List<LeaveRequest> findAll() {
        ensureFile();
        List<LeaveRequest> out = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",", -1);
                if (p.length < 8) continue;

                String requestId   = safe(p, 0);
                String employee    = safe(p, 1);
                String dateRequest = safe(p, 2);
                LeaveType type     = LeaveType.fromString(safe(p, 3));
                String dateFiled   = safe(p, 4);
                String startDate   = safe(p, 5);
                String endDate     = safe(p, 6);
                LeaveStatus status = LeaveStatus.fromString(safe(p, 7));

                out.add(new LeaveRequest(
                        requestId,
                        employee,
                        dateRequest,
                        type,
                        dateFiled,
                        startDate,
                        endDate,
                        status
                ));
            }

        } catch (Exception e) {
            throw new RuntimeException("Error reading Leave.csv: " + e.getMessage(), e);
        }

        return out;
    }

    @Override
    public Optional<LeaveRequest> findById(String requestId) {
        if (requestId == null) return Optional.empty();
        return findAll().stream()
                .filter(r -> requestId.equalsIgnoreCase(r.getRequestId()))
                .findFirst();
    }

    @Override
    public void saveAll(List<LeaveRequest> requests) {
        ensureFile();

        try (BufferedWriter bw = Files.newBufferedWriter(csvPath, StandardCharsets.UTF_8)) {
            bw.write(HEADER);
            bw.newLine();

            for (LeaveRequest r : requests) {
                String row = String.join(",",
                        csv(r.getRequestId()),
                        csv(r.getEmployeeName()),
                        csv(r.getDateRequest()),
                        csv(toCsvLeaveType(r.getLeaveType())),
                        csv(r.getDateFiled()),
                        csv(r.getStartDate()),
                        csv(r.getEndDate()),
                        csv(r.getStatus().name()),
                        ""
                );
                bw.write(row);
                bw.newLine();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error writing Leave.csv: " + e.getMessage(), e);
        }
    }

    @Override
    public void add(LeaveRequest request) {
        List<LeaveRequest> all = findAll();
        all.add(request);
        saveAll(all);
    }

    @Override
    public List<LeaveRequest> findByEmployeeNo(String employeeKey) {
        String key = employeeKey == null ? "" : employeeKey.trim().toLowerCase();
        return findAll().stream()
                .filter(r -> {
                    String name = r.getEmployeeName() == null ? "" : r.getEmployeeName().trim().toLowerCase();
                    return key.isEmpty() || name.contains(key);
                })
                .collect(Collectors.toList());
    }

    @Override
    public void append(LeaveRequest request) {
        add(request);
    }

    private void ensureFile() {
        try {
            if (Files.notExists(csvPath)) {
                Files.createDirectories(csvPath.getParent());
                Files.writeString(csvPath, HEADER + System.lineSeparator(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot create Leave.csv: " + e.getMessage(), e);
        }
    }

    private static String safe(String[] p, int idx) {
        if (idx < 0 || idx >= p.length) return "";
        return p[idx] == null ? "" : p[idx].trim();
    }

    private static String csv(String s) {
        if (s == null) return "";
        return s.replace("\n", " ").replace("\r", " ").trim();
    }

    private static String toCsvLeaveType(LeaveType type) {
        if (type == LeaveType.SICK) return "Sick Leave";
        return "Vacation Type";
    }
}
