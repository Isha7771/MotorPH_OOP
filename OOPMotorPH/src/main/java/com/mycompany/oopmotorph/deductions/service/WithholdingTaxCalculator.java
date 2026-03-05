/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.deductions.service;

public class WithholdingTaxCalculator implements DeductionCalculator {

    @Override
    public double compute(double gross) {
        if (gross <= 0) return 0.0;

        double tax;

        if (gross <= 20833.0) {
            tax = 0.0;
        } else if (gross <= 33333.0) {
            tax = (gross - 20833.0) * 0.20;
        } else if (gross <= 66667.0) {
            tax = 2500.0 + (gross - 33333.0) * 0.25;
        } else {
            tax = 10833.33 + (gross - 66667.0) * 0.30;
        }

        if (tax < 0) tax = 0;
        return round2(tax);
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}