/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.payroll.model;

public class PayslipDetails {

    private String payslipId;
    private String employeeNo;
    private String periodFrom;   // keep as text for csv simplicity
    private String periodTo;
    private double totalHours;

    private double gross;
    private double sss;
    private double philHealth;
    private double pagibig;
    private double tax;

    private double totalDeductions;
    private double netPay;

    public String getPayslipId() { return payslipId; }
    public void setPayslipId(String payslipId) { this.payslipId = payslipId; }

    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }

    public String getPeriodFrom() { return periodFrom; }
    public void setPeriodFrom(String periodFrom) { this.periodFrom = periodFrom; }

    public String getPeriodTo() { return periodTo; }
    public void setPeriodTo(String periodTo) { this.periodTo = periodTo; }

    public double getTotalHours() { return totalHours; }
    public void setTotalHours(double totalHours) { this.totalHours = totalHours; }

    public double getGross() { return gross; }
    public void setGross(double gross) { this.gross = gross; }

    public double getSss() { return sss; }
    public void setSss(double sss) { this.sss = sss; }

    public double getPhilHealth() { return philHealth; }
    public void setPhilHealth(double philHealth) { this.philHealth = philHealth; }

    public double getPagibig() { return pagibig; }
    public void setPagibig(double pagibig) { this.pagibig = pagibig; }

    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }

    public double getTotalDeductions() { return totalDeductions; }
    public void setTotalDeductions(double totalDeductions) { this.totalDeductions = totalDeductions; }

    public double getNetPay() { return netPay; }
    public void setNetPay(double netPay) { this.netPay = netPay; }
}