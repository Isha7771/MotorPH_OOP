/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.supervisor.ui.pages;

import com.mycompany.oopmotorph.overtime.model.OvertimeRequest;
import com.mycompany.oopmotorph.overtime.model.OvertimeStatus;
import com.mycompany.oopmotorph.overtime.repository.OvertimeCsvRepository;
import com.mycompany.oopmotorph.overtime.repository.OvertimeRepository;
import com.mycompany.oopmotorph.overtime.service.OvertimeService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class SupervisorOvertimePanel extends JPanel {

    private final OvertimeService overtimeService;

    private final JTextField txtSearch = new JTextField(22);
    private final JComboBox<String> cmbStatus =
            new JComboBox<>(new String[]{"All", "PENDING", "APPROVED", "REJECTED"});
    private final JButton btnRefresh = new JButton("Refresh");

    private final JTable tbl = new JTable();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Request ID", "Employee Name", "Date", "Hours", "Status", "Action"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };

    public SupervisorOvertimePanel() {
        Path overtimeCsvPath = Paths.get(System.getProperty("user.dir"))
                .resolve("Data")
                .resolve("Overtime.csv");

        OvertimeRepository repo = new OvertimeCsvRepository(overtimeCsvPath);
        this.overtimeService = new OvertimeService(repo);

        buildUI();
        wireEvents();
        loadTable();

        tbl.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int row = tbl.rowAtPoint(e.getPoint());
                int col = tbl.columnAtPoint(e.getPoint());

                if (row >= 0 && col >= 0 && "Action".equals(tbl.getColumnName(col))) {
                    tbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    tbl.setCursor(Cursor.getDefaultCursor());
                }
            }
        });
    }

    private void buildUI() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        top.add(new JLabel("Search:"));
        top.add(txtSearch);
        top.add(new JLabel("Status:"));
        top.add(cmbStatus);
        top.add(btnRefresh);

        tbl.setModel(model);

        // Make "View" look clickable (blue + underlined)
        tbl.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                String text = value == null ? "" : value.toString();
                lbl.setText("<html><u>" + text + "</u></html>");
                lbl.setForeground(isSelected ? Color.WHITE : Color.BLUE);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                return lbl;
            }
        });

        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tbl), BorderLayout.CENTER);
    }

    private void wireEvents() {
        btnRefresh.addActionListener(e -> loadTable());
        txtSearch.addActionListener(e -> loadTable());
        cmbStatus.addActionListener(e -> loadTable());

        tbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tbl.rowAtPoint(e.getPoint());
                int col = tbl.columnAtPoint(e.getPoint());
                if (row < 0 || col < 0) return;

                if (!"Action".equals(tbl.getColumnName(col))) return;

                String action = String.valueOf(model.getValueAt(row, col));
                if (!"View".equalsIgnoreCase(action)) return;

                String requestId = String.valueOf(model.getValueAt(row, 0));
                openViewDialog(requestId);
            }
        });
    }

    private void loadTable() {
        model.setRowCount(0);

        String search = txtSearch.getText() == null ? "" : txtSearch.getText().trim();
        String statusText = String.valueOf(cmbStatus.getSelectedItem());

        Optional<OvertimeStatus> statusFilter = Optional.empty();
        if (!"All".equalsIgnoreCase(statusText)) {
            statusFilter = Optional.of(OvertimeStatus.valueOf(statusText));
        }

        List<OvertimeRequest> rows = overtimeService.search(search, statusFilter);

        for (OvertimeRequest r : rows) {
            model.addRow(new Object[]{
                    r.getRequestId(),
                    r.getEmployeeName(),
                    r.getDate(),
                    r.getHours(),
                    r.getStatus().name(),
                    "View"
            });
        }
    }

    private void openViewDialog(String requestId) {
        Optional<OvertimeRequest> opt = overtimeService.findById(requestId);
        if (opt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Request not found: " + requestId,
                    "Not Found",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        OvertimeRequest r = opt.get();

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Overtime Request Overview",
                Dialog.ModalityType.APPLICATION_MODAL);

        dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dlg.setLayout(new BorderLayout(12, 12));

        JPanel content = new JPanel(new GridLayout(0, 1, 6, 6));
        content.setBorder(new EmptyBorder(12, 12, 12, 12));

        content.add(new JLabel("Request ID: " + r.getRequestId()));
        content.add(new JLabel("Employee Name: " + r.getEmployeeName()));
        content.add(new JLabel("Date: " + r.getDate()));
        content.add(new JLabel("Hours: " + r.getHours()));
        content.add(new JLabel("Status: " + r.getStatus().name()));
        content.add(new JLabel("Reason: " + r.getReason()));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnApprove = new JButton("Approve");
        JButton btnReject = new JButton("Reject");
        JButton btnClose = new JButton("Close");

        boolean isPending = r.getStatus() == OvertimeStatus.PENDING;
        btnApprove.setEnabled(isPending);
        btnReject.setEnabled(isPending);

        btnApprove.addActionListener(e -> {
            if (!confirm("Approve this overtime request?")) return;
            try {
                overtimeService.approve(r.getRequestId());
                dlg.dispose();
                loadTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Approve Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        btnReject.addActionListener(e -> {
            if (!confirm("Reject this overtime request?")) return;
            try {
                overtimeService.reject(r.getRequestId());
                dlg.dispose();
                loadTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Reject Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        btnClose.addActionListener(e -> dlg.dispose());

        buttons.add(btnApprove);
        buttons.add(btnReject);
        buttons.add(btnClose);

        dlg.add(content, BorderLayout.CENTER);
        dlg.add(buttons, BorderLayout.SOUTH);

        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(
                this, msg, "Confirm", JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION;
    }
}