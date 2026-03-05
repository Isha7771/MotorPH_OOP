/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.payroll.model;

public class PayslipRow {

    private String payslipId;
    private String employeeNo;
    private String employeeName;
    private String payDate;   // keep as String for now (matches CSV)
    private String status;    // GENERATED / DISTRIBUTED / DISPUTED
    private String action;    // who distributed / notes

    public PayslipRow() {}

    public PayslipRow(String payslipId, String employeeNo, String employeeName,
                      String payDate, String status, String action) {
        this.payslipId = payslipId;
        this.employeeNo = employeeNo;
        this.employeeName = employeeName;
        this.payDate = payDate;
        this.status = status;
        this.action = action;
    }

    public String getPayslipId() { return payslipId; }
    public void setPayslipId(String payslipId) { this.payslipId = payslipId; }

    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getPayDate() { return payDate; }
    public void setPayDate(String payDate) { this.payDate = payDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}
