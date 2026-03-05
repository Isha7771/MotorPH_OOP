/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.hr.service;

import com.mycompany.oopmotorph.employee.model.EmployeeRecord;
import com.mycompany.oopmotorph.employee.repository.EmployeeRepository;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class HrEmployeeManagementService {

    private final EmployeeRepository repo;

    public HrEmployeeManagementService(EmployeeRepository repo) {
        this.repo = repo;
    }

    public List<EmployeeRecord> listAll() throws IOException {
        return repo.findAll();
    }

    public List<EmployeeRecord> search(String keyword) throws IOException {
        String q = (keyword == null) ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty()) return repo.findAll();

        return repo.findAll().stream()
                .filter(e -> contains(e.getEmployeeNo(), q)
                        || contains(e.getLastName(), q)
                        || contains(e.getFirstName(), q)
                        || contains(e.getPosition(), q)
                        || contains(e.getStatus(), q))
                .collect(Collectors.toList());
    }

    public void add(EmployeeRecord incoming) throws IOException {
        validateHr(incoming);

        Optional<EmployeeRecord> existing = repo.findByEmployeeNo(incoming.getEmployeeNo());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Employee # already exists: " + incoming.getEmployeeNo());
        }

        // HR DOES NOT manage these 3 -> default to 0 on add (safe)
        incoming.setRiceSubsidy(0);
        incoming.setPhoneAllowance(0);
        incoming.setClothingAllowance(0);

        repo.add(incoming);
    }

    /**
     * HR Update: overwrite HR fields + gross/hourly,
     * but preserve Rice/Phone/Clothing allowances (payroll-owned).
     */
    public void updateHr(EmployeeRecord incoming) throws IOException {
        validateHr(incoming);

        EmployeeRecord existing = repo.findByEmployeeNo(incoming.getEmployeeNo())
                .orElseThrow(() -> new IllegalArgumentException("Employee # not found: " + incoming.getEmployeeNo()));

        // HR-managed fields
        existing.setLastName(incoming.getLastName());
        existing.setFirstName(incoming.getFirstName());
        existing.setBirthday(incoming.getBirthday());
        existing.setAddress(incoming.getAddress());
        existing.setPhoneNumber(incoming.getPhoneNumber());

        existing.setSssNo(incoming.getSssNo());
        existing.setPhilhealthNo(incoming.getPhilhealthNo());
        existing.setTinNo(incoming.getTinNo());
        existing.setPagibigNo(incoming.getPagibigNo());

        existing.setStatus(incoming.getStatus());
        existing.setPosition(incoming.getPosition());
        existing.setImmediateSupervisor(incoming.getImmediateSupervisor());

        // include rates (as you decided)
        existing.setGrossSemiMonthlyRate(incoming.getGrossSemiMonthlyRate());
        existing.setHourlyRate(incoming.getHourlyRate());

        // optional: allow HR to manage basic salary (keep if you want)
        existing.setBasicSalary(incoming.getBasicSalary());

        // IMPORTANT: do NOT touch rice/phone/clothing here
        repo.update(existing);
    }

    public void delete(String employeeNo) throws IOException {
        if (employeeNo == null || employeeNo.isBlank()) {
            throw new IllegalArgumentException("Employee # is required.");
        }
        repo.delete(employeeNo.trim());
    }

    /**
     * Generates the next Employee # based on the current maximum numeric Employee # in the CSV.
     * Example: if max is 10034, returns 10035.
     */
    public String nextEmployeeNo() throws IOException {
        int max = 0;
        for (EmployeeRecord e : repo.findAll()) {
            String raw = (e == null) ? null : e.getEmployeeNo();
            if (raw == null) continue;
            String s = raw.trim();
            if (!s.matches("\\d+")) continue;
            try {
                int n = Integer.parseInt(s);
                if (n > max) max = n;
            } catch (NumberFormatException ignore) {
                // skip non-int employee numbers
            }
        }
        return String.valueOf(max + 1);
    }

    // ---------------- validation ----------------
    private void validateHr(EmployeeRecord e) {
        if (e == null) throw new IllegalArgumentException("Employee record is required.");
        if (isBlank(e.getEmployeeNo())) throw new IllegalArgumentException("Employee # is required.");
        if (isBlank(e.getLastName())) throw new IllegalArgumentException("Last Name is required.");
        if (isBlank(e.getFirstName())) throw new IllegalArgumentException("First Name is required.");
        if (e.getBirthday() == null) throw new IllegalArgumentException("Birthday is required.");
        if (isBlank(e.getStatus())) throw new IllegalArgumentException("Status is required.");

        String s = e.getStatus().trim().toLowerCase(Locale.ROOT);
        if (!s.equals("regular") && !s.equals("probation") && !s.equals("probationary")) {
            throw new IllegalArgumentException("Status must be Regular or Probationary only.");
        }

        if (e.getGrossSemiMonthlyRate() < 0) throw new IllegalArgumentException("Gross Semi-monthly Rate cannot be negative.");
        if (e.getHourlyRate() < 0) throw new IllegalArgumentException("Hourly Rate cannot be negative.");
        if (e.getBasicSalary() < 0) throw new IllegalArgumentException("Basic Salary cannot be negative.");
    }

    private boolean contains(String field, String q) {
        return field != null && field.toLowerCase(Locale.ROOT).contains(q);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
