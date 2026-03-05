/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.mycompany.oopmotorph.leave.model;

public enum LeaveType {
    VACATION,
    SICK;

    public static LeaveType fromString(String s) {
        if (s == null) return VACATION;
        String v = s.trim().toUpperCase();
        if (v.contains("SICK")) return SICK;
        return VACATION;
    }
}