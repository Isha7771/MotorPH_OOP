/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.payroll.service;

import com.mycompany.oopmotorph.payroll.model.PayslipRow;
import com.mycompany.oopmotorph.payroll.repository.PayslipCsvRepository;
import com.mycompany.oopmotorph.payroll.repository.PayslipDisputeCsvRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class EmployeePayslipService {

    private final PayslipCsvRepository payslipRepo;
    private final PayslipDisputeCsvRepository disputeRepo;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("M/d/yyyy");

    public EmployeePayslipService(PayslipCsvRepository payslipRepo,
                                  PayslipDisputeCsvRepository disputeRepo) {
        this.payslipRepo = payslipRepo;
        this.disputeRepo = disputeRepo;
    }

    public List<PayslipRow> getOwnPayslips(String employeeNo) throws java.io.IOException {
        return payslipRepo.findByEmployeeNo(employeeNo);
    }

    public void dispute(String payslipId, String employeeNo, String employeeName, String reason)
            throws java.io.IOException {

        if (payslipId == null || payslipId.trim().isEmpty())
            throw new IllegalArgumentException("Payslip ID is required.");
        if (employeeNo == null || employeeNo.trim().isEmpty())
            throw new IllegalArgumentException("Employee No is required.");
        if (reason == null || reason.trim().isEmpty())
            throw new IllegalArgumentException("Reason is required.");

        payslipRepo.updateStatus(payslipId, "Disputed", "Employee Dispute");

        String disputeId = "PD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String dateFiled = LocalDate.now().format(fmt);

        disputeRepo.append(disputeId, payslipId, employeeNo, employeeName, dateFiled, reason.trim());
    }
}
