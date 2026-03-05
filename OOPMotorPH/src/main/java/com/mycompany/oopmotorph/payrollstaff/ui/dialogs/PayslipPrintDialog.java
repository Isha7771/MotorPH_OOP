package com.mycompany.oopmotorph.payrollstaff.ui.dialogs;

import com.mycompany.oopmotorph.payroll.model.PayslipDetails;
import com.mycompany.oopmotorph.payroll.model.PayslipRow;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Simple printable payslip viewer for Payroll Staff.
 * Uses pure Swing elements and JTextArea#print.
 */
public class PayslipPrintDialog extends JDialog {

    private final JTextArea area = new JTextArea();
    private final NumberFormat moneyFmt = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

    public PayslipPrintDialog(Window owner, PayslipRow row, PayslipDetails details) {
        super(owner, "Payslip - Print", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setText(buildText(row, details));

        JButton btnPrint = new JButton("Print");
        JButton btnClose = new JButton("Close");

        btnPrint.addActionListener(e -> doPrint());
        btnClose.addActionListener(e -> dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnPrint);
        bottom.add(btnClose);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        root.add(new JScrollPane(area), BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
        setSize(700, 520);
        setLocationRelativeTo(owner);
    }

    private String buildText(PayslipRow row, PayslipDetails d) {
        StringBuilder sb = new StringBuilder();
        sb.append("MOTORPH PAYSLIP\n");
        sb.append("==============================\n");
        if (row != null) {
            sb.append("Payslip ID   : ").append(nz(row.getPayslipId())).append("\n");
            sb.append("Employee #   : ").append(nz(row.getEmployeeNo())).append("\n");
            sb.append("Employee Name: ").append(nz(row.getEmployeeName())).append("\n");
            sb.append("Pay Date     : ").append(nz(row.getPayDate())).append("\n");
            sb.append("Status       : ").append(nz(row.getStatus())).append("\n");
            sb.append("Action       : ").append(nz(row.getAction())).append("\n");
        }
        sb.append("------------------------------\n");
        if (d != null) {
            sb.append("Period From  : ").append(nz(d.getPeriodFrom())).append("\n");
            sb.append("Period To    : ").append(nz(d.getPeriodTo())).append("\n");
            sb.append("Total Hours  : ").append(d.getTotalHours()).append("\n\n");

            sb.append("Gross Pay    : ").append(moneyFmt.format(d.getGross())).append("\n\n");
            sb.append("Deductions\n");
            sb.append("  SSS        : ").append(moneyFmt.format(d.getSss())).append("\n");
            sb.append("  PhilHealth : ").append(moneyFmt.format(d.getPhilHealth())).append("\n");
            sb.append("  Pag-IBIG   : ").append(moneyFmt.format(d.getPagibig())).append("\n");
            sb.append("  Tax        : ").append(moneyFmt.format(d.getTax())).append("\n");
            sb.append("Total Deduct.: ").append(moneyFmt.format(d.getTotalDeductions())).append("\n\n");
            sb.append("NET PAY      : ").append(moneyFmt.format(d.getNetPay())).append("\n");
        } else {
            sb.append("(No payslip details found in PayslipDetails.csv for this payslip.)\n");
        }
        sb.append("==============================\n");
        sb.append("This document is system-generated.\n");
        return sb.toString();
    }

    private void doPrint() {
        try {
            boolean ok = area.print();
            if (!ok) {
                JOptionPane.showMessageDialog(this, "Printing was cancelled.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to print.\n" + ex.getMessage(),
                    "Print Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String nz(String s) {
        return s == null ? "" : s.trim();
    }
}
