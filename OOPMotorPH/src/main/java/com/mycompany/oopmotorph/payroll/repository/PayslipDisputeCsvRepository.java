/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.payroll.repository;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class PayslipDisputeCsvRepository implements PayslipDisputeRepository     {

    private final Path csvPath;
    private static final String HEADER = "Dispute ID,Payslip ID,Employee No,Employee Name,Date Filed,Reason";

    public PayslipDisputeCsvRepository(Path csvPath) {
        this.csvPath = csvPath;
        ensureFile();
    }

    public void append(String disputeId, String payslipId, String employeeNo,
                       String employeeName, String dateFiled, String reason) {
        ensureFile();
        try {
            String row = String.join(",",
                    safe(disputeId),
                    safe(payslipId),
                    safe(employeeNo),
                    safe(employeeName),
                    safe(dateFiled),
                    safe(reason)
            );

            Files.writeString(csvPath,
                    row + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception e) {
            throw new RuntimeException("Error writing PayslipDisputes.csv: " + e.getMessage(), e);
        }
    }

    private void ensureFile() {
        try {
            if (Files.notExists(csvPath)) {
                Files.createDirectories(csvPath.getParent());
                Files.writeString(csvPath, HEADER + System.lineSeparator(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot create PayslipDisputes.csv: " + e.getMessage(), e);
        }
    }

    private static String safe(String s) {
        if (s == null) return "";
        return s.replace("\n", " ").replace("\r", " ").replace(",", " ").trim();
    }
}
