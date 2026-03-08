package com.mycompany.oopmotorph.it.ui;

import com.mycompany.oopmotorph.it.ui.pages.ManageUsersPanel;
import com.mycompany.oopmotorph.it.ui.pages.ItTicketsPanel;

import javax.swing.*;
import java.awt.*;

public class ItFrame extends JFrame {

    private final String employeeNo;
    private final String lastName;
    private final String firstName;

    private final JPanel contentPanel = new JPanel(new CardLayout());

    private static final String CARD_MANAGE_USERS = "manageUsers";
    private static final String CARD_IT_TICKETS = "itTickets";

    public ItFrame(String employeeNo, String lastName, String firstName) {
        super("MotorPH - IT Dashboard");

        this.employeeNo = employeeNo;
        this.lastName = lastName;
        this.firstName = firstName;

        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Left nav
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(new Color(20, 40, 90));
        nav.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("<html><b>IT Staff</b><br/>"
                + firstName + " " + lastName + "</html>");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnManageUsers = new JButton("Manage Users");
        styleNavButton(btnManageUsers);
        btnManageUsers.addActionListener(e -> showCard(CARD_MANAGE_USERS));

        JButton btnTickets = new JButton("IT Tickets");
        styleNavButton(btnTickets);
        btnTickets.addActionListener(e -> showCard(CARD_IT_TICKETS));

        JButton btnLogout = new JButton("Logout");
        styleNavButton(btnLogout);
        btnLogout.addActionListener(e -> {
            dispose();
            // optional: balik sa RoleSelectionFrame
            new com.mycompany.oopmotorph.ui.RoleSelectionFrame().setVisible(true);
        });

        nav.add(lblTitle);
        nav.add(Box.createVerticalStrut(20));
        nav.add(btnManageUsers);
        nav.add(Box.createVerticalStrut(10));
        nav.add(btnTickets);
        nav.add(Box.createVerticalGlue());
        nav.add(btnLogout);

        // Center pages
        JPanel manageUsersPanel = new ManageUsersPanel();
        JPanel itTicketsPanel = new ItTicketsPanel(employeeNo, firstName, lastName);

        contentPanel.add(manageUsersPanel, CARD_MANAGE_USERS);
        contentPanel.add(itTicketsPanel, CARD_IT_TICKETS);

        add(nav, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void styleNavButton(JButton btn) {
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setBackground(new Color(13, 40, 100));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
    }

    private void showCard(String cardName) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, cardName);
    }
}
