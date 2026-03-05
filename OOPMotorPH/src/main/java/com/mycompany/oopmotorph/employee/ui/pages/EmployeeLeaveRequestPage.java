/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.employee.ui.pages;

import com.mycompany.oopmotorph.leave.model.LeaveRequest;
import com.mycompany.oopmotorph.leave.model.LeaveStatus;
import com.mycompany.oopmotorph.leave.model.LeaveType;
import com.mycompany.oopmotorph.leave.repository.LeaveCsvRepository;
import com.mycompany.oopmotorph.leave.service.LeaveService;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class EmployeeLeaveRequestPage extends JPanel {

    private final LeaveService leaveService;
    private final String employeeName;

    private final JComboBox<String> cmbLeaveType =
            new JComboBox<>(new String[]{"Vacation Type", "Sick Leave"});
    private final JDateChooser dcStart = new JDateChooser();
    private final JDateChooser dcEnd = new JDateChooser();

    private final JButton btnSubmit = new JButton("Submit");
    private final JButton btnRefresh = new JButton("Refresh");
    private final JButton btnView = new JButton("View");

    private final JTable tbl = new JTable();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Request ID", "Date Filed", "Status"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    private List<LeaveRequest> current = java.util.Collections.emptyList();

    public EmployeeLeaveRequestPage(String employeeName) {
        this.employeeName = employeeName;

        Path leaveCsvPath = Paths.get(System.getProperty("user.dir"))
                .resolve("Data")
                .resolve("Leave.csv");

        this.leaveService = new LeaveService(new LeaveCsvRepository(leaveCsvPath));

        buildUI();
        wireEvents();
        loadTable();
    }

    public EmployeeLeaveRequestPage() {
        this("Employee");
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.anchor = GridBagConstraints.WEST;

        dcStart.setDateFormatString("M/d/yyyy");
        dcEnd.setDateFormatString("M/d/yyyy");

        int r = 0;
        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("Leave Type:"), gc);
        gc.gridx = 1; gc.gridy = r; form.add(cmbLeaveType, gc); r++;

        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("Start Date:"), gc);
        gc.gridx = 1; gc.gridy = r; form.add(dcStart, gc); r++;

        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("End Date:"), gc);
        gc.gridx = 1; gc.gridy = r; form.add(dcEnd, gc); r++;

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.add(btnSubmit);
        buttons.add(btnRefresh);
        buttons.add(btnView);

        JPanel left = new JPanel(new BorderLayout(10, 10));
        left.add(form, BorderLayout.CENTER);
        left.add(buttons, BorderLayout.SOUTH);

        add(left, BorderLayout.WEST);

        tbl.setModel(model);
        tbl.setRowHeight(24);
        add(new JScrollPane(tbl), BorderLayout.CENTER);
    }

    private void wireEvents() {
        btnSubmit.addActionListener(e -> onSubmit());
        btnRefresh.addActionListener(e -> loadTable());
        btnView.addActionListener(e -> onView());
    }

    private void onSubmit() {
        try {
            LeaveType type = toLeaveType((String) cmbLeaveType.getSelectedItem());
            LocalDate start = toLocalDate(dcStart);
            LocalDate end = toLocalDate(dcEnd);

            LeaveRequest created = leaveService.submitEmployeeLeave(employeeName, type, start, end);

            JOptionPane.showMessageDialog(this,
                    "Request submitted!\nID: " + created.getRequestId() +
                            "\nStatus: " + created.getStatus().name(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            dcStart.setDate(null);
            dcEnd.setDate(null);

            loadTable();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Submit Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onView() {
        int row = tbl.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a request first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        LeaveRequest r = current.get(row);

        String msg =
                "Request ID: " + r.getRequestId() + "\n" +
                "Employee Name: " + r.getEmployeeName() + "\n" +
                "Leave Type: " + (r.getLeaveType() == null ? "" : r.getLeaveType().name()) + "\n" +
                "Date Request: " + r.getDateRequest() + "\n" +
                "Date Filed: " + r.getDateFiled() + "\n" +
                "Start Date: " + r.getStartDate() + "\n" +
                "End Date: " + r.getEndDate() + "\n" +
                "Status: " + (r.getStatus() == null ? "" : r.getStatus().name());

        JOptionPane.showMessageDialog(this, msg, "Leave Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadTable() {
        try {
            current = leaveService.getEmployeeLeaves(employeeName);

            model.setRowCount(0);
            for (LeaveRequest r : current) {
                model.addRow(new Object[]{
                        r.getRequestId(),
                        r.getDateFiled(),
                        r.getStatus() == null ? "" : r.getStatus().name()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load requests:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static LeaveType toLeaveType(String s) {
        if (s == null) return null;
        if (s.equalsIgnoreCase("Sick Leave")) return LeaveType.SICK;
        return LeaveType.VACATION;
    }

    private static LocalDate toLocalDate(JDateChooser dc) {
        if (dc.getDate() == null) return null;
        return dc.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
