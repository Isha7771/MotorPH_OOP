/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.payrollstaff.service;

import com.mycompany.oopmotorph.common.CsvPaths;
import com.mycompany.oopmotorph.deductions.service.DeductionCalculator;
import com.mycompany.oopmotorph.employee.repository.EmployeeCsvRepository;
import com.mycompany.oopmotorph.payroll.repository.PayslipCsvRepository;
import com.mycompany.oopmotorph.payroll.repository.PayslipDetailsCsvRepository;
import com.mycompany.oopmotorph.payroll.service.PayslipGenerationService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PayrollProcessingService {

    private final PayslipGenerationService generator;

    public PayrollProcessingService(PayslipGenerationService generator) {
        this.generator = Objects.requireNonNull(generator, "generator");
    }

    public void generateBatch(LocalDate from, LocalDate to, LocalDate payDate, List<String> employeeNos) throws IOException {
        // Orchestrator delegates the business logic to payroll service
        generator.generateBatch(from, to, payDate, employeeNos);
    }

    public static PayrollProcessingService createDefault(
            PayrollAttendanceService payrollAttendanceService,
            DeductionCalculator sss,
            DeductionCalculator phil,
            DeductionCalculator pagibig,
            DeductionCalculator tax
    ) {
        PayslipGenerationService generator = new PayslipGenerationService(
                new EmployeeCsvRepository(CsvPaths.employeeDataCsv()),
                new PayslipCsvRepository(CsvPaths.payslipsCsv()),
                new PayslipDetailsCsvRepository(CsvPaths.payslipDetailsCsv()),
                payrollAttendanceService,
                new ArrayList<>(List.of(sss, phil, pagibig, tax))
        );

        return new PayrollProcessingService(generator);
    }
}