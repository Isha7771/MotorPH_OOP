/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.oopmotorph.overtime.repository;

import com.mycompany.oopmotorph.overtime.model.OvertimeRequest;

import java.util.List;
import java.util.Optional;

public interface OvertimeRepository {
    List<OvertimeRequest> findAll();
    Optional<OvertimeRequest> findById(String requestId);
    void saveAll(List<OvertimeRequest> requests);
    void add(OvertimeRequest request);

    List<OvertimeRequest> findByEmployeeNo(String employeeKey);
    void append(OvertimeRequest request);
}