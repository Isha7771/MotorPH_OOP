/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.supervisor.ui.pages;

import com.mycompany.oopmotorph.timesheet.model.TimeLog;
import com.mycompany.oopmotorph.timesheet.repository.TimeLogCsvRepository;
import com.mycompany.oopmotorph.timesheet.service.TimeLogService;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SupervisorTimesheetPanel extends JPanel {

    private final TimeLogService service;
    private final DefaultTableModel model;
    private final JTable table;

    private final JTextField txtSearch = new JTextField();
    private final JDateChooser dateChooser = new JDateChooser();
    private final JButton btnRefresh = new JButton("Refresh");
    private final JButton btnClearDate = new JButton("Clear Date");

    private List<TimeLog> allLogs = new ArrayList<>();

    public SupervisorTimesheetPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 16, 16, 16));

        // repo/service
        Path csv = Path.of("Data", "DataTimeLogs.csv");
        this.service = new TimeLogService(new TimeLogCsvRepository(csv));

        // Filter bar (top)
        add(buildFilterPanel(), BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new Object[]{
                "Employee #", "Last Name", "First Name", "Date", "Log In", "Log Out", "Total Hours"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Events
        btnRefresh.addActionListener(e -> reload());
        btnClearDate.addActionListener(e -> {
            dateChooser.setDate(null);
            applyFilter();
        });

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });

        dateChooser.getDateEditor().addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) applyFilter();
        });

        reload();
    }

    private JComponent buildFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        panel.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel lblEmployee = new JLabel("Employee:");
        JLabel lblDate = new JLabel("Date:");

        txtSearch.setPreferredSize(new Dimension(180, 28));
        txtSearch.setToolTipText("Search Employee # / Name");

        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setPreferredSize(new Dimension(140, 28));
        dateChooser.setMaximumSize(new Dimension(160, 28));

        panel.add(lblEmployee);
        panel.add(txtSearch);

        panel.add(lblDate);
        panel.add(dateChooser);

        panel.add(btnClearDate);
        panel.add(btnRefresh);

        return panel;
    }

    private void reload() {
        allLogs = new ArrayList<>(service.getAll());
        applyFilter();
    }

    private void applyFilter() {
        String q = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase();
        LocalDate selected = toLocalDate(dateChooser.getDate());

        model.setRowCount(0);

        for (TimeLog t : allLogs) {
            boolean matchQuery = q.isBlank()
                    || t.getEmployeeNumber().toLowerCase().contains(q)
                    || t.getLastName().toLowerCase().contains(q)
                    || t.getFirstName().toLowerCase().contains(q)
                    || t.getFullName().toLowerCase().contains(q);

            boolean matchDate = (selected == null) || selected.equals(t.getDate());

            if (matchQuery && matchDate) {
                model.addRow(new Object[]{
                        t.getEmployeeNumber(),
                        t.getLastName(),
                        t.getFirstName(),
                        t.getDate() == null ? "" : t.getDate().toString(),
                        t.getTimeIn() == null ? "" : t.getTimeIn().toString(),
                        t.getTimeOut() == null ? "" : t.getTimeOut().toString(),
                        t.getTotalHoursText()
                });
            }
        }
    }

    private LocalDate toLocalDate(Date d) {
        if (d == null) return null;
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}