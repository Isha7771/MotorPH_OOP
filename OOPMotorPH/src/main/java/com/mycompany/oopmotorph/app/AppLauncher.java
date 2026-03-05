/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.app;

import com.mycompany.oopmotorph.auth.UserCsvBootstrapper;
import com.mycompany.oopmotorph.common.CsvPaths;
import com.mycompany.oopmotorph.ui.RoleSelectionFrame;

import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // ✅ Ensure Data/users.csv exists for ALL employees in Data/EmployeeData.csv
                UserCsvBootstrapper.ensureUsersCsv(CsvPaths.usersCsv(), CsvPaths.employeeDataCsv());
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Startup error while generating users.csv: " + e.getMessage() +
                                "\n\nMake sure Data/EmployeeData.csv exists.",
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            new RoleSelectionFrame().setVisible(true);
        });
    }
}
