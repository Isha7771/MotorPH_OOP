/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.oopmotorph.attendance.repository;

import com.mycompany.oopmotorph.attendance.model.AttendanceRecord;
import java.util.List;

public interface AttendanceRepository {
    List<AttendanceRecord> findAll();
}