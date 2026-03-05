/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.attendance.model;

public class EmployeeMini {
    private final String employeeId;
    private final String employeeName;
    private final String position;

    public EmployeeMini(String employeeId, String employeeName, String position) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.position = position;
    }

    public String getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getPosition() { return position; }
}
