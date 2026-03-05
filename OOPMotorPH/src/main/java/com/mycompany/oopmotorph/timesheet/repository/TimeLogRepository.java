/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.oopmotorph.timesheet.repository;

import com.mycompany.oopmotorph.timesheet.model.TimeLog;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TimeLogRepository {
    List<TimeLog> findAll();

    void upsertTimeIn(String employeeNo, String lastName, String firstName, LocalDate date, LocalTime timeIn);
    void upsertTimeOut(String employeeNo, LocalDate date, LocalTime timeOut);
}