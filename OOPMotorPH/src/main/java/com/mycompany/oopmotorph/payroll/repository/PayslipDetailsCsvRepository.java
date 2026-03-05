/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.payroll.repository;

import com.mycompany.oopmotorph.common.CsvUtils;
import com.mycompany.oopmotorph.payroll.model.PayslipDetails;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.APPEND;

public class PayslipDetailsCsvRepository implements PayslipDetailsRepository {

    private final Path csvPath;

    private static final String HEADER =
            "PayslipID,EmployeeNo,PeriodFrom,PeriodTo,TotalHours,Gross,SSS,PhilHealth,Pagibig,Tax,TotalDeductions,NetPay";

    public PayslipDetailsCsvRepository(Path csvPath) {
        this.csvPath = csvPath;
    }

    public List<PayslipDetails> findAll() throws IOException {
        ensureFile();

        List<String> lines = Files.readAllLines(csvPath);
        List<PayslipDetails> out = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line == null) continue;
            line = line.trim();
            if (line.isEmpty()) continue;

            if (i == 0 && line.toLowerCase().startsWith("payslipid")) continue;

            String[] p = CsvUtils.splitCsvLine(line);

            String payslipId = get(p, 0);
            String employeeNo = get(p, 1);
            String periodFrom = get(p, 2);
            String periodTo = get(p, 3);

            double totalHours = toDouble(get(p, 4));
            double gross = toDouble(get(p, 5));
            double sss = toDouble(get(p, 6));
            double philHealth = toDouble(get(p, 7));
            double pagibig = toDouble(get(p, 8));
            double tax = toDouble(get(p, 9));
            double totalDeductions = toDouble(get(p, 10));
            double netPay = toDouble(get(p, 11));

            PayslipDetails d = new PayslipDetails();
            d.setPayslipId(payslipId);
            d.setEmployeeNo(employeeNo);
            d.setPeriodFrom(periodFrom);
            d.setPeriodTo(periodTo);
            d.setTotalHours(totalHours);
            d.setGross(gross);
            d.setSss(sss);
            d.setPhilHealth(philHealth);
            d.setPagibig(pagibig);
            d.setTax(tax);
            d.setTotalDeductions(totalDeductions);
            d.setNetPay(netPay);

            out.add(d);
        }

        return out;
    }

    public Optional<PayslipDetails> findByPayslipId(String payslipId) throws IOException {
        if (payslipId == null || payslipId.trim().isEmpty()) return Optional.empty();
        String key = payslipId.trim().toLowerCase();

        for (PayslipDetails d : findAll()) {
            String id = d.getPayslipId() == null ? "" : d.getPayslipId().trim().toLowerCase();
            if (id.equals(key)) return Optional.of(d);
        }
        return Optional.empty();
    }

    public void appendAll(List<PayslipDetails> rows) throws IOException {
        ensureFile();
        try (BufferedWriter bw = Files.newBufferedWriter(csvPath, APPEND)) {
            for (PayslipDetails r : rows) {
                bw.write(toCsv(r));
                bw.newLine();
            }
        }
    }

    private void ensureFile() throws IOException {
        if (!Files.exists(csvPath)) {
            Files.createDirectories(csvPath.getParent());
            try (BufferedWriter bw = Files.newBufferedWriter(csvPath)) {
                bw.write(HEADER);
                bw.newLine();
            }
            return;
        }

        if (Files.size(csvPath) == 0) {
            Files.writeString(csvPath, HEADER + System.lineSeparator());
        }
    }

    private String toCsv(PayslipDetails r) {
        return String.join(",",
                CsvUtils.escapeCsv(nz(r.getPayslipId())),
                CsvUtils.escapeCsv(nz(r.getEmployeeNo())),
                CsvUtils.escapeCsv(nz(r.getPeriodFrom())),
                CsvUtils.escapeCsv(nz(r.getPeriodTo())),
                String.valueOf(r.getTotalHours()),
                String.valueOf(r.getGross()),
                String.valueOf(r.getSss()),
                String.valueOf(r.getPhilHealth()),
                String.valueOf(r.getPagibig()),
                String.valueOf(r.getTax()),
                String.valueOf(r.getTotalDeductions()),
                String.valueOf(r.getNetPay())
        );
    }

    private static String get(String[] p, int idx) {
        return (p != null && idx >= 0 && idx < p.length) ? CsvUtils.unquote(p[idx]).trim() : "";
    }

    private static double toDouble(String s) {
        if (s == null) return 0;
        String x = s.trim();
        if (x.isEmpty()) return 0;
        try { return Double.parseDouble(x); } catch (Exception e) { return 0; }
    }

    private static String nz(String s) {
        return s == null ? "" : s.trim();
    }
}