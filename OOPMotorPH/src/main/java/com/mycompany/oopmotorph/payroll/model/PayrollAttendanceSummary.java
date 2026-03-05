/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.payrollstaff.model;

public class PayrollAttendanceSummary {

    private final String employeeId;
    private final String employeeName;

    private final int presentCount;
    private final int lateCount;
    private final int absentCount;

    private final double totalHours;

    public PayrollAttendanceSummary(String employeeId, String employeeName,
                                    int presentCount, int lateCount, int absentCount,
                                    double totalHours) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.presentCount = presentCount;
        this.lateCount = lateCount;
        this.absentCount = absentCount;
        this.totalHours = totalHours;
    }

    public String getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }

    public int getPresentCount() { return presentCount; }
    public int getLateCount() { return lateCount; }
    public int getAbsentCount() { return absentCount; }

    public double getTotalHours() { return totalHours; }
}
