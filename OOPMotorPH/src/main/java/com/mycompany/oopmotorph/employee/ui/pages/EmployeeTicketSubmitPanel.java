/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.employee.ui.pages;

import com.mycompany.oopmotorph.ticket.model.Ticket;
import com.mycompany.oopmotorph.ticket.repository.TicketCsvRepository;
import com.mycompany.oopmotorph.ticket.service.TicketService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Path;
import java.util.List;

public class EmployeeTicketSubmitPanel extends JPanel {

    private final String employeeNo;
    private final String firstName;
    private final String lastName;

    private final TicketService service;

    private final JTextField txtTitle = new JTextField();
    private final JTextArea txtDescription = new JTextArea(4, 20);
    private final DefaultTableModel model;
    private final JTable table;

    public EmployeeTicketSubmitPanel(String employeeNo, String firstName, String lastName) {
        this.employeeNo = employeeNo;
        this.firstName = firstName;
        this.lastName = lastName;

        Path csv = Path.of("Data", "Tickets.csv");
        this.service = new TicketService(new TicketCsvRepository(csv));

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel header = new JLabel("Submit IT Ticket");
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));

        // Form
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        txtTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);

        form.add(new JLabel("Title:"));
        form.add(txtTitle);
        form.add(Box.createVerticalStrut(8));
        form.add(new JLabel("Description:"));
        form.add(new JScrollPane(txtDescription));
        form.add(Box.createVerticalStrut(8));

        JButton btnSubmit = new JButton("Submit Ticket");
        btnSubmit.addActionListener(e -> submitTicket());
        form.add(btnSubmit);

        // Table of own tickets
        model = new DefaultTableModel(
                new Object[]{"Ticket Id", "Title", "Status", "Created At", "Updated At"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(24);

        JPanel top = new JPanel(new BorderLayout());
        top.add(header, BorderLayout.NORTH);
        top.add(form, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        reload();
    }

    private void submitTicket() {
        String title = txtTitle.getText().trim();
        String desc = txtDescription.getText().trim();

        if (title.isBlank() || desc.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please fill in both title and description.");
            return;
        }

        try {
            Ticket t = service.submitTicket(
                    employeeNo,
                    lastName,
                    firstName,
                    title,
                    desc
            );
            JOptionPane.showMessageDialog(this, "Ticket submitted. ID: " + t.getId());
            txtTitle.setText("");
            txtDescription.setText("");
            reload();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to submit ticket: " + ex.getMessage());
        }
    }

    private void reload() {
        List<Ticket> tickets = service.findByEmployee(employeeNo);
        model.setRowCount(0);
        for (Ticket t : tickets) {
            model.addRow(new Object[]{
                    t.getId(),
                    t.getTitle(),
                    t.getStatus(),
                    t.getCreatedAt(),
                    t.getUpdatedAt()
            });
        }
    }
}
