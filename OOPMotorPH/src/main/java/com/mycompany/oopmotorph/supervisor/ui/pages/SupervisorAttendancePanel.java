/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.supervisor.ui.pages;

import com.mycompany.oopmotorph.attendance.model.AttendanceRecord;
import com.mycompany.oopmotorph.attendance.model.AttendanceStatus;
import com.mycompany.oopmotorph.attendance.repository.AttendanceCsvRepository;
import com.mycompany.oopmotorph.attendance.service.AttendanceService;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SupervisorAttendancePanel extends JPanel {

    private AttendanceService attendanceService;

    private final DefaultTableModel model;
    private final JTable table;

    private final JTextField txtSearch = new JTextField();
    private final JDateChooser dateChooser = new JDateChooser();
    private final JComboBox<String> cmbStatus =
            new JComboBox<>(new String[]{"All", "Present", "Late", "Absent"});

    private final JButton btnRefresh = new JButton("Refresh");
    private final JButton btnClearDate = new JButton("Clear Date");

    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("M/d/yyyy");

    public SupervisorAttendancePanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 16, 16, 16)); // ✅ match Timesheet padding

        rebuildService();

        // Top filter bar (match Timesheet)
        add(buildFilterPanel(), BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new Object[]{
                "Employee #", "Employee Name", "Date", "Department/Position",
                "Time In", "Time Out", "Total Hours", "Status", "Remarks"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(24); // ✅ match Timesheet look
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Events (match Timesheet behavior style)
        btnRefresh.addActionListener(e -> {
            // Reset filters like a "true refresh"
            txtSearch.setText("");
            dateChooser.setDate(null);
            cmbStatus.setSelectedIndex(0);
            rebuildService();
            loadTable();
        });

        btnClearDate.addActionListener(e -> {
            dateChooser.setDate(null);
            loadTable();
        });

        txtSearch.addActionListener(e -> loadTable());
        cmbStatus.addActionListener(e -> loadTable());
        dateChooser.getDateEditor().addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) loadTable();
        });

        loadTable();
    }

    private JComponent buildFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        panel.setBorder(new EmptyBorder(0, 0, 8, 0)); // ✅ match Timesheet spacing

        JLabel lblSearch = new JLabel("Search:");
        JLabel lblDate = new JLabel("Date:");
        JLabel lblStatus = new JLabel("Status:");

        // ✅ EXACT Timesheet sizing
        txtSearch.setPreferredSize(new Dimension(180, 28));
        txtSearch.setToolTipText("Search Employee # / Name");

        // Keep your date format consistent with Attendance parsing
        dateChooser.setDateFormatString("M/d/yyyy");
        dateChooser.setPreferredSize(new Dimension(140, 28));
        dateChooser.setMaximumSize(new Dimension(160, 28));

        cmbStatus.setPreferredSize(new Dimension(110, 28));

        panel.add(lblSearch);
        panel.add(txtSearch);

        panel.add(lblDate);
        panel.add(dateChooser);

        panel.add(lblStatus);
        panel.add(cmbStatus);

        panel.add(btnClearDate);
        panel.add(btnRefresh);

        return panel;
    }

    private void rebuildService() {
        attendanceService = new AttendanceService(
                new AttendanceCsvRepository(Paths.get("Data/Attendance.csv"))
        );
    }

    private void loadTable() {
        String q = txtSearch.getText() == null ? "" : txtSearch.getText().trim();
        LocalDate selected = toLocalDate(dateChooser.getDate());
        AttendanceStatus status = parseStatusOrNull((String) cmbStatus.getSelectedItem());

        List<AttendanceRecord> rows = attendanceService.getAttendance(q, selected, status);

        model.setRowCount(0);

        for (AttendanceRecord r : rows) {
            model.addRow(new Object[]{
                    r.getEmployeeId(),
                    r.getEmployeeName(),
                    r.getDate() == null ? "" : r.getDate().format(dateFmt),
                    r.getPosition(),
                    r.getTimeIn() == null ? "" : r.getTimeIn().toString(),
                    r.getTimeOut() == null ? "" : r.getTimeOut().toString(),
                    String.format("%.2f", r.getTotalHours()),
                    r.getStatus().name(),
                    r.getRemarks()
            });
        }
    }

    private LocalDate toLocalDate(java.util.Date d) {
        if (d == null) return null;
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private AttendanceStatus parseStatusOrNull(String s) {
        if (s == null) return null;
        switch (s) {
            case "Present": return AttendanceStatus.PRESENT;
            case "Late": return AttendanceStatus.LATE;
            case "Absent": return AttendanceStatus.ABSENT;
            default: return null; // All
        }
    }
}