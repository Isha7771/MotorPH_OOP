/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.oopmotorph.payroll.repository;

public interface PayslipDisputeRepository {

    void append(
        String disputeId,
        String payslipId,
        String employeeNo,
        String employeeName,
        String dateFiled,
        String reason
    );
}