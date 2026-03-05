/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.mycompany.oopmotorph.overtime.model;

public enum OvertimeStatus {
    PENDING,
    APPROVED,
    REJECTED;

    public static OvertimeStatus fromString(String s) {
        if (s == null) return PENDING;
        try {
            return OvertimeStatus.valueOf(s.trim().toUpperCase());
        } catch (Exception e) {
            return PENDING;
        }
    }
}
