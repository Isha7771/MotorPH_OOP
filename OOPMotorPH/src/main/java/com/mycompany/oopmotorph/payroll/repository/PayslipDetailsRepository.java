/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.oopmotorph.payroll.repository;

import com.mycompany.oopmotorph.payroll.model.PayslipDetails;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface PayslipDetailsRepository {

    List<PayslipDetails> findAll() throws IOException;

    Optional<PayslipDetails> findByPayslipId(String payslipId) throws IOException;

    void appendAll(List<PayslipDetails> rows) throws IOException;
}
