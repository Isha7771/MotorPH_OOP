/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.ui;

import com.mycompany.oopmotorph.auth.Role;
import com.mycompany.oopmotorph.auth.AuthService;
import com.mycompany.oopmotorph.auth.CsvUserRepository;
import com.mycompany.oopmotorph.auth.User;
import com.mycompany.oopmotorph.common.CsvPaths;
import com.mycompany.oopmotorph.employee.ui.EmployeeFrame;
import com.mycompany.oopmotorph.hr.ui.HrFrame;
import com.mycompany.oopmotorph.payrollstaff.ui.PayrollStaffFrame;
import com.mycompany.oopmotorph.supervisor.ui.SupervisorFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final Role role;

    // ✅ Make these fields so openDashboard() can access them
    private final JTextField txtUser = new JTextField();
    private final JPasswordField txtPass = new JPasswordField();

    public LoginFrame(Role role) {
        super("MotorPH - Login");
        this.role = role;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(buildUI());
    }

    private JPanel buildUI() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Login");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLabel = new JLabel("Role: " + role);
        roleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        roleLabel.setBorder(new EmptyBorder(8, 0, 20, 0));
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(roleLabel);

        panel.add(label("Username"));
        panel.add(field(txtUser));
        panel.add(Box.createVerticalStrut(12));

        panel.add(label("Password"));
        panel.add(field(txtPass));
        panel.add(Box.createVerticalStrut(22));

        JButton btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(13, 40, 100));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        btnLogin.addActionListener(e -> openDashboard(role));

        JButton btnBack = new JButton("Back");
        btnBack.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBack.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnBack.addActionListener(e -> {
            new RoleSelectionFrame().setVisible(true);
            dispose();
        });

        panel.add(btnLogin);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnBack);

        return panel;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 0, 6, 0));
        return l;
    }

    private JComponent field(JComponent c) {
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        return c;
    }

    private void openDashboard(Role role) {
        SwingUtilities.invokeLater(() -> {
            String username = txtUser.getText().trim();
            String password = new String(txtPass.getPassword());

            if (username.isBlank()) {
                JOptionPane.showMessageDialog(this, "Please enter your Username (Employee No).");
                return;
            }

            try {
                AuthService auth = new AuthService(new CsvUserRepository(CsvPaths.usersCsv()));
                var opt = auth.login(username, password, role);
                if (opt.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid username/password or you selected the wrong portal role.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                User user = opt.get();
                JFrame next;

                // Option B: ADMIN opens SupervisorFrame
                if (user.getRole() == Role.ADMIN || user.getRole() == Role.SUPERVISOR) {
                    next = new SupervisorFrame(user.getEmployeeNo(), user.getLastName(), user.getFirstName());
                } else if (user.getRole() == Role.HR_STAFF) {
                    next = new HrFrame(user.getEmployeeNo(), user.getLastName(), user.getFirstName());
                } else if (user.getRole() == Role.PAYROLL_STAFF) {
                    next = new PayrollStaffFrame(user.getEmployeeNo(), user.getLastName(), user.getFirstName());
                } else if (user.getRole() == Role.ITSTAFF) {
                    next = new ItFrame(user.getEmployeeNo(), user.getLastName(), user.getFirstName());
                } else {
                    next = new EmployeeFrame(user.getEmployeeNo(), user.getLastName(), user.getFirstName());
                }


                next.setVisible(true);
                dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Login error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
