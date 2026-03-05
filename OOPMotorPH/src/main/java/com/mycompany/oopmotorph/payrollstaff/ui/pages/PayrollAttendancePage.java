/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.payrollstaff.ui.pages;

import com.mycompany.oopmotorph.attendance.repository.AttendanceCsvRepository;
import com.mycompany.oopmotorph.attendance.service.AttendanceService;
import com.mycompany.oopmotorph.common.CsvPaths;
import com.mycompany.oopmotorph.payrollstaff.model.PayrollAttendanceSummary;
import com.mycompany.oopmotorph.payrollstaff.service.PayrollAttendanceService;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class PayrollAttendancePage extends JPanel {

    private final PayrollAttendanceService payrollAttendanceService;

    private final JTextField txtSearch = new JTextField(18);

    private final JDateChooser fromChooser = new JDateChooser();
    private final JDateChooser toChooser = new JDateChooser();

    private final JButton btnGenerate = new JButton("Generate");
    private final JButton btnRefresh = new JButton("Refresh");

    private final JTable tbl = new JTable();
    private final DefaultTableModel model;

    public PayrollAttendancePage() {
        setLayout(new BorderLayout(10, 10));

        AttendanceService attendanceService =
                new AttendanceService(new AttendanceCsvRepository(CsvPaths.attendanceCsv()));

        this.payrollAttendanceService = new PayrollAttendanceService(attendanceService);

        // Top bar
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Search:"));
        top.add(txtSearch);

        top.add(new JLabel("From:"));
        fromChooser.setDateFormatString("M/d/yyyy");
        fromChooser.setPreferredSize(new Dimension(130, 25));
        top.add(fromChooser);

        top.add(new JLabel("To:"));
        toChooser.setDateFormatString("M/d/yyyy");
        toChooser.setPreferredSize(new Dimension(130, 25));
        top.add(toChooser);

        top.add(btnGenerate);
        top.add(btnRefresh);

        add(top, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new Object[]{
                "Employee #", "Employee Name",
                "Present", "Late", "Absent",
                "Total Hours"
        }, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tbl.setModel(model);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tbl), BorderLayout.CENTER);

        // Events
        btnGenerate.addActionListener(e -> loadSummary());
        btnRefresh.addActionListener(e -> clearAndReload());
        txtSearch.addActionListener(e -> loadSummary());
    }

    private void clearAndReload() {
        model.setRowCount(0);
        txtSearch.setText("");
        fromChooser.setDate(null);
        toChooser.setDate(null);
    }

    private void loadSummary() {
        try {
            LocalDate from = getDate(fromChooser);
            LocalDate to = getDate(toChooser);
            String search = txtSearch.getText();

            List<PayrollAttendanceSummary> list =
                    payrollAttendanceService.summarize(from, to, search);

            model.setRowCount(0);
            for (PayrollAttendanceSummary s : list) {
                model.addRow(new Object[]{
                        s.getEmployeeId(),
                        s.getEmployeeName(),
                        s.getPresentCount(),
                        s.getLateCount(),
                        s.getAbsentCount(),
                        s.getTotalHours()
                });
            }

            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No attendance records found for the selected range.",
                        "No Data",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to generate attendance summary.\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private LocalDate getDate(JDateChooser chooser) {
        if (chooser.getDate() == null) return null;
        return chooser.getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}