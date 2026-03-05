/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.employee.ui.pages;

import com.mycompany.oopmotorph.overtime.model.OvertimeRequest;
import com.mycompany.oopmotorph.overtime.model.OvertimeStatus;
import com.mycompany.oopmotorph.overtime.repository.OvertimeCsvRepository;
import com.mycompany.oopmotorph.overtime.service.OvertimeService;
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

public class EmployeeOvertimeRequestPage extends JPanel {

    private final OvertimeService overtimeService;
    private final String employeeName;

    private final JDateChooser dcDate = new JDateChooser();
    private final JSpinner spHours = new JSpinner(new SpinnerNumberModel(1, 1, 24, 1));
    private final JTextArea txtReason = new JTextArea(3, 25);

    private final JButton btnSubmit = new JButton("Submit");
    private final JButton btnRefresh = new JButton("Refresh");
    private final JButton btnView = new JButton("View");

    private final JTable tbl = new JTable();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Request ID", "Date", "Hours", "Status"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    private List<OvertimeRequest> current = java.util.Collections.emptyList();

    public EmployeeOvertimeRequestPage(String employeeName) {
        this.employeeName = employeeName;

        Path overtimeCsvPath = Paths.get(System.getProperty("user.dir"))
                .resolve("Data")
                .resolve("Overtime.csv");

        this.overtimeService = new OvertimeService(new OvertimeCsvRepository(overtimeCsvPath));

        buildUI();
        wireEvents();
        loadTable();
    }

    public EmployeeOvertimeRequestPage() {
        this("Employee");
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

    

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.anchor = GridBagConstraints.WEST;

        dcDate.setDateFormatString("M/d/yyyy");

        int r = 0;
        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("Overtime Date:"), gc);
        gc.gridx = 1; gc.gridy = r; form.add(dcDate, gc); r++;

        gc.gridx = 0; gc.gridy = r; form.add(new JLabel("Hours:"), gc);
        gc.gridx = 1; gc.gridy = r; form.add(spHours, gc); r++;

        gc.gridx = 0; gc.gridy = r; gc.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Reason:"), gc);
        gc.gridx = 1; gc.gridy = r;
        JScrollPane spReason = new JScrollPane(txtReason);
        form.add(spReason, gc); r++;

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
            LocalDate date = toLocalDate(dcDate);
            int hours = (Integer) spHours.getValue();
            String reason = txtReason.getText();

            OvertimeRequest created = overtimeService.submitEmployeeOvertime(employeeName, date, hours, reason);

            JOptionPane.showMessageDialog(this,
                    "Request submitted!\nID: " + created.getRequestId() +
                            "\nStatus: " + created.getStatus().name(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            dcDate.setDate(null);
            spHours.setValue(1);
            txtReason.setText("");

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

        OvertimeRequest r = current.get(row);

        String msg =
                "Request ID: " + r.getRequestId() + "\n" +
                "Employee Name: " + r.getEmployeeName() + "\n" +
                "Date: " + r.getDate() + "\n" +
                "Hours: " + r.getHours() + "\n" +
                "Status: " + (r.getStatus() == null ? "" : r.getStatus().name()) + "\n" +
                "Reason: " + (r.getReason() == null ? "" : r.getReason());

        JOptionPane.showMessageDialog(this, msg, "Overtime Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadTable() {
        try {
            current = overtimeService.getEmployeeOvertimes(employeeName);

            model.setRowCount(0);
            for (OvertimeRequest r : current) {
                model.addRow(new Object[]{
                        r.getRequestId(),
                        r.getDate(),
                        r.getHours(),
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

    private static LocalDate toLocalDate(JDateChooser dc) {
        if (dc.getDate() == null) return null;
        return dc.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}