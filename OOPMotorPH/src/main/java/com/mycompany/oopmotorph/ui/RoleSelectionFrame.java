/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.ui;

import com.mycompany.oopmotorph.auth.Role;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RoleSelectionFrame extends JFrame {

    public RoleSelectionFrame() {
        super("MotorPH - Role Selection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        // Root: two columns
        JPanel root = new JPanel(new GridLayout(1, 2));
        root.add(buildLeftPanel());
        root.add(buildRightPanel());
        setContentPane(root);
    }

    private JPanel buildLeftPanel() {
        JPanel left = new JPanel();
        left.setBackground(Color.WHITE);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(new EmptyBorder(50, 60, 50, 60));

        JLabel title = new JLabel("<html><div style='text-align:left;'>Welcome to MotorPH<br/>Role Selection Portal</div></html>");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("CHOOSE WHAT ROLES DO YOU WISH TO VIEW?");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(20, 40, 90));
        subtitle.setBorder(new EmptyBorder(20, 0, 30, 0));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(title);
        left.add(subtitle);

        left.add(makeRoleButton("Supervisor", Role.SUPERVISOR));
        left.add(Box.createVerticalStrut(16));
        left.add(makeRoleButton("Payroll Staff", Role.PAYROLL_STAFF));
        left.add(Box.createVerticalStrut(16));
        left.add(makeRoleButton("Employee", Role.EMPLOYEE));
        left.add(Box.createVerticalStrut(16));
        left.add(makeRoleButton("HR Staff", Role.HR_STAFF));
        left.add(makeRoleButton("IT Staff", Role.ITSTAFF));

        left.add(Box.createVerticalGlue());
        return left;
    }

    private JPanel buildRightPanel() {
        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(new Color(14, 46, 120)); // simple blue

        // Optional: show an image if you have one in resources
        JLabel placeholder = new JLabel("MotorPH", SwingConstants.CENTER);
        placeholder.setForeground(Color.WHITE);
        placeholder.setFont(new Font("SansSerif", Font.BOLD, 40));
        right.add(placeholder, BorderLayout.CENTER);

        return right;
    }

    private JButton makeRoleButton(String text, Role role) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(13, 40, 100));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);

        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        btn.addActionListener(e -> openLogin(role));
        return btn;
    }

    private void openLogin(Role selectedRole) {
        // Next screen
        SwingUtilities.invokeLater(() -> {
            new LoginFrame(selectedRole).setVisible(true);
            dispose();
        });
    }
}
