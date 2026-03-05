/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.employee.repository;

import com.mycompany.oopmotorph.common.CsvUtils;
import com.mycompany.oopmotorph.employee.model.EmployeeRecord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EmployeeCsvRepository implements EmployeeRepository {

    private final Path csvPath;
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("M/d/yyyy");

    // ✅ UPDATED HEADER (NO rice/phone/clothing)
    private static final String HEADER =
            "Employee #,Last Name,First Name,Birthday,Address,Phone Number,SSS #,Philhealth #,TIN #,Pag-ibig #," +
            "Status,Position,Immediate Supervisor,Basic Salary,Gross Semi-monthly Rate,Hourly Rate";

    public EmployeeCsvRepository(Path csvPath) {
        this.csvPath = csvPath;
    }

    @Override
    public List<EmployeeRecord> findAll() throws IOException {
        if (!Files.exists(csvPath)) return Collections.emptyList();

        try (BufferedReader br = Files.newBufferedReader(csvPath)) {
            String headerLine = br.readLine();
            if (headerLine == null) return Collections.emptyList();

            String[] headers = CsvUtils.splitCsvLine(headerLine);
            Map<String, Integer> idx = indexMap(headers);

            List<EmployeeRecord> out = new ArrayList<>();
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] cells = CsvUtils.splitCsvLine(line);

                EmployeeRecord r = new EmployeeRecord();
                r.setEmployeeNo(get(cells, idx, "Employee #"));
                r.setLastName(get(cells, idx, "Last Name"));
                r.setFirstName(get(cells, idx, "First Name"));

                r.setBirthday(parseDate(get(cells, idx, "Birthday")));

                r.setAddress(get(cells, idx, "Address"));
                r.setPhoneNumber(get(cells, idx, "Phone Number"));
                r.setSssNo(get(cells, idx, "SSS #"));
                r.setPhilhealthNo(get(cells, idx, "Philhealth #"));
                r.setTinNo(get(cells, idx, "TIN #"));
                r.setPagibigNo(get(cells, idx, "Pag-ibig #"));

                r.setStatus(get(cells, idx, "Status"));
                r.setPosition(get(cells, idx, "Position"));
                r.setImmediateSupervisor(get(cells, idx, "Immediate Supervisor"));

                r.setBasicSalary(parseMoney(get(cells, idx, "Basic Salary")));

                // ✅ ONLY include these in HR + Payroll
                r.setGrossSemiMonthlyRate(parseMoney(get(cells, idx, "Gross Semi-monthly Rate")));
                r.setHourlyRate(parseMoney(get(cells, idx, "Hourly Rate")));

                // NOTE: If old CSV still contains rice/phone/clothing columns,
                // we safely ignore them. If other modules still rely on them,
                // they should be updated to not use them.

                out.add(r);
            }

            return out;
        }
    }

    @Override
    public Optional<EmployeeRecord> findByEmployeeNo(String employeeNo) throws IOException {
        if (employeeNo == null) return Optional.empty();
        String key = employeeNo.trim();

        for (EmployeeRecord r : findAll()) {
            if (r.getEmployeeNo() != null && r.getEmployeeNo().trim().equalsIgnoreCase(key)) {
                return Optional.of(r);
            }
        }
        return Optional.empty();
    }

    @Override
    public void add(EmployeeRecord employee) throws IOException {
        ensureFileExists();

        String empNo = safe(employee.getEmployeeNo());
        if (empNo.isEmpty()) throw new IllegalArgumentException("Employee # is required.");

        List<EmployeeRecord> all = findAll();
        for (EmployeeRecord r : all) {
            if (safe(r.getEmployeeNo()).equalsIgnoreCase(empNo)) {
                throw new IllegalArgumentException("Employee # already exists: " + empNo);
            }
        }

        all.add(employee);
        writeAll(all);
    }

    @Override
    public void update(EmployeeRecord employee) throws IOException {
        ensureFileExists();

        String empNo = safe(employee.getEmployeeNo());
        if (empNo.isEmpty()) throw new IllegalArgumentException("Employee # is required.");

        List<EmployeeRecord> all = findAll();

        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (safe(all.get(i).getEmployeeNo()).equalsIgnoreCase(empNo)) {
                all.set(i, employee);
                found = true;
                break;
            }
        }

        if (!found) throw new IllegalArgumentException("Employee # not found: " + empNo);

        writeAll(all);
    }

    @Override
    public void delete(String employeeNo) throws IOException {
        ensureFileExists();

        String key = safe(employeeNo);
        if (key.isEmpty()) throw new IllegalArgumentException("Employee # is required.");

        List<EmployeeRecord> all = findAll();
        boolean removed = all.removeIf(e -> safe(e.getEmployeeNo()).equalsIgnoreCase(key));

        if (!removed) throw new IllegalArgumentException("Employee # not found: " + key);

        writeAll(all);
    }

    // ---------- write ----------
    private void writeAll(List<EmployeeRecord> rows) throws IOException {
        Files.createDirectories(csvPath.getParent());

        try (BufferedWriter bw = Files.newBufferedWriter(csvPath)) {
            bw.write(HEADER);
            bw.newLine();

            for (EmployeeRecord r : rows) {
                bw.write(String.join(",",
                        CsvUtils.escapeCsv(safe(r.getEmployeeNo())),
                        CsvUtils.escapeCsv(safe(r.getLastName())),
                        CsvUtils.escapeCsv(safe(r.getFirstName())),
                        CsvUtils.escapeCsv(formatDate(r.getBirthday())),
                        CsvUtils.escapeCsv(safe(r.getAddress())),
                        CsvUtils.escapeCsv(safe(r.getPhoneNumber())),
                        CsvUtils.escapeCsv(safe(r.getSssNo())),
                        CsvUtils.escapeCsv(safe(r.getPhilhealthNo())),
                        CsvUtils.escapeCsv(safe(r.getTinNo())),
                        CsvUtils.escapeCsv(safe(r.getPagibigNo())),
                        CsvUtils.escapeCsv(safe(r.getStatus())),
                        CsvUtils.escapeCsv(safe(r.getPosition())),
                        CsvUtils.escapeCsv(safe(r.getImmediateSupervisor())),
                        formatMoney(r.getBasicSalary()),
                        formatMoney(r.getGrossSemiMonthlyRate()),
                        formatMoney(r.getHourlyRate())
                ));
                bw.newLine();
            }
        }
    }

    private void ensureFileExists() throws IOException {
        if (!Files.exists(csvPath)) {
            Files.createDirectories(csvPath.getParent());
            try (BufferedWriter bw = Files.newBufferedWriter(csvPath)) {
                bw.write(HEADER);
                bw.newLine();
            }
        }
    }

    // ---------- helpers ----------
    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDate.parse(s.trim(), dateFmt);
        } catch (Exception e) {
            return null;
        }
    }

    private String formatDate(LocalDate d) {
        return (d == null) ? "" : dateFmt.format(d);
    }

    private double parseMoney(String s) {
        if (s == null || s.isBlank()) return 0.0;
        String cleaned = s.trim().replace(",", "").replace("\"", "");
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private String formatMoney(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return "0";
        return String.valueOf(v);
    }

    private Map<String, Integer> indexMap(String[] headers) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            map.put(headers[i].trim(), i);
        }
        return map;
    }

    private String get(String[] cells, Map<String, Integer> idx, String col) {
        Integer i = idx.get(col);
        if (i == null || i < 0 || i >= cells.length) return "";
        return CsvUtils.unquote(cells[i]);
    }

    private String safe(String s) {
        return (s == null) ? "" : s.trim();
    }
}