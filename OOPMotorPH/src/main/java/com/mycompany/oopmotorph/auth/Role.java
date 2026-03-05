/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.mycompany.oopmotorph.auth;

import java.util.Locale;

public enum Role {
    // NOTE: ADMIN users still open SupervisorFrame (Option B)
    ADMIN,
    SUPERVISOR,
    PAYROLL_STAFF,
    EMPLOYEE,
    HR_STAFF;

    public static Role fromString(String text) {
        if (text == null) return EMPLOYEE;

        String s = text.trim().toUpperCase(Locale.ROOT);

        // allow common CSV formats:
        // "Payroll Staff", "PAYROLL_STAFF", "payroll-staff"
        s = s.replace("-", "_").replace(" ", "_");

        return switch (s) {
            case "ADMIN" -> ADMIN;
            case "SUPERVISOR" -> SUPERVISOR;
            case "PAYROLL_STAFF" -> PAYROLL_STAFF;
            case "PAYROLL" -> PAYROLL_STAFF;
            case "HR_STAFF" -> HR_STAFF;
            case "HR" -> HR_STAFF;
            case "EMPLOYEE" -> EMPLOYEE;
            default -> EMPLOYEE; // fallback so app doesn't crash on unknown role text
        };
    }
}