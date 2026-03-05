/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.leave.service;

import com.mycompany.oopmotorph.leave.model.LeaveRequest;
import com.mycompany.oopmotorph.leave.model.LeaveStatus;
import com.mycompany.oopmotorph.leave.model.LeaveType;
import com.mycompany.oopmotorph.leave.repository.LeaveRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LeaveService {

    private final LeaveRepository repo;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("M/d/yyyy");

    public LeaveService(LeaveRepository repo) {
        this.repo = repo;
    }


    public List<LeaveRequest> search(String keyword, Optional<LeaveStatus> statusFilter) {
        String kw = keyword == null ? "" : keyword.trim().toLowerCase();

        List<LeaveRequest> all = repo.findAll();
        List<LeaveRequest> out = new ArrayList<>();

        for (LeaveRequest r : all) {
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

    public Optional<LeaveRequest> findById(String requestId) {
        if (isBlank(requestId)) return Optional.empty();
        return repo.findById(requestId.trim());
    }

    public List<LeaveRequest> getEmployeeLeaves(String employeeName) {
        return repo.findByEmployeeNo(employeeName);
    }

    // ---------------- ENCAPSULATED STATUS UPDATES ----------------

    public void approve(String requestId) {
        setStatusOrThrow(requestId, LeaveStatus.APPROVED);
    }

    public void reject(String requestId) {
        setStatusOrThrow(requestId, LeaveStatus.REJECTED);
    }

    public void setStatus(String requestId, LeaveStatus newStatus) {
        setStatusOrThrow(requestId, newStatus);
    }

    private void setStatusOrThrow(String requestId, LeaveStatus newStatus) {
        if (isBlank(requestId)) throw new IllegalArgumentException("Request ID is required.");
        if (newStatus == null) throw new IllegalArgumentException("Status is required.");

        List<LeaveRequest> all = repo.findAll();
        boolean updated = false;

        for (LeaveRequest r : all) {
            if (requestId.trim().equalsIgnoreCase(safe(r.getRequestId()))) {

                r.setStatus(newStatus);
                updated = true;
                break;
            }
        }

        if (!updated) {
            throw new IllegalArgumentException("Leave request not found: " + requestId);
        }

        repo.saveAll(all);
    }


    public void createRequest(LeaveRequest request) {
        validateRequestForCreate(request);
        repo.add(request);
    }

    public LeaveRequest submitEmployeeLeave(String employeeName,
                                           LeaveType leaveType,
                                           LocalDate startDate,
                                           LocalDate endDate) {

        validateEmployeeSubmit(employeeName, leaveType, startDate, endDate);

        String requestId = generateNextRequestId();
        String dateRequest = LocalDate.now().format(fmt);
        String dateFiled = LocalDate.now().format(fmt);

        LeaveRequest req = new LeaveRequest(
                requestId,
                employeeName.trim(),
                dateRequest,
                leaveType,
                dateFiled,
                startDate.format(fmt),
                endDate.format(fmt),
                LeaveStatus.PENDING
        );

        repo.append(req);
        return req;
    }

    private void validateEmployeeSubmit(String employeeName,
                                        LeaveType leaveType,
                                        LocalDate startDate,
                                        LocalDate endDate) {

        if (isBlank(employeeName)) throw new IllegalArgumentException("Employee name is required.");
        if (leaveType == null) throw new IllegalArgumentException("Leave type is required.");
        if (startDate == null || endDate == null) throw new IllegalArgumentException("Start/End date is required.");
        if (endDate.isBefore(startDate)) throw new IllegalArgumentException("End date cannot be before start date.");
    }

    private void validateRequestForCreate(LeaveRequest r) {
        if (r == null) throw new IllegalArgumentException("Leave request is required.");
        if (isBlank(r.getRequestId())) throw new IllegalArgumentException("Request ID is required.");
        if (isBlank(r.getEmployeeName())) throw new IllegalArgumentException("Employee name is required.");
        if (r.getLeaveType() == null) throw new IllegalArgumentException("Leave type is required.");
        if (isBlank(r.getStartDate()) || isBlank(r.getEndDate()))
            throw new IllegalArgumentException("Start/End date is required.");
        if (r.getStatus() == null) throw new IllegalArgumentException("Status is required.");
    }


    public String generateNextRequestId() {
        List<LeaveRequest> all = repo.findAll();
        int max = 0;

        for (LeaveRequest r : all) {
            String id = safe(r.getRequestId());
            if (id.startsWith("L-")) {
                try {
                    int n = Integer.parseInt(id.substring(2));
                    if (n > max) max = n;
                } catch (Exception ignored) { }
            }
        }
        return String.format("L-%04d", max + 1);
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