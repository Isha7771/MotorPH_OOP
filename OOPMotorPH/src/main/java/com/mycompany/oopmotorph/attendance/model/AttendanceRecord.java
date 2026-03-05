/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.attendance.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceRecord {

    private final String employeeId;
    private final String employeeName;
    private final LocalDate date;
    private final String position;
    private final LocalTime timeIn;
    private final LocalTime timeOut;

    private final AttendanceStatus status;
    private final String remarks;

    public AttendanceRecord(
            String employeeId,
            String employeeName,
            LocalDate date,
            String position,
            LocalTime timeIn,
            LocalTime timeOut,
            AttendanceStatus status,
            String remarks
    ) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.date = date;
        this.position = position;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.status = status;
        this.remarks = remarks;
    }

    public String getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public LocalDate getDate() { return date; }
    public String getPosition() { return position; }
    public LocalTime getTimeIn() { return timeIn; }
    public LocalTime getTimeOut() { return timeOut; }
    public AttendanceStatus getStatus() { return status; }
    public String getRemarks() { return remarks; }

    public double getTotalHours() {
        if (timeIn == null || timeOut == null) return 0.0;
        long minutes = Duration.between(timeIn, timeOut).toMinutes();
        if (minutes < 0) return 0.0;
        return minutes / 60.0;
    }
}