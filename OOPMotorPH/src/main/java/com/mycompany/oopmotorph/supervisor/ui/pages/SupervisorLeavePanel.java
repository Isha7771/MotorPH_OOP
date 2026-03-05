/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.supervisor.ui.pages;

import com.mycompany.oopmotorph.leave.model.LeaveRequest;
import com.mycompany.oopmotorph.leave.model.LeaveStatus;
import com.mycompany.oopmotorph.leave.repository.LeaveCsvRepository;
import com.mycompany.oopmotorph.leave.repository.LeaveRepository;
import com.mycompany.oopmotorph.leave.service.LeaveService;

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

public class SupervisorLeavePanel extends JPanel {

    private final LeaveService leaveService;

    private final JTextField txtSearch = new JTextField(20);
    private final JComboBox<String> cmbStatus =
            new JComboBox<>(new String[]{"All", "PENDING", "APPROVED", "REJECTED"});
    private final JButton btnRefresh = new JButton("Refresh");

    private final JTable tbl = new JTable();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Request ID", "Employee Name", "Date Filed", "Status", "Action"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    public SupervisorLeavePanel() {

        Path leavePath = Paths.get(System.getProperty("user.dir"))
                .resolve("Data")
                .resolve("Leave.csv");

        LeaveRepository repo = new LeaveCsvRepository(leavePath);
        this.leaveService = new LeaveService(repo);

        buildUI();
        wireEvents();
        loadTable();
    }

    private void buildUI() {

        setLayout(new BorderLayout(12,12));
        setBorder(new EmptyBorder(12,12,12,12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT,10,5));
        top.add(new JLabel("Search:"));
        top.add(txtSearch);
        top.add(new JLabel("Status:"));
        top.add(cmbStatus);
        top.add(btnRefresh);

        tbl.setModel(model);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tbl.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                lbl.setText("<html><u>View</u></html>");
                lbl.setForeground(isSelected ? Color.WHITE : Color.BLUE);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                return lbl;
            }
        });

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tbl), BorderLayout.CENTER);
    }

    private void wireEvents() {

        btnRefresh.addActionListener(e -> loadTable());
        txtSearch.addActionListener(e -> loadTable());
        cmbStatus.addActionListener(e -> loadTable());

        tbl.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int col = tbl.columnAtPoint(e.getPoint());
                if (col == 4) {
                    tbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    tbl.setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        tbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tbl.rowAtPoint(e.getPoint());
                int col = tbl.columnAtPoint(e.getPoint());

                if (row >= 0 && col == 4) {
                    String requestId = String.valueOf(model.getValueAt(row, 0));
                    openViewDialog(requestId);
                }
            }
        });
    }

    private void loadTable() {
        model.setRowCount(0);

        String search = txtSearch.getText() == null ? "" : txtSearch.getText().trim();
        String statusText = String.valueOf(cmbStatus.getSelectedItem());

        Optional<LeaveStatus> statusFilter = Optional.empty();
        if (!"All".equalsIgnoreCase(statusText)) {
            statusFilter = Optional.of(LeaveStatus.valueOf(statusText));
        }

        List<LeaveRequest> rows = leaveService.search(search, statusFilter);

        for (LeaveRequest r : rows) {
            model.addRow(new Object[]{
                    r.getRequestId(),
                    r.getEmployeeName(),
                    r.getDateFiled(),
                    r.getStatus().name(),
                    "View"
            });
        }
    }

    private void openViewDialog(String requestId) {

        Optional<LeaveRequest> opt = leaveService.findById(requestId);
        if (opt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Request not found: " + requestId,
                    "Not Found",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        LeaveRequest r = opt.get();

        JDialog dlg = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Leave Request Overview",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dlg.setLayout(new BorderLayout(10,10));

        JPanel content = new JPanel(new GridLayout(0,1,6,6));
        content.setBorder(new EmptyBorder(12,12,12,12));

        content.add(new JLabel("Request ID: " + r.getRequestId()));
        content.add(new JLabel("Employee Name: " + r.getEmployeeName()));
        content.add(new JLabel("Date Request: " + r.getDateRequest()));
        content.add(new JLabel("Leave Type: " + (r.getLeaveType() == null ? "" : r.getLeaveType().name())));
        content.add(new JLabel("Start Date: " + r.getStartDate()));
        content.add(new JLabel("End Date: " + r.getEndDate()));
        content.add(new JLabel("Status: " + r.getStatus().name()));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnApprove = new JButton("Approve");
        JButton btnReject = new JButton("Reject");
        JButton btnClose = new JButton("Close");

        boolean isPending = r.getStatus() == LeaveStatus.PENDING;
        btnApprove.setEnabled(isPending);
        btnReject.setEnabled(isPending);

        btnApprove.addActionListener(e -> {
            try {
                leaveService.approve(r.getRequestId());
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
            try {
                leaveService.reject(r.getRequestId());
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
}