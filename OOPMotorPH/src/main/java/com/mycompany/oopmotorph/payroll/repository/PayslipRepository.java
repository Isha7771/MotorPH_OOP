/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.oopmotorph.payroll.repository;

import com.mycompany.oopmotorph.payroll.model.PayslipKey;
import com.mycompany.oopmotorph.payroll.model.PayslipRow;

import java.io.IOException;
import java.util.List;

public interface PayslipRepository {

    List<PayslipRow> findAll() throws IOException;

    List<PayslipRow> findByEmployeeNo(String employeeNo) throws IOException;

    void appendAll(List<PayslipRow> rows) throws IOException;

    String nextPayslipId() throws IOException;

    void updateStatus(String payslipId, String newStatus, String action) throws IOException;

    void updateStatus(String payslipId, String employeeNo, String newStatus, String action) throws IOException;

    void updateStatusBulk(List<PayslipKey> keys, String newStatus, String action) throws IOException;
}