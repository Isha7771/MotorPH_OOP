/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.payroll.service;

import com.mycompany.oopmotorph.deductions.service.DeductionCalculator;
import com.mycompany.oopmotorph.employee.model.EmployeeRecord;
import com.mycompany.oopmotorph.employee.repository.EmployeeCsvRepository;
import com.mycompany.oopmotorph.payroll.model.PayslipDetails;
import com.mycompany.oopmotorph.payroll.model.PayslipRow;
import com.mycompany.oopmotorph.payroll.repository.PayslipCsvRepository;
import com.mycompany.oopmotorph.payroll.repository.PayslipDetailsCsvRepository;
import com.mycompany.oopmotorph.payrollstaff.service.PayrollAttendanceService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class PayslipGenerationService {

    private final EmployeeCsvRepository employeeRepo;
    private final PayslipCsvRepository payslipListRepo;
    private final PayslipDetailsCsvRepository payslipDetailsRepo;
    private final PayrollAttendanceService payrollAttendanceService;

    // ✅ Polymorphism-ready: keep as a list (you can explain this in Milestone 1)
    private final List<DeductionCalculator> calculators;

    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("M/d/yyyy");

    public PayslipGenerationService(EmployeeCsvRepository employeeRepo,
                                   PayslipCsvRepository payslipListRepo,
                                   PayslipDetailsCsvRepository payslipDetailsRepo,
                                   PayrollAttendanceService payrollAttendanceService,
                                   List<DeductionCalculator> calculators) {

        this.employeeRepo = Objects.requireNonNull(employeeRepo, "employeeRepo");
        this.payslipListRepo = Objects.requireNonNull(payslipListRepo, "payslipListRepo");
        this.payslipDetailsRepo = Objects.requireNonNull(payslipDetailsRepo, "payslipDetailsRepo");
        this.payrollAttendanceService = Objects.requireNonNull(payrollAttendanceService, "payrollAttendanceService");

        this.calculators = (calculators == null) ? new ArrayList<>() : new ArrayList<>(calculators);
    }

    public void generateBatch(LocalDate from, LocalDate to, LocalDate payDate, List<String> employeeNos) throws IOException {
        validateInputs(from, to, payDate, employeeNos);

        // Summarize total hours per employee for period
        Map<String, Double> hoursByEmp = payrollAttendanceService
                .summarize(from, to, "")
                .stream()
                .collect(Collectors.toMap(
                        s -> safe(s.getEmployeeId()),
                        s -> s.getTotalHours(),
                        (a, b) -> a
                ));

        // Selected employees
        Set<String> selectedIds = employeeNos.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        List<EmployeeRecord> selected = employeeRepo.findAll().stream()
                .filter(e -> e.getEmployeeNo() != null && selectedIds.contains(e.getEmployeeNo().trim()))
                .collect(Collectors.toList());

        if (selected.isEmpty()) {
            throw new IllegalArgumentException(
                    "No matching employees found in EmployeeData.csv for the selected employee numbers."
            );
        }

        List<PayslipRow> listRows = new ArrayList<>();
        List<PayslipDetails> detailRows = new ArrayList<>();

        for (EmployeeRecord e : selected) {
            String empNo = safe(e.getEmployeeNo());
            double totalHours = hoursByEmp.getOrDefault(empNo, 0.0);

            double gross = computeGross(e, totalHours);

            // ✅ Polymorphism: loop through calculators
            // We still need to assign values to PayslipDetails fields,
            // so we keep a map by calculator class name (simple + stable).
            Map<String, Double> ded = new HashMap<>();
            for (DeductionCalculator calc : calculators) {
                double val = safeCalc(calc, gross);
                ded.put(calc.getClass().getSimpleName(), val);
            }

            double sss = ded.getOrDefault("SssCalculator", 0.0);
            double phil = ded.getOrDefault("PhilHealthCalculator", 0.0);
            double pagibig = ded.getOrDefault("PagibigCalculator", 0.0);
            double tax = ded.getOrDefault("WithholdingTaxCalculator", 0.0);

            double totalDed = sss + phil + pagibig + tax;
            double net = gross - totalDed;

            String payslipId = payslipListRepo.nextPayslipId();

            PayslipRow row = new PayslipRow(
                    payslipId,
                    empNo,
                    safeName(e.getFullName()),
                    dateFmt.format(payDate),
                    "FOR_SUPERVISOR",
                    "Generated"
            );
            listRows.add(row);

            PayslipDetails d = new PayslipDetails();
            d.setPayslipId(payslipId);
            d.setEmployeeNo(empNo);
            d.setPeriodFrom(dateFmt.format(from));
            d.setPeriodTo(dateFmt.format(to));
            d.setTotalHours(round2(totalHours));
            d.setGross(round2(gross));
            d.setSss(round2(sss));
            d.setPhilHealth(round2(phil));
            d.setPagibig(round2(pagibig));
            d.setTax(round2(tax));
            d.setTotalDeductions(round2(totalDed));
            d.setNetPay(round2(net));
            detailRows.add(d);
        }

        payslipListRepo.appendAll(listRows);
        payslipDetailsRepo.appendAll(detailRows);
    }

    private void validateInputs(LocalDate from, LocalDate to, LocalDate payDate, List<String> employeeNos) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Payroll period From/To is required.");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("To date must be after (or equal to) From date.");
        }
        if (payDate == null) {
            throw new IllegalArgumentException("Pay Date is required.");
        }
        if (employeeNos == null || employeeNos.isEmpty()) {
            throw new IllegalArgumentException("Select at least one employee.");
        }
    }

    private double computeGross(EmployeeRecord e, double totalHours) {
        if (e == null) return 0.0;

        double hourly = e.getHourlyRate();
        double semi = e.getGrossSemiMonthlyRate();

        if (hourly > 0 && totalHours > 0) return hourly * totalHours;
        if (semi > 0) return semi;
        return 0.0;
    }

    private double safeCalc(DeductionCalculator calc, double gross) {
        if (calc == null || gross <= 0) return 0.0;
        try {
            double v = calc.compute(gross);
            return (v < 0) ? 0.0 : v;
        } catch (Exception ex) {
            return 0.0;
        }
    }

    private String safe(String s) {
        return (s == null) ? "" : s.trim();
    }

    private String safeName(String s) {
        return (s == null || s.isBlank()) ? "(Unknown)" : s.trim();
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}