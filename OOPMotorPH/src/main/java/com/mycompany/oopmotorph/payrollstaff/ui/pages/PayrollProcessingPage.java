/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.payrollstaff.ui.pages;

import com.mycompany.oopmotorph.attendance.repository.AttendanceCsvRepository;
import com.mycompany.oopmotorph.attendance.service.AttendanceService;
import com.mycompany.oopmotorph.common.CsvPaths;
import com.mycompany.oopmotorph.deductions.service.DeductionCalculator;
import com.mycompany.oopmotorph.deductions.service.PagibigCalculator;
import com.mycompany.oopmotorph.deductions.service.PhilHealthCalculator;
import com.mycompany.oopmotorph.deductions.service.SssCalculator;
import com.mycompany.oopmotorph.deductions.service.WithholdingTaxCalculator;
import com.mycompany.oopmotorph.employee.model.EmployeeRecord;
import com.mycompany.oopmotorph.employee.repository.EmployeeCsvRepository;
import com.mycompany.oopmotorph.payrollstaff.service.PayrollAttendanceService;
import com.mycompany.oopmotorph.payrollstaff.service.PayrollProcessingService;
import com.mycompany.oopmotorph.payroll.model.PayslipDetails;
import com.mycompany.oopmotorph.payroll.model.PayslipRow;
import com.mycompany.oopmotorph.payroll.repository.PayslipCsvRepository;
import com.mycompany.oopmotorph.payroll.repository.PayslipDetailsCsvRepository;
import com.mycompany.oopmotorph.payrollstaff.ui.dialogs.PayslipPrintDialog;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PayrollProcessingPage extends JPanel {

    private final PayrollProcessingService processingService;
    private final EmployeeCsvRepository employeeRepo;

    // Keep these so we can PREVIEW breakdown on row double-click
    private final PayrollAttendanceService payrollAttendanceService;

    // calculators (same ones used by PayrollProcessingService)
    private final DeductionCalculator sssCalc = new SssCalculator();
    private final DeductionCalculator philCalc = new PhilHealthCalculator();
    private final DeductionCalculator pagibigCalc = new PagibigCalculator();
    private final DeductionCalculator taxCalc = new WithholdingTaxCalculator();

    private final NumberFormat moneyFmt = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

    private final JDateChooser fromChooser = new JDateChooser();
    private final JDateChooser toChooser = new JDateChooser();
    private final JDateChooser payDateChooser = new JDateChooser();

    private final JButton btnLoadEmployees = new JButton("Load Employees");
    private final JButton btnGenerateSelected = new JButton("Generate Selected");
    private final JButton btnGenerateAll = new JButton("Generate Batch (All)");
    private final JButton btnView = new JButton("View");
    private final JButton btnPrint = new JButton("Print Payslip");
    private final JButton btnRefresh = new JButton("Refresh");

    private final JTable tbl = new JTable();
    private final DefaultTableModel model;

    private final PayslipCsvRepository payslipRepo = new PayslipCsvRepository(CsvPaths.payslipsCsv());
    private final PayslipDetailsCsvRepository payslipDetailsRepo = new PayslipDetailsCsvRepository(CsvPaths.payslipDetailsCsv());

    public PayrollProcessingPage() {
        setLayout(new BorderLayout(10, 10));

        // --- Wiring backend (pure classes) ---
        this.employeeRepo = new EmployeeCsvRepository(CsvPaths.employeeDataCsv());

        AttendanceService attendanceService =
                new AttendanceService(new AttendanceCsvRepository(CsvPaths.attendanceCsv()));
        this.payrollAttendanceService = new PayrollAttendanceService(attendanceService);

        this.processingService = PayrollProcessingService.createDefault(
                payrollAttendanceService,
                sssCalc,
                philCalc,
                pagibigCalc,
                taxCalc
        );

        // --- Top controls (Period + Pay Date) ---
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        top.add(new JLabel("Period From:"));
        fromChooser.setDateFormatString("M/d/yyyy");
        fromChooser.setPreferredSize(new Dimension(130, 25));
        top.add(fromChooser);

        top.add(new JLabel("To:"));
        toChooser.setDateFormatString("M/d/yyyy");
        toChooser.setPreferredSize(new Dimension(130, 25));
        top.add(toChooser);

        top.add(new JLabel("Pay Date:"));
        payDateChooser.setDateFormatString("M/d/yyyy");
        payDateChooser.setPreferredSize(new Dimension(130, 25));
        top.add(payDateChooser);

        top.add(btnLoadEmployees);
        top.add(btnRefresh);

        add(top, BorderLayout.NORTH);

        // --- Table (checkbox selection) ---
        model = new DefaultTableModel(new Object[]{
                "Select", "Employee #", "Employee Name", "Status", "Position", "Gross Semi-monthly", "Hourly Rate"
        }, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                return col == 0; // only checkbox editable
            }
            @Override public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 0) ? Boolean.class : String.class;
            }
        };

        tbl.setModel(model);
        tbl.setRowHeight(24);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Double-click to show payroll breakdown preview
        tbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tbl.getSelectedRow() >= 0) {
                    onViewBreakdown();
                }
            }
        });

        add(new JScrollPane(tbl), BorderLayout.CENTER);

        // --- Bottom buttons ---
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnView);
        bottom.add(btnPrint);
        bottom.add(btnGenerateSelected);
        bottom.add(btnGenerateAll);
        add(bottom, BorderLayout.SOUTH);

        // --- Actions ---
        btnLoadEmployees.addActionListener(e -> loadEmployees());
        btnRefresh.addActionListener(e -> refreshForm());
        btnGenerateSelected.addActionListener(e -> onGenerateSelected());
        btnGenerateAll.addActionListener(e -> onGenerateAll());
        btnView.addActionListener(e -> onViewBreakdown());
        btnPrint.addActionListener(e -> onPrintPayslip());

        // initial load
        loadEmployees();
    }

    private void onPrintPayslip() {
        try {
            int row = tbl.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select an employee row first.");
                return;
            }

            String empNo = String.valueOf(model.getValueAt(row, 1)).trim();
            if (empNo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Invalid Employee #.");
                return;
            }

            // Find latest payslip row + details for this employee (last occurrence in file)
            PayslipRow latestRow = null;
            for (PayslipRow r : payslipRepo.findAll()) {
                if (empNo.equalsIgnoreCase(nz(r.getEmployeeNo()))) {
                    latestRow = r;
                }
            }
            if (latestRow == null) {
                JOptionPane.showMessageDialog(this,
                        "No payslip found for employee # " + empNo + ".\nGenerate payslips first.");
                return;
            }

            PayslipDetails details = payslipDetailsRepo.findByPayslipId(latestRow.getPayslipId())
                    .orElse(null);

            PayslipPrintDialog dlg = new PayslipPrintDialog(SwingUtilities.getWindowAncestor(this), latestRow, details);
            dlg.setVisible(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to print payslip.\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadEmployees() {
        try {
            List<EmployeeRecord> employees = employeeRepo.findAll();
            model.setRowCount(0);

            for (EmployeeRecord e : employees) {
                model.addRow(new Object[]{
                        Boolean.FALSE,
                        nz(e.getEmployeeNo()),
                        nz(e.getFullName()),
                        nz(e.getStatus()),
                        nz(e.getPosition()),
                        String.valueOf(e.getGrossSemiMonthlyRate()),
                        String.valueOf(e.getHourlyRate())
                });
            }

            if (employees.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No employees found in EmployeeData.csv",
                        "No Data",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to load employees.\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshForm() {
        fromChooser.setDate(null);
        toChooser.setDate(null);
        payDateChooser.setDate(null);

        for (int r = 0; r < model.getRowCount(); r++) {
            model.setValueAt(Boolean.FALSE, r, 0);
        }
    }

    private void onGenerateSelected() {
        try {
            LocalDate from = getDate(fromChooser);
            LocalDate to = getDate(toChooser);
            LocalDate payDate = getDate(payDateChooser);

            List<String> selectedEmpNos = getCheckedEmployeeNos();
            if (selectedEmpNos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please check at least one employee.");
                return;
            }

            processingService.generateBatch(from, to, payDate, selectedEmpNos);

            JOptionPane.showMessageDialog(this,
                    "Payslips generated!\nStatus: FOR_SUPERVISOR\n\nSaved to:\n- Payslips.csv\n- PayslipDetails.csv",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            for (int r = 0; r < model.getRowCount(); r++) {
                model.setValueAt(Boolean.FALSE, r, 0);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to generate selected payslips.\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onGenerateAll() {
        try {
            LocalDate from = getDate(fromChooser);
            LocalDate to = getDate(toChooser);
            LocalDate payDate = getDate(payDateChooser);

            List<String> allEmpNos = new ArrayList<>();
            for (int r = 0; r < model.getRowCount(); r++) {
                String empNo = String.valueOf(model.getValueAt(r, 1)).trim();
                if (!empNo.isEmpty()) allEmpNos.add(empNo);
            }

            if (allEmpNos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No employees loaded.");
                return;
            }

            int ok = JOptionPane.showConfirmDialog(this,
                    "Generate payslips for ALL employees (" + allEmpNos.size() + ")?",
                    "Confirm Batch Generation",
                    JOptionPane.YES_NO_OPTION);

            if (ok != JOptionPane.YES_OPTION) return;

            processingService.generateBatch(from, to, payDate, allEmpNos);

            JOptionPane.showMessageDialog(this,
                    "Batch payslips generated!\nStatus: FOR_SUPERVISOR\n\nSaved to:\n- Payslips.csv\n- PayslipDetails.csv",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to generate batch payslips.\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Double-click a row to preview gross, deductions, net pay.
     * This does NOT write files; it is preview only.
     */
    private void onViewBreakdown() {
        try {
            int row = tbl.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select an employee row first.");
                return;
            }

            LocalDate from = getDate(fromChooser);
            LocalDate to = getDate(toChooser);

            if (from == null || to == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select Period From and To first.\nThen double-click an employee to preview the breakdown.",
                        "Missing Payroll Period",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (to.isBefore(from)) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date range: To must be after (or equal to) From.",
                        "Invalid Range",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String empNo = String.valueOf(model.getValueAt(row, 1)).trim();
            String empName = String.valueOf(model.getValueAt(row, 2)).trim();
            String status = String.valueOf(model.getValueAt(row, 3)).trim();
            String position = String.valueOf(model.getValueAt(row, 4)).trim();

            java.util.Optional<EmployeeRecord> opt = employeeRepo.findByEmployeeNo(empNo);
            if (opt.isEmpty()) {
             JOptionPane.showMessageDialog(this,
            "Employee not found in EmployeeData.csv: " + empNo);
             return;
}
EmployeeRecord emp = opt.get();

double totalHours = payrollAttendanceService.summarize(from, to, "")
        .stream()
        .filter(s -> empNo.equalsIgnoreCase(nz(s.getEmployeeId())))
        .mapToDouble(s -> s.getTotalHours())
        .findFirst()
        .orElse(0.0);

            double hourlyRate = emp.getHourlyRate();
            double semiRate = emp.getGrossSemiMonthlyRate();

            double gross;
            String grossRule;
            if (hourlyRate > 0 && totalHours > 0) {
                gross = hourlyRate * totalHours;
                grossRule = "Hourly Rate × Total Hours";
            } else {
                gross = semiRate;
                grossRule = "Gross Semi-monthly Rate";
            }

            double sss = safeCalc(sssCalc, gross);
            double phil = safeCalc(philCalc, gross);
            double pagibig = safeCalc(pagibigCalc, gross);
            double tax = safeCalc(taxCalc, gross);

            double totalDed = sss + phil + pagibig + tax;
            double netPay = gross - totalDed;

            String msg =
                    "PAYROLL BREAKDOWN \n" +
                    "----------------------------------\n" +
                    "Employee #: " + empNo + "\n" +
                    "Name: " + empName + "\n" +
                    "Status: " + status + "\n" +
                    "Position: " + position + "\n\n" +
                    "Period: " + from + " to " + to + "\n" +
                    "Total Hours: " + round2(totalHours) + "\n\n" +
                    "Gross Rule: " + grossRule + "\n" +
                    "Gross Semi-monthly: " + moneyFmt.format(semiRate) + "\n" +
                    "Hourly Rate: " + moneyFmt.format(hourlyRate) + "\n" +
                    "Gross Pay: " + moneyFmt.format(round2(gross)) + "\n\n" +
                    "Deductions:\n" +
                    "- SSS: " + moneyFmt.format(round2(sss)) + "\n" +
                    "- PhilHealth: " + moneyFmt.format(round2(phil)) + "\n" +
                    "- Pag-ibig: " + moneyFmt.format(round2(pagibig)) + "\n" +
                    "- Tax: " + moneyFmt.format(round2(tax)) + "\n" +
                    "Total Deductions: " + moneyFmt.format(round2(totalDed)) + "\n\n" +
                    "Net Pay: " + moneyFmt.format(round2(netPay));

            JOptionPane.showMessageDialog(this, msg, "Payroll Breakdown Preview", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to preview payroll breakdown.\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String> getCheckedEmployeeNos() {
        List<String> out = new ArrayList<>();
        for (int r = 0; r < model.getRowCount(); r++) {
            Object checked = model.getValueAt(r, 0);
            boolean isChecked = (checked instanceof Boolean) && (Boolean) checked;
            if (isChecked) {
                String empNo = String.valueOf(model.getValueAt(r, 1)).trim();
                if (!empNo.isEmpty()) out.add(empNo);
            }
        }
        return out;
    }

    private LocalDate getDate(JDateChooser chooser) {
        if (chooser.getDate() == null) return null;
        return chooser.getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private double safeCalc(DeductionCalculator calc, double gross) {
        if (calc == null || gross <= 0) return 0.0;
        try {
            double v = calc.compute(gross);
            return (v < 0) ? 0.0 : v;
        } catch (Exception e) {
            return 0.0;
        }
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private String nz(String s) {
        return (s == null) ? "" : s.trim();
    }
}