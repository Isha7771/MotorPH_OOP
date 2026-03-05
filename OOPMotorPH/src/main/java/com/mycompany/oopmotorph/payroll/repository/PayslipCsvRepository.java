/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.payroll.repository;

import com.mycompany.oopmotorph.common.CsvUtils;
import com.mycompany.oopmotorph.payroll.model.PayslipKey;
import com.mycompany.oopmotorph.payroll.model.PayslipRow;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PayslipCsvRepository implements PayslipRepository {

    private final Path csvPath;

    private static final String HEADER = "PayslipID,EmployeeNo,EmployeeName,Pay Date,Status,Action";

    public PayslipCsvRepository(Path csvPath) {
        this.csvPath = csvPath;
    }

    public List<PayslipRow> findAll() throws IOException {
        ensureFile();

        List<String> lines = Files.readAllLines(csvPath);
        List<PayslipRow> out = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line == null) continue;
            line = line.trim();
            if (line.isEmpty()) continue;

            if (i == 0 && line.toLowerCase().startsWith("payslipid")) continue;

            String[] p = CsvUtils.splitCsvLine(line);

            String id = get(p, 0);
            String empNo = get(p, 1);
            String empName = get(p, 2);
            String payDate = get(p, 3);

            String status = get(p, 4);
            if (status.isBlank()) status = "GENERATED";

            String action = get(p, 5);

            out.add(new PayslipRow(id, empNo, empName, payDate, status, action));
        }

        return out;
    }

    public List<PayslipRow> findByEmployeeNo(String employeeNo) throws IOException {
        String key = employeeNo == null ? "" : employeeNo.trim().toLowerCase();
        return findAll().stream()
                .filter(r -> {
                    String emp = r.getEmployeeNo() == null ? "" : r.getEmployeeNo().trim().toLowerCase();
                    return key.isEmpty() || emp.equals(key);
                })
                .collect(Collectors.toList());
    }

    public void appendAll(List<PayslipRow> rows) throws IOException {
        if (rows == null || rows.isEmpty()) return;

        ensureFile();

        try (BufferedWriter bw = Files.newBufferedWriter(csvPath, StandardOpenOption.APPEND)) {
            for (PayslipRow r : rows) {
                bw.write(toCsvLine(r));
                bw.newLine();
            }
        }
    }

    public String nextPayslipId() throws IOException {
        int max = 0;

        for (PayslipRow r : findAll()) {
            String id = (r.getPayslipId() == null) ? "" : r.getPayslipId().trim();
            int dash = id.indexOf('-');
            if (dash >= 0 && dash + 1 < id.length()) {
                String num = id.substring(dash + 1).replaceAll("[^0-9]", "");
                if (!num.isEmpty()) {
                    try {
                        max = Math.max(max, Integer.parseInt(num));
                    } catch (Exception ignored) { }
                }
            }
        }

        return "P-" + String.format("%04d", max + 1);
    }

    public void updateStatus(String payslipId, String newStatus, String action) throws IOException {
        List<PayslipRow> rows = findAll();

        boolean foundAny = false;
        for (PayslipRow r : rows) {
            if (equalsIgnoreCaseSafe(r.getPayslipId(), payslipId)) {
                r.setStatus(newStatus);
                r.setAction(action);
                foundAny = true;
            }
        }

        if (!foundAny) throw new IllegalArgumentException("Payslip ID not found: " + payslipId);

        writeAll(rows);
    }

    public void updateStatus(String payslipId, String employeeNo, String newStatus, String action) throws IOException {
        List<PayslipRow> rows = findAll();

        boolean found = false;
        for (PayslipRow r : rows) {
            if (equalsIgnoreCaseSafe(r.getPayslipId(), payslipId)
                    && equalsIgnoreCaseSafe(r.getEmployeeNo(), employeeNo)) {
                r.setStatus(newStatus);
                r.setAction(action);
                found = true;
                break; // 
            }
        }

        if (!found) {
            throw new IllegalArgumentException(
                    "Payslip row not found: payslipId=" + payslipId + ", employeeNo=" + employeeNo
            );
        }

        writeAll(rows);
    }
    
    public void updateStatusBulk(List<PayslipKey> keys, String newStatus, String action) throws IOException {
        if (keys == null || keys.isEmpty()) return;

        for (PayslipKey k : keys) {
            if (k == null) continue;
            updateStatus(k.getPayslipId(), k.getEmployeeNo(), newStatus, action);
        }
    }

    private void writeAll(List<PayslipRow> rows) throws IOException {
        ensureParentDir();

        try (BufferedWriter bw = Files.newBufferedWriter(csvPath)) {
            bw.write(HEADER);
            bw.newLine();

            for (PayslipRow r : rows) {
                bw.write(toCsvLine(r));
                bw.newLine();
            }
        }
    }

    private String toCsvLine(PayslipRow r) {
        return String.join(",",
                CsvUtils.escapeCsv(nz(r.getPayslipId())),
                CsvUtils.escapeCsv(nz(r.getEmployeeNo())),
                CsvUtils.escapeCsv(nz(r.getEmployeeName())),
                CsvUtils.escapeCsv(nz(r.getPayDate())),
                CsvUtils.escapeCsv(nz(defaultStatus(r.getStatus()))),
                CsvUtils.escapeCsv(nz(r.getAction()))
        );
    }

    private String defaultStatus(String status) {
        if (status == null || status.trim().isEmpty()) return "GENERATED";
        return status.trim();
    }

    private void ensureFile() throws IOException {
        ensureParentDir();

        if (!Files.exists(csvPath)) {
            Files.writeString(csvPath, HEADER + System.lineSeparator(), StandardOpenOption.CREATE);
            return;
        }

        if (Files.size(csvPath) == 0) {
            Files.writeString(csvPath, HEADER + System.lineSeparator(), StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    private void ensureParentDir() throws IOException {
        Path parent = csvPath.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }

    private static String get(String[] p, int idx) {
        return (p != null && idx >= 0 && idx < p.length) ? CsvUtils.unquote(p[idx]).trim() : "";
    }

    private static String nz(String s) {
        return (s == null) ? "" : s.trim();
    }

    private static boolean equalsIgnoreCaseSafe(String a, String b) {
        String x = (a == null) ? "" : a.trim();
        String y = (b == null) ? "" : b.trim();
        return !x.isEmpty() && !y.isEmpty() && x.equalsIgnoreCase(y);
    }
}