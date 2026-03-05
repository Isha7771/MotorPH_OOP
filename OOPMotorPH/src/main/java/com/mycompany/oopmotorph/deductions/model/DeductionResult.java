/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.deductions.model;

public class DeductionResult {

    private final DeductionType type;
    private final double employeeShare;
    private final double employerShare;

    public DeductionResult(DeductionType type, double employeeShare, double employerShare) {
        this.type = type;
        this.employeeShare = employeeShare;
        this.employerShare = employerShare;
    }

    public DeductionType getType() { return type; }
    public double getEmployeeShare() { return employeeShare; }
    public double getEmployerShare() { return employerShare; }

    public double getTotal() {
        return employeeShare + employerShare;
    }
}