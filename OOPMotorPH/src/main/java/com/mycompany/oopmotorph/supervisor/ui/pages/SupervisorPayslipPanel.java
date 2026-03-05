package com.mycompany.oopmotorph.supervisor.ui.pages;

import com.mycompany.oopmotorph.payroll.model.PayslipRow;
import com.mycompany.oopmotorph.payroll.repository.PayslipCsvRepository;
import com.mycompany.oopmotorph.payroll.service.PayslipDistributionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SupervisorPayslipPanel extends JPanel {

    private final JTextField txtSearch = new JTextField(18);
    private final JComboBox<String> cmbStatus =
            new JComboBox<>(new String[]{"All", "GENERATED", "DISTRIBUTED", "DISPUTED"});
    private final JButton btnRefresh = new JButton("Refresh");

    private final JTable tbl = new JTable();
    private final DefaultTableModel model;

    private final JButton btnView = new JButton("View");
    private final JButton btnDistributeSelected = new JButton("Distribute Selected");
    private final JButton btnSelectAll = new JButton("Select All");
    private final JButton btnClearSelection = new JButton("Clear");

    private final PayslipDistributionService distributionService;

    // Set this from logged-in admin/supervisor (optional)
    private String supervisorName = "Supervisor";

    public SupervisorPayslipPanel() {
        setLayout(new BorderLayout(10, 10));

        Path payslipPath = Paths.get(System.getProperty("user.dir"))
                .resolve("Data")
                .resolve("Payslips.csv");

        this.distributionService = new PayslipDistributionService(new PayslipCsvRepository(payslipPath));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Search:"));
        top.add(txtSearch);
        top.add(new JLabel("Status:"));
        top.add(cmbStatus);
        top.add(btnRefresh);

        add(top, BorderLayout.NORTH);

        // Payslips.csv columns:
        // PayslipID,EmployeeNo,EmployeeName,PayDate,Status,Action
        model = new DefaultTableModel(
                new Object[]{"Select", "Payslip ID", "Employee #", "Employee Name", "Pay Date", "Status", "Action"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 0; // only checkbox editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 0) ? Boolean.class : String.class;
            }
        };

        tbl.setModel(model);
        tbl.setRowHeight(24);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tbl), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnSelectAll);
        bottom.add(btnClearSelection);
        bottom.add(btnView);
        bottom.add(btnDistributeSelected);
        add(bottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadTable());
        txtSearch.addActionListener(e -> loadTable());
        cmbStatus.addActionListener(e -> loadTable());

        btnSelectAll.addActionListener(e -> setAllSelected(true));
        btnClearSelection.addActionListener(e -> setAllSelected(false));
        btnView.addActionListener(e -> onView());
        btnDistributeSelected.addActionListener(e -> onDistributeSelected());

        loadTable();
    }

    public void setSupervisorName(String supervisorName) {
        if (supervisorName != null && !supervisorName.isBlank()) {
            this.supervisorName = supervisorName.trim();
        }
    }

    private void loadTable() {
        try {
            String q = txtSearch.getText();
            String status = (String) cmbStatus.getSelectedItem();

            List<PayslipRow> rows = distributionService.filter(q, status);

            model.setRowCount(0);
            for (PayslipRow r : rows) {
                model.addRow(new Object[]{
                        Boolean.FALSE, // checkbox
                        r.getPayslipId(),
                        r.getEmployeeNo(),
                        r.getEmployeeName(),
                        r.getPayDate(),
                        r.getStatus(),
                        r.getAction()
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to load payslips.\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setAllSelected(boolean selected) {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(selected, i, 0);
        }
    }

    private PayslipRow getSelectedPayslip() throws Exception {
        int row = tbl.getSelectedRow();
        if (row < 0) return null;

        String payslipId = String.valueOf(model.getValueAt(row, 1));

        // Reload from repo (source of truth)
        for (PayslipRow r : distributionService.getAll()) {
            if (r.getPayslipId().equalsIgnoreCase(payslipId)) return r;
        }
        return null;
    }

    private void onView() {
        try {
            PayslipRow r = getSelectedPayslip();
            if (r == null) {
                JOptionPane.showMessageDialog(this, "Select a payslip row first.");
                return;
            }

            String msg =
                    "Payslip ID: " + r.getPayslipId() + "\n" +
                    "Employee: " + r.getEmployeeNo() + " - " + r.getEmployeeName() + "\n" +
                    "Pay Date: " + r.getPayDate() + "\n" +
                    "Status: " + r.getStatus() + "\n" +
                    "Action: " + (r.getAction() == null ? "" : r.getAction());

            JOptionPane.showMessageDialog(this, msg, "Payslip Details", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to view payslip.\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDistributeSelected() {
        try {
            List<String> selectedPayslipIds = new ArrayList<>();
            for (int i = 0; i < model.getRowCount(); i++) {
                Object v = model.getValueAt(i, 0);
                boolean checked = (v instanceof Boolean) && (Boolean) v;
                if (checked) {
                    selectedPayslipIds.add(String.valueOf(model.getValueAt(i, 1)));
                }
            }

            if (selectedPayslipIds.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tick the checkbox(es) of payslip(s) to distribute.");
                return;
            }

            int ok = JOptionPane.showConfirmDialog(this,
                    "Distribute " + selectedPayslipIds.size() + " payslip(s)?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION);

            if (ok != JOptionPane.YES_OPTION) return;

            // Distribute one-by-one; skip already distributed
            int distributed = 0;
            int skipped = 0;

            List<PayslipRow> all = distributionService.getAll(); // source of truth for status check
            for (String pid : selectedPayslipIds) {
                PayslipRow found = null;
                for (PayslipRow r : all) {
                    if (r.getPayslipId().equalsIgnoreCase(pid)) { found = r; break; }
                }
                if (found == null) { skipped++; continue; }

                if ("DISTRIBUTED".equalsIgnoreCase(found.getStatus())) {
                    skipped++;
                    continue;
                }

                distributionService.distribute(pid, supervisorName);
                distributed++;
            }

            loadTable();

            JOptionPane.showMessageDialog(this,
                    "Distributed: " + distributed + "\n" +
                    "Skipped: " + skipped,
                    "Done",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to distribute payslips.\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
