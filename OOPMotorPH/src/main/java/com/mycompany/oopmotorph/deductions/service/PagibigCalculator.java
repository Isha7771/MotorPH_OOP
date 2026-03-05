/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.deductions.service;

public class PagibigCalculator implements DeductionCalculator {

    private static final double RATE = 0.02; // 2%
    private static final double CAP = 100.0;

    @Override
    public double compute(double gross) {
        if (gross <= 0) return 0.0;

        double val = gross * RATE;
        if (val > CAP) val = CAP;

        return round2(val);
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}