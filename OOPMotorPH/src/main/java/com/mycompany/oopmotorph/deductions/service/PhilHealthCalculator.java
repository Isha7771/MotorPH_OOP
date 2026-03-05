/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.deductions.service;

public class PhilHealthCalculator implements DeductionCalculator {

    private static final double TOTAL_RATE = 0.025; // 2.5% total (placeholder)
    private static final double EMPLOYEE_SHARE = 0.5; // employee half
    private static final double CAP = 900.0;

    @Override
    public double compute(double gross) {
        if (gross <= 0) return 0.0;

        double val = gross * TOTAL_RATE * EMPLOYEE_SHARE;
        if (val > CAP) val = CAP;

        return round2(val);
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
