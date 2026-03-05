/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.auth;

import java.util.Locale;

public class RoleMapper {

    public Role fromPosition(String positionRaw) {
        String p = safe(positionRaw).toLowerCase(Locale.ROOT);

        // ADMIN (Chief Officers)
        if (p.equals("chief executive officer")
                || p.equals("chief operating officer")
                || p.equals("chief finance officer")
                || p.equals("chief marketing officer")) {
            return Role.ADMIN;
        }

        // HR
        if (p.equals("hr manager")
                || p.equals("hr assistant")
                || p.equals("hr officer")) {
            return Role.HR_STAFF;
        }

        // PAYROLL
        if (p.equals("payroll manager")
                || p.equals("payroll staff")
                || p.equals("accounting staff")) {
            return Role.PAYROLL_STAFF;
        }

        // default
        return Role.EMPLOYEE;
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}