/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.employee.ui;

import com.mycompany.oopmotorph.employee.ui.pages.EmployeeAttendancePage;
import com.mycompany.oopmotorph.employee.ui.pages.EmployeeLeaveRequestPage;
import com.mycompany.oopmotorph.employee.ui.pages.EmployeeOvertimeRequestPage;
import com.mycompany.oopmotorph.employee.ui.pages.EmployeePayslipPage;
import com.mycompany.oopmotorph.timesheet.repository.TimeLogCsvRepository;
import com.mycompany.oopmotorph.timesheet.service.TimeLogService;
import com.mycompany.oopmotorph.ui.RoleSelectionFrame;
import com.mycompany.oopmotorph.ui.UserHeaderSupport;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EmployeeFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final JLabel lblTitle = new JLabel("Employee Dashboard");
    private final JLabel lblUser = new JLabel();

    // ===== Logged-in user info =====
    private final String employeeNo;
    private final String lastName;
    private final String firstName;

    // ===== Time In/Out Support =====
    private final Path timeLogCsvPath =
            Paths.get(System.getProperty("user.dir"))
                    .resolve("Data")
                    .resolve("DataTimeLogs.csv");

    private final TimeLogService timeLogService =
            new TimeLogService(new TimeLogCsvRepository(timeLogCsvPath));

    private final JButton btnTimeIn = new JButton("Time In");
    private final JButton btnTimeOut = new JButton("Time Out");

    // Card names
    private static final String PAGE_ATTENDANCE = "attendance";
    private static final String PAGE_LEAVE      = "leave";
    private static final String PAGE_OVERTIME   = "overtime";
    private static final String PAGE_PAYSLIP    = "payslip";

    public EmployeeFrame(String employeeNo, String lastName, String firstName) {
        super("MotorPH - Employee");

        this.employeeNo = employeeNo;
        this.lastName = lastName;
        this.firstName = firstName;

        lblUser.setText(firstName + " " + lastName + " (" + employeeNo + ")");
        UserHeaderSupport.makeClickable(lblUser, this, employeeNo);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(buildRoot());
        registerPages();

        showPage(PAGE_ATTENDANCE, "Attendance");
    }

    private JPanel buildRoot() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(contentPanel, BorderLayout.CENTER);
        return root;
    }

    private JComponent buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(13, 40, 100));
        top.setBorder(new EmptyBorder(12, 16, 12, 16));

        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        top.add(lblTitle, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("SansSerif", Font.PLAIN, 14));

        btnTimeIn.setFocusPainted(false);
        btnTimeOut.setFocusPainted(false);

        btnTimeIn.addActionListener(e -> handleTimeIn());
        btnTimeOut.addActionListener(e -> handleTimeOut());

        rightPanel.add(lblUser);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(btnTimeIn);
        rightPanel.add(btnTimeOut);

        top.add(rightPanel, BorderLayout.EAST);
        return top;
    }

    private JComponent buildSidebar() {
        JPanel side = new JPanel();
        side.setBackground(new Color(245, 247, 252));
        side.setPreferredSize(new Dimension(230, 0));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(new EmptyBorder(16, 12, 16, 12));

        side.add(sideTitle("Employee"));
        side.add(Box.createVerticalStrut(14));

        side.add(navButton("Attendance", PAGE_ATTENDANCE));
        side.add(Box.createVerticalStrut(10));
        side.add(navButton("Leave Request", PAGE_LEAVE));
        side.add(Box.createVerticalStrut(10));
        side.add(navButton("Overtime Request", PAGE_OVERTIME));
        side.add(Box.createVerticalStrut(10));
        side.add(navButton("Payslip", PAGE_PAYSLIP));

        side.add(Box.createVerticalGlue());
        side.add(Box.createVerticalStrut(14));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogout.addActionListener(e -> logout());
        side.add(btnLogout);

        return side;
    }

    private JLabel sideTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 16));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JButton navButton(String text, String pageKey) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setFocusPainted(false);
        btn.addActionListener(e -> showPage(pageKey, text));
        return btn;
    }

    private void registerPages() {
        contentPanel.add(new EmployeeAttendancePage(employeeNo), PAGE_ATTENDANCE);
        contentPanel.add(new EmployeeLeaveRequestPage(firstName + " " + lastName), PAGE_LEAVE);
        contentPanel.add(new EmployeeOvertimeRequestPage(firstName + " " + lastName), PAGE_OVERTIME);
        contentPanel.add(new EmployeePayslipPage(employeeNo, firstName + " " + lastName), PAGE_PAYSLIP);
    }

    private void showPage(String pageKey, String title) {
        lblTitle.setText(title);
        cardLayout.show(contentPanel, pageKey);
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(() -> {
                new RoleSelectionFrame().setVisible(true);
                dispose();
            });
        }
    }

    private void handleTimeIn() {
        try {
            System.out.println("TimeLog CSV path = " + timeLogCsvPath.toAbsolutePath());
            timeLogService.timeIn(employeeNo, lastName, firstName);
            JOptionPane.showMessageDialog(this, "Time In recorded!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Unable to Time In:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleTimeOut() {
        try {
            System.out.println("TimeLog CSV path = " + timeLogCsvPath.toAbsolutePath());
            timeLogService.timeOut(employeeNo);
            JOptionPane.showMessageDialog(this, "Time Out recorded!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Unable to Time Out:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}