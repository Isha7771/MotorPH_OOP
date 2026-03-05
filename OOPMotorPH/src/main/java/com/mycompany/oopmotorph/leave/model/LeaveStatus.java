/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.mycompany.oopmotorph.leave.model;

public enum LeaveStatus {
    PENDING,
    APPROVED,
    REJECTED,
    COMPLETE;

    public static LeaveStatus fromString(String s) {
        if (s == null) return PENDING;
        try {
            return LeaveStatus.valueOf(s.trim().toUpperCase());
        } catch (Exception e) {
            return PENDING;
        }
    }
}
