/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.oopmotorph.leave.repository;

import com.mycompany.oopmotorph.leave.model.LeaveRequest;

import java.util.List;
import java.util.Optional;

public interface LeaveRepository {
    List<LeaveRequest> findAll();
    Optional<LeaveRequest> findById(String requestId);
    void saveAll(List<LeaveRequest> requests);
    void add(LeaveRequest request);

    List<LeaveRequest> findByEmployeeNo(String employeeKey);
    void append(LeaveRequest request);
}