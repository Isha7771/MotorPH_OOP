/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.payroll.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class PayrollPeriod {

    private final LocalDate start;
    private final LocalDate end;

    public PayrollPeriod(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("PayrollPeriod dates must not be null.");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("PayrollPeriod end date must be >= start date.");
        }
        this.start = start;
        this.end = end;
    }

    public LocalDate getStart() { return start; }
    public LocalDate getEnd() { return end; }

    public String label() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d, yyyy");
        return start.format(fmt) + " - " + end.format(fmt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PayrollPeriod)) return false;
        PayrollPeriod that = (PayrollPeriod) o;
        return start.equals(that.start) && end.equals(that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "PayrollPeriod{" + "start=" + start + ", end=" + end + '}';
    }
}