package com.mycompany.oopmotorph.ui;

import com.mycompany.oopmotorph.employee.ui.dialogs.EmployeeProfileDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Small helper to make the top-right user label clickable across dashboards.
 */
public class UserHeaderSupport {

    private UserHeaderSupport() {}

    public static void makeClickable(JLabel lblUser, Window owner, String employeeNo) {
        if (lblUser == null || employeeNo == null || employeeNo.isBlank()) return;

        lblUser.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblUser.setToolTipText("View your employee record");

        lblUser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                EmployeeProfileDialog dlg = new EmployeeProfileDialog(owner, employeeNo);
                dlg.setVisible(true);
            }
        });
    }
}
