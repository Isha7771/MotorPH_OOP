/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.payroll.model;

import java.util.Objects;

public final class PayslipKey {

    private final String payslipId;
    private final String employeeNo;

    public PayslipKey(String payslipId, String employeeNo) {
        this.payslipId = safe(payslipId);
        this.employeeNo = safe(employeeNo);

        if (this.payslipId.isEmpty() || this.employeeNo.isEmpty()) {
            throw new IllegalArgumentException("PayslipKey requires payslipId and employeeNo.");
        }
    }

    public String getPayslipId() { return payslipId; }
    public String getEmployeeNo() { return employeeNo; }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PayslipKey)) return false;
        PayslipKey that = (PayslipKey) o;
        return payslipId.equals(that.payslipId) && employeeNo.equals(that.employeeNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(payslipId, employeeNo);
    }

    @Override
    public String toString() {
        return "PayslipKey{" + "payslipId='" + payslipId + '\'' +
                ", employeeNo='" + employeeNo + '\'' + '}';
    }
}