/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.timesheet.service;

import com.mycompany.oopmotorph.timesheet.model.TimeLog;
import com.mycompany.oopmotorph.timesheet.repository.TimeLogRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class TimeLogService {
    private final TimeLogRepository repo;

    public TimeLogService(TimeLogRepository repo) {
        this.repo = Objects.requireNonNull(repo);
    }

    public List<TimeLog> getAll() {
        return repo.findAll().stream()
                .sorted(Comparator.comparing(TimeLog::getDate).reversed()
                        .thenComparing(TimeLog::getEmployeeNumber))
                .toList();
    }

    public void timeIn(String empNo, String last, String first) {
        repo.upsertTimeIn(empNo, last, first, LocalDate.now(), LocalTime.now().withSecond(0).withNano(0));
    }

    public void timeOut(String empNo) {
        repo.upsertTimeOut(empNo, LocalDate.now(), LocalTime.now().withSecond(0).withNano(0));
    }
}