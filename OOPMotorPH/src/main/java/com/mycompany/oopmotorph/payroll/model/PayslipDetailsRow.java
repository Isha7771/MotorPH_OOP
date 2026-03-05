/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.payroll.model;

public class PayslipDetailsRow {

    private final String payslipId;
    private final String employeeNo;
    private final String employeeName;
    private final String payDate;

    private final String grossPay;
    private final String sss;
    private final String philHealth;
    private final String pagIbig;
    private final String withholdingTax;
    private final String totalDeductions;
    private final String netPay;

    public PayslipDetailsRow(String payslipId,
                             String employeeNo,
                             String employeeName,
                             String payDate,
                             String grossPay,
                             String sss,
                             String philHealth,
                             String pagIbig,
                             String withholdingTax,
                             String totalDeductions,
                             String netPay) {
        this.payslipId = payslipId;
        this.employeeNo = employeeNo;
        this.employeeName = employeeName;
        this.payDate = payDate;
        this.grossPay = grossPay;
        this.sss = sss;
        this.philHealth = philHealth;
        this.pagIbig = pagIbig;
        this.withholdingTax = withholdingTax;
        this.totalDeductions = totalDeductions;
        this.netPay = netPay;
    }

    public String getPayslipId() { return payslipId; }
    public String getEmployeeNo() { return employeeNo; }
    public String getEmployeeName() { return employeeName; }
    public String getPayDate() { return payDate; }

    public String getGrossPay() { return grossPay; }
    public String getSss() { return sss; }
    public String getPhilHealth() { return philHealth; }
    public String getPagIbig() { return pagIbig; }
    public String getWithholdingTax() { return withholdingTax; }
    public String getTotalDeductions() { return totalDeductions; }
    public String getNetPay() { return netPay; }
}