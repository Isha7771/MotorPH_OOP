/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.hr.ui.pages;

import com.mycompany.oopmotorph.attendance.model.AttendanceRecord;
import com.mycompany.oopmotorph.attendance.repository.AttendanceCsvRepository;
import com.mycompany.oopmotorph.attendance.service.AttendanceService;
import com.mycompany.oopmotorph.common.CsvPaths;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HrAttendanceMonitoringPage extends JPanel {

    private final AttendanceService attendanceService;

    private final JTextField txtSearch = new JTextField(20);
    private final JDateChooser dateChooser = new JDateChooser();
    private final JComboBox<String> cmbStatus =
            new JComboBox<>(new String[]{"All", "PRESENT", "LATE", "ABSENT"});
    private final JButton btnRefresh = new JButton("Refresh");

    private final JTable tbl = new JTable();
    private final DefaultTableModel model;

    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("M/d/yyyy");

    public HrAttendanceMonitoringPage() {
        setLayout(new BorderLayout(10, 10));

        // Backend wiring
        this.attendanceService = new AttendanceService(new AttendanceCsvRepository(CsvPaths.attendanceCsv()));

        // Top controls
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Search:"));
        top.add(txtSearch);

        top.add(new JLabel("Date:"));
        dateChooser.setDateFormatString("M/d/yyyy");
        dateChooser.setPreferredSize(new Dimension(130, 25));
        top.add(dateChooser);

        top.add(new JLabel("Status:"));
        top.add(cmbStatus);

        top.add(btnRefresh);

        add(top, BorderLayout.NORTH);

        // Table (matches your AttendanceRecord fields)
        model = new DefaultTableModel(
                new Object[]{"Employee #", "Employee Name", "Date", "Position", "Time In", "Time Out", "Total Hours", "Status", "Remarks"},
                0
        ) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tbl.setModel(model);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tbl), BorderLayout.CENTER);

        // Events
        btnRefresh.addActionListener(e -> loadTable());
        txtSearch.addActionListener(e -> loadTable());
        cmbStatus.addActionListener(e -> loadTable());
        dateChooser.addPropertyChangeListener("date", evt -> loadTable());

        loadTable();
    }

    private void loadTable() {
        try {
            String q = txtSearch.getText();
            LocalDate selectedDate = getSelectedDate();
            String statusText = (String) cmbStatus.getSelectedItem();

            // ✅ use your service method (String status overload we added)
            List<AttendanceRecord> rows = attendanceService.getAttendance(q, selectedDate, statusText);

            model.setRowCount(0);
            for (AttendanceRecord r : rows) {
                model.addRow(new Object[]{
                        r.getEmployeeId(),              // ✅ correct getter
                        r.getEmployeeName(),
                        (r.getDate() == null) ? "" : dateFmt.format(r.getDate()),
                        r.getPosition(),                // ✅ you don't have department
                        (r.getTimeIn() == null) ? "" : r.getTimeIn().toString(),
                        (r.getTimeOut() == null) ? "" : r.getTimeOut().toString(),
                        r.getTotalHours(),
                        r.getStatus(),
                        r.getRemarks()
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to load attendance.\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private LocalDate getSelectedDate() {
        if (dateChooser.getDate() == null) return null;
        return dateChooser.getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}