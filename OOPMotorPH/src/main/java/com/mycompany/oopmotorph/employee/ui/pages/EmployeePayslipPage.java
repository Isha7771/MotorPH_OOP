/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.employee.ui.pages;

import com.mycompany.oopmotorph.payroll.model.PayslipDetails;
import com.mycompany.oopmotorph.payroll.model.PayslipRow;
import com.mycompany.oopmotorph.payroll.repository.PayslipCsvRepository;
import com.mycompany.oopmotorph.payroll.repository.PayslipDetailsCsvRepository;
import com.mycompany.oopmotorph.payroll.repository.PayslipDisputeCsvRepository;
import com.mycompany.oopmotorph.payroll.service.EmployeePayslipService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class EmployeePayslipPage extends JPanel {

    private final String employeeNo;
    private final String employeeName;

    private final EmployeePayslipService service;
    private final PayslipDetailsCsvRepository detailsRepo;

    private final JButton btnRefresh = new JButton("Refresh");
    private final JButton btnView = new JButton("View");
    private final JButton btnDispute = new JButton("Dispute");

    private final JTable tbl = new JTable();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Payslip ID", "Pay Date", "Status"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    private List<PayslipRow> current = java.util.Collections.emptyList();

    public EmployeePayslipPage(String employeeNo, String employeeName) {
        this.employeeNo = employeeNo;
        this.employeeName = employeeName;

        Path payslipPath = Paths.get(System.getProperty("user.dir"))
                .resolve("Data")
                .resolve("Payslips.csv");

        Path disputePath = Paths.get(System.getProperty("user.dir"))
                .resolve("Data")
                .resolve("PayslipDisputes.csv");

        Path detailsPath = Paths.get(System.getProperty("user.dir"))
                .resolve("Data")
                .resolve("PayslipDetails.csv");

        this.service = new EmployeePayslipService(
                new PayslipCsvRepository(payslipPath),
                new PayslipDisputeCsvRepository(disputePath)
        );

        this.detailsRepo = new PayslipDetailsCsvRepository(detailsPath);

        buildUI();
        wire();
        loadTable();
    }

    public EmployeePayslipPage() {
        this("", "");
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

   

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.add(btnRefresh);
        top.add(btnView);
        top.add(btnDispute);

        add(top, BorderLayout.WEST);

        tbl.setModel(model);
        tbl.setRowHeight(24);
        add(new JScrollPane(tbl), BorderLayout.CENTER);
    }

    private void wire() {
        btnRefresh.addActionListener(e -> loadTable());
        btnView.addActionListener(e -> onView());
        btnDispute.addActionListener(e -> onDispute());
    }

    private void loadTable() {
        try {
            current = service.getOwnPayslips(employeeNo);
            model.setRowCount(0);

            for (PayslipRow r : current) {
                model.addRow(new Object[]{
                        r.getPayslipId(),
                        r.getPayDate(),
                        r.getStatus()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load payslips:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onView() {
        int row = tbl.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a payslip first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        PayslipRow r = current.get(row);

        try {
            Optional<PayslipDetails> dOpt = detailsRepo.findByPayslipId(r.getPayslipId());

            String msg;
            if (dOpt.isPresent()) {
                PayslipDetails d = dOpt.get();

                msg =
                        "Payslip ID: " + nz(r.getPayslipId()) + "\n" +
                        "Employee No: " + nz(r.getEmployeeNo()) + "\n" +
                        "Employee Name: " + nz(r.getEmployeeName()) + "\n" +
                        "Pay Date: " + nz(r.getPayDate()) + "\n" +
                        "Status: " + nz(r.getStatus()) + "\n\n" +
                        "Period From: " + nz(d.getPeriodFrom()) + "\n" +
                        "Period To: " + nz(d.getPeriodTo()) + "\n" +
                        "Total Hours: " + d.getTotalHours() + "\n\n" +
                        "Gross: " + d.getGross() + "\n" +
                        "SSS: " + d.getSss() + "\n" +
                        "PhilHealth: " + d.getPhilHealth() + "\n" +
                        "Pagibig: " + d.getPagibig() + "\n" +
                        "Tax: " + d.getTax() + "\n" +
                        "Total Deductions: " + d.getTotalDeductions() + "\n" +
                        "Net Pay: " + d.getNetPay();
            } else {
                msg =
                        "Payslip ID: " + nz(r.getPayslipId()) + "\n" +
                        "Employee No: " + nz(r.getEmployeeNo()) + "\n" +
                        "Employee Name: " + nz(r.getEmployeeName()) + "\n" +
                        "Pay Date: " + nz(r.getPayDate()) + "\n" +
                        "Status: " + nz(r.getStatus()) + "\n\n" +
                        "No PayslipDetails found for this payslip yet.\n" +
                        "Generate payroll details first (PayslipDetails.csv).";
            }

            JOptionPane.showMessageDialog(this, msg, "Payslip Details", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load payslip details:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDispute() {
        int row = tbl.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a payslip first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        PayslipRow r = current.get(row);

        String status = (r.getStatus() == null) ? "" : r.getStatus().trim().toLowerCase();
        if (status.equals("disputed")) {
            JOptionPane.showMessageDialog(this,
                    "This payslip is already disputed.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String reason = JOptionPane.showInputDialog(this, "Enter dispute reason:");
        if (reason == null) return;

        try {
            service.dispute(r.getPayslipId(), employeeNo, employeeName, reason);
            JOptionPane.showMessageDialog(this,
                    "Dispute filed. Status set to Disputed.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Dispute Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String nz(String s) {
        return s == null ? "" : s.trim();
    }
}