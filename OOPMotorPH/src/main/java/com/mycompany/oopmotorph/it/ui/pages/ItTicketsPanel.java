package com.mycompany.oopmotorph.it.ui.pages;

import com.mycompany.oopmotorph.ticket.model.Ticket;
import com.mycompany.oopmotorph.ticket.repository.TicketCsvRepository;
import com.mycompany.oopmotorph.ticket.service.TicketService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Path;
import java.util.List;

public class ItTicketsPanel extends JPanel {

    private final String itEmployeeNo;
    private final String itFullName;

    private final TicketService service;
    private final DefaultTableModel model;
    private final JTable table;

    public ItTicketsPanel(String itEmployeeNo, String firstName, String lastName) {
        this.itEmployeeNo = itEmployeeNo;
        this.itFullName = firstName + " " + lastName;

        Path csv = Path.of("Data", "Tickets.csv");
        this.service = new TicketService(new TicketCsvRepository(csv));

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel header = new JLabel("IT Tickets");
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));

        model = new DefaultTableModel(
                new Object[]{
                        "Ticket Id", "Employee No", "Employee Name",
                        "Title", "Status", "Created At", "Updated At", "Handled By"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(24);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> reload());

        JButton btnSetInProgress = new JButton("Set In Progress");
        btnSetInProgress.addActionListener(e -> updateSelectedStatus("IN_PROGRESS"));

        JButton btnSetResolved = new JButton("Set Resolved");
        btnSetResolved.addActionListener(e -> updateSelectedStatus("RESOLVED"));

        JPanel top = new JPanel(new BorderLayout());
        top.add(header, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnRefresh);
        actions.add(btnSetInProgress);
        actions.add(btnSetResolved);
        top.add(actions, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        reload();
    }

    private void reload() {
        List<Ticket> tickets = service.findAll();
        model.setRowCount(0);
        for (Ticket t : tickets) {
            model.addRow(new Object[]{
                    t.getId(),
                    t.getEmployeeNo(),
                    t.getEmployeeFullName(),
                    t.getTitle(),
                    t.getStatus(),
                    t.getCreatedAt(),
                    t.getUpdatedAt(),
                    t.getHandledByName()
            });
        }
    }

    private void updateSelectedStatus(String newStatus) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a ticket first.");
            return;
        }
        String ticketId = (String) model.getValueAt(row, 0);
        try {
            service.updateStatus(ticketId, newStatus, itEmployeeNo, itFullName);
            reload();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update ticket: " + ex.getMessage());
        }
    }
}
