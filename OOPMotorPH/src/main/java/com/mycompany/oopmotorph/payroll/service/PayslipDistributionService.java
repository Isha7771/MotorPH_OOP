/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.payroll.service;

import com.mycompany.oopmotorph.payroll.model.PayslipKey;
import com.mycompany.oopmotorph.payroll.model.PayslipRow;
import com.mycompany.oopmotorph.payroll.repository.PayslipCsvRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PayslipDistributionService {

    private final PayslipCsvRepository repo;

    public PayslipDistributionService(PayslipCsvRepository repo) {
        this.repo = repo;
    }

    public List<PayslipRow> getAll() throws IOException {
        return repo.findAll();
    }

    public List<PayslipRow> filter(String search, String status) throws IOException {
        String q = (search == null) ? "" : search.trim().toLowerCase();

        return repo.findAll().stream()
                .filter(r -> q.isEmpty()
                        || safe(r.getPayslipId()).toLowerCase().contains(q)
                        || safe(r.getEmployeeNo()).toLowerCase().contains(q)
                        || safe(r.getEmployeeName()).toLowerCase().contains(q))
                .filter(r -> status == null || status.equalsIgnoreCase("All")
                        || safe(r.getStatus()).equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }


    public void distribute(String payslipId, String supervisorName) throws IOException {
        repo.updateStatus(payslipId, "DISTRIBUTED", "Distributed by " + supervisorName);
    }

    public void distribute(String payslipId, String employeeNo, String supervisorName) throws IOException {
        repo.updateStatus(payslipId, employeeNo, "DISTRIBUTED", "Distributed by " + supervisorName);
    }

    public void distributeSelected(List<PayslipKey> selected, String supervisorName) throws IOException {
        if (selected == null || selected.isEmpty()) return;

        String action = "Distributed by " + supervisorName;

        for (PayslipKey key : selected) {
            if (key == null) continue;
            repo.updateStatus(key.getPayslipId(), key.getEmployeeNo(), "DISTRIBUTED", action);
        }
    }

    public void distributeSelectedRows(List<PayslipRow> selectedRows, String supervisorName) throws IOException {
        if (selectedRows == null || selectedRows.isEmpty()) return;

        List<PayslipKey> keys = new ArrayList<>();
        for (PayslipRow r : selectedRows) {
            if (r == null) continue;
            keys.add(new PayslipKey(r.getPayslipId(), r.getEmployeeNo()));
        }
        distributeSelected(keys, supervisorName);
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}