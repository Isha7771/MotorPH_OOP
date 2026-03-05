/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.deductions.model;

public class DeductionItem {

    private final double minInclusive;
    private final double maxInclusive;
    private final double employeeShare;
    private final double employerShare;

    public DeductionItem(double minInclusive, double maxInclusive,
                         double employeeShare, double employerShare) {
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
        this.employeeShare = employeeShare;
        this.employerShare = employerShare;
    }

    public double getMinInclusive() { return minInclusive; }
    public double getMaxInclusive() { return maxInclusive; }
    public double getEmployeeShare() { return employeeShare; }
    public double getEmployerShare() { return employerShare; }
}