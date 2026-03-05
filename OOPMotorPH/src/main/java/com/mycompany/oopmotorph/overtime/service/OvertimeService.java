/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.overtime.service;

import com.mycompany.oopmotorph.overtime.model.OvertimeRequest;
import com.mycompany.oopmotorph.overtime.model.OvertimeStatus;
import com.mycompany.oopmotorph.overtime.repository.OvertimeRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OvertimeService {

    private final OvertimeRepository repo;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("M/d/yyyy");

    public OvertimeService(OvertimeRepository repo) {
        this.repo = repo;
    }

    public List<OvertimeRequest> search(String keyword, Optional<OvertimeStatus> statusFilter) {
        String kw = keyword == null ? "" : keyword.trim().toLowerCase();

        List<OvertimeRequest> all = repo.findAll();
        List<OvertimeRequest> out = new ArrayList<>();

        for (OvertimeRequest r : all) {
            String id = safeLower(r.getRequestId());
            String name = safeLower(r.getEmployeeName());

            boolean matchKw =
                    kw.isEmpty()
                            || id.contains(kw)
                            || name.contains(kw);

            boolean matchStatus =
                    statusFilter.isEmpty() || r.getStatus() == statusFilter.get();

            if (matchKw && matchStatus) out.add(r);
        }
        return out;
    }

    public Optional<OvertimeRequest> findById(String requestId) {
        if (isBlank(requestId)) return Optional.empty();
        return repo.findById(requestId.trim());
    }

    public List<OvertimeRequest> getEmployeeOvertimes(String employeeName) {
        return repo.findByEmployeeNo(employeeName);
    }

    // ---------------- ENCAPSULATED STATUS UPDATES ----------------

    public void approve(String requestId) {
        setStatusOrThrow(requestId, OvertimeStatus.APPROVED);
    }

    public void reject(String requestId) {
        setStatusOrThrow(requestId, OvertimeStatus.REJECTED);
    }

    public void setStatus(String requestId, OvertimeStatus newStatus) {
        setStatusOrThrow(requestId, newStatus);
    }

    private void setStatusOrThrow(String requestId, OvertimeStatus newStatus) {
        if (isBlank(requestId)) throw new IllegalArgumentException("Request ID is required.");
        if (newStatus == null) throw new IllegalArgumentException("Status is required.");

        List<OvertimeRequest> all = repo.findAll();
        boolean updated = false;

        for (OvertimeRequest r : all) {
            if (requestId.trim().equalsIgnoreCase(safe(r.getRequestId()))) {

                r.setStatus(newStatus);
                updated = true;
                break;
            }
        }

        if (!updated) {
            throw new IllegalArgumentException("Overtime request not found: " + requestId);
        }

        repo.saveAll(all);
    }

    public void createRequest(OvertimeRequest request) {
        validateRequestForCreate(request);
        repo.add(request);
    }

    public OvertimeRequest submitEmployeeOvertime(String employeeName,
                                                  LocalDate overtimeDate,
                                                  int hours,
                                                  String reason) {

        validateEmployeeSubmit(employeeName, overtimeDate, hours);

        String requestId = generateNextRequestId();
        String date = overtimeDate.format(fmt);
        String hoursStr = hours + " Hours";
        String r = reason == null ? "" : reason.trim();

        OvertimeRequest req = new OvertimeRequest(
                requestId,
                employeeName.trim(),
                date,
                hoursStr,
                OvertimeStatus.PENDING,
                r
        );

        repo.append(req);
        return req;
    }

    private void validateEmployeeSubmit(String employeeName,
                                        LocalDate overtimeDate,
                                        int hours) {

        if (isBlank(employeeName)) throw new IllegalArgumentException("Employee name is required.");
        if (overtimeDate == null) throw new IllegalArgumentException("Overtime date is required.");
        if (hours <= 0) throw new IllegalArgumentException("Hours must be greater than 0.");
    }

    private void validateRequestForCreate(OvertimeRequest r) {
        if (r == null) throw new IllegalArgumentException("Overtime request is required.");
        if (isBlank(r.getRequestId())) throw new IllegalArgumentException("Request ID is required.");
        if (isBlank(r.getEmployeeName())) throw new IllegalArgumentException("Employee name is required.");
        if (isBlank(r.getDate())) throw new IllegalArgumentException("Date is required.");
        if (isBlank(r.getHours())) throw new IllegalArgumentException("Hours is required.");
        if (r.getStatus() == null) throw new IllegalArgumentException("Status is required.");
    }


    public String generateNextRequestId() {
        List<OvertimeRequest> all = repo.findAll();
        int max = 0;

        for (OvertimeRequest r : all) {
            String id = safe(r.getRequestId());
            if (id.startsWith("OT-")) {
                try {
                    int n = Integer.parseInt(id.substring(3));
                    if (n > max) max = n;
                } catch (Exception ignored) { }
            }
        }
        return String.format("OT-%04d", max + 1);
    }


    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private static String safeLower(String s) {
        return safe(s).toLowerCase();
    }
}