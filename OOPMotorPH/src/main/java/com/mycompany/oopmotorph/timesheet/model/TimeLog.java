/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.timesheet.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimeLog {
    private final String employeeNumber;
    private final String lastName;
    private final String firstName;
    private final LocalDate date;
    private final LocalTime timeIn;
    private final LocalTime timeOut;

    public TimeLog(String employeeNumber, String lastName, String firstName,
                   LocalDate date, LocalTime timeIn, LocalTime timeOut) {
        this.employeeNumber = employeeNumber == null ? "" : employeeNumber.trim();
        this.lastName = lastName == null ? "" : lastName.trim();
        this.firstName = firstName == null ? "" : firstName.trim();
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    public String getEmployeeNumber() { return employeeNumber; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public LocalDate getDate() { return date; }
    public LocalTime getTimeIn() { return timeIn; }
    public LocalTime getTimeOut() { return timeOut; }

    public String getFullName() {
        String ln = lastName.isBlank() ? "" : lastName;
        String fn = firstName.isBlank() ? "" : firstName;
        return (ln + ", " + fn).trim().replaceAll("^,\\s*", "");
    }

    public String getTotalHoursText() {
        if (timeIn == null || timeOut == null) return "";
        long mins = java.time.Duration.between(timeIn, timeOut).toMinutes();
        if (mins < 0) return "";
        return String.format("%d:%02d", mins / 60, mins % 60);
    }
}