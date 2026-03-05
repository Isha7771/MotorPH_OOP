/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.oopmotorph.employee.repository;

import com.mycompany.oopmotorph.employee.model.EmployeeRecord;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {

    List<EmployeeRecord> findAll() throws IOException;

    Optional<EmployeeRecord> findByEmployeeNo(String employeeNo) throws IOException;

    void add(EmployeeRecord employee) throws IOException;

    void update(EmployeeRecord employee) throws IOException;

    void delete(String employeeNo) throws IOException;
}