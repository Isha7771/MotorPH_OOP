/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.common;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CsvPaths {

    private CsvPaths() {}

    public static Path dataDir() {
        return Paths.get(System.getProperty("user.dir")).resolve("Data");
    }

    public static Path employeeDataCsv() {
        return dataDir().resolve("EmployeeData.csv");
    }

    public static Path usersCsv() {
        return dataDir().resolve("users.csv");
    }

    public static Path payslipsCsv() {
        return dataDir().resolve("Payslips.csv");
    }

    public static Path attendanceCsv() {
        return dataDir().resolve("Attendance.csv");
    }

    public static Path overtimeCsv() {
        return dataDir().resolve("Overtime.csv");
    }

    public static Path leaveCsv() {
        return dataDir().resolve("Leave.csv");
    }
    
    public static Path payslipDetailsCsv() {
         return dataDir().resolve("PayslipDetails.csv");
    }
}
