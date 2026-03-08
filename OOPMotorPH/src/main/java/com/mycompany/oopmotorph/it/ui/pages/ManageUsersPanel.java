package com.mycompany.oopmotorph.it.ui.pages;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageUsersPanel extends JPanel {

    private final DefaultTableModel model;
    private final JTable table;

    public ManageUsersPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel header = new JLabel("Manage User Credentials");
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));

        model = new DefaultTableModel(
                new Object[]{"Employee No", "Last Name", "First Name", "Role", "Active", "Username"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Later: allow editing specific columns
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(24);

        JPanel top = new JPanel(new BorderLayout());
        top.add(header, BorderLayout.WEST);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}
