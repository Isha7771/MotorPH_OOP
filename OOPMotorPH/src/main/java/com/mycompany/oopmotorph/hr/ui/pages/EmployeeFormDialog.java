/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.hr.ui.pages;

import com.mycompany.oopmotorph.employee.model.EmployeeRecord;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.ParseException;

public class EmployeeFormDialog extends JDialog {

    // Auto-generated on Add; read-only on Edit.
    private final JTextField txtEmpNo = new JTextField(15);
    private final JTextField txtLast = new JTextField(15);
    private final JTextField txtFirst = new JTextField(15);
    private final JTextField txtBday = new JTextField(15); // M/d/yyyy
    private final JTextField txtAddress = new JTextField(20);
    private final JFormattedTextField txtPhone = new JFormattedTextField(createMask("###-###-###"));

    // Masked inputs so users can't type the wrong format
    private final JFormattedTextField txtSss = new JFormattedTextField(createMask("##-#######-#"));
    private final JFormattedTextField txtPhil = new JFormattedTextField(createMask("##-#########-#"));
    private final JFormattedTextField txtTin = new JFormattedTextField(createMask("###-###-###-###"));
    private final JFormattedTextField txtPagibig = new JFormattedTextField(createMask("####-####-####"));

    private final JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Regular", "Probationary"});
    private final JTextField txtPosition = new JTextField(15);
    private final JTextField txtSupervisor = new JTextField(15);

    private final JTextField txtBasicSalary = new JTextField(12);
    private final JTextField txtGross = new JTextField(12);
    private final JTextField txtHourly = new JTextField(12);

    private final JButton btnSave = new JButton("Save");
    private final JButton btnCancel = new JButton("Cancel");

    private EmployeeRecord result; // null if cancelled
    private final boolean editMode;

    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("M/d/yyyy");

    public EmployeeFormDialog(Window owner, String title, EmployeeRecord existing) {
        this(owner, title, existing, null);
    }

    /**
     * @param autoEmployeeNo used only when adding a new employee (existing == null).
     */
    public EmployeeFormDialog(Window owner, String title, EmployeeRecord existing, String autoEmployeeNo) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.editMode = (existing != null);

        buildUI();

        if (existing != null) {
            fill(existing);
            txtEmpNo.setEditable(false); // primary key
        } else {
            // Add mode: employee number is system-generated
            txtEmpNo.setEditable(false);
            if (autoEmployeeNo != null && !autoEmployeeNo.isBlank()) {
                txtEmpNo.setText(autoEmployeeNo.trim());
            }
        }

        pack();
        setLocationRelativeTo(owner);

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> {
            result = null;
            dispose();
        });
    }

    public EmployeeRecord getResult() {
        return result;
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 6, 4, 6);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;

        // Add mode: hide Employee # input (system-generated). Edit mode: show read-only.
        if (editMode) {
            addRow(form, gc, r++, "Employee #", txtEmpNo);
        }
        addRow(form, gc, r++, "Last Name", txtLast);
        addRow(form, gc, r++, "First Name", txtFirst);
        addRow(form, gc, r++, "Birthday (M/d/yyyy)", txtBday);
        addRow(form, gc, r++, "Address", txtAddress);
        addRow(form, gc, r++, "Phone Number", txtPhone);

        // Ensure masks show placeholders and behave nicely
        initMaskedField(txtSss);
        initMaskedField(txtPagibig);

        addRow(form, gc, r++, "SSS #", txtSss);
        addRow(form, gc, r++, "Philhealth #", txtPhil);
        addRow(form, gc, r++, "TIN #", txtTin);
        addRow(form, gc, r++, "Pag-ibig #", txtPagibig);

        addRow(form, gc, r++, "Status", cmbStatus);
        addRow(form, gc, r++, "Position", txtPosition);
        addRow(form, gc, r++, "Immediate Supervisor", txtSupervisor);

        // HR includes these
        addRow(form, gc, r++, "Basic Salary", txtBasicSalary);
        addRow(form, gc, r++, "Gross Semi-monthly Rate", txtGross);
        addRow(form, gc, r++, "Hourly Rate", txtHourly);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(btnSave);
        buttons.add(btnCancel);

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints gc, int row, String label, Component field) {
        gc.gridx = 0;
        gc.gridy = row;
        gc.weightx = 0;
        panel.add(new JLabel(label), gc);

        gc.gridx = 1;
        gc.weightx = 1;
        panel.add(field, gc);
    }

    private void fill(EmployeeRecord e) {
        txtEmpNo.setText(nz(e.getEmployeeNo()));
        txtLast.setText(nz(e.getLastName()));
        txtFirst.setText(nz(e.getFirstName()));
        txtBday.setText(e.getBirthday() == null ? "" : dateFmt.format(e.getBirthday()));
        txtAddress.setText(nz(e.getAddress()));
        txtPhone.setText(nz(e.getPhoneNumber()));

        txtSss.setText(nz(e.getSssNo()));
        txtPhil.setText(nz(e.getPhilhealthNo()));
        txtTin.setText(nz(e.getTinNo()));
        txtPagibig.setText(nz(e.getPagibigNo()));

        if (e.getStatus() != null && e.getStatus().toLowerCase().contains("prob")) {
            cmbStatus.setSelectedItem("Probationary");
        } else {
            cmbStatus.setSelectedItem("Regular");
        }

        txtPosition.setText(nz(e.getPosition()));
        txtSupervisor.setText(nz(e.getImmediateSupervisor()));

        txtBasicSalary.setText(String.valueOf(e.getBasicSalary()));
        txtGross.setText(String.valueOf(e.getGrossSemiMonthlyRate()));
        txtHourly.setText(String.valueOf(e.getHourlyRate()));
    }

    private void onSave() {
        try {
            EmployeeRecord e = new EmployeeRecord();

            // Employee # is always required (auto-set in Add mode; read-only in Edit mode)
            e.setEmployeeNo(reqPattern(txtEmpNo.getText(), "Employee #", "\\d+", "Use numbers only."));

            e.setLastName(reqNoDigits(txtLast.getText(), "Last Name"));
            e.setFirstName(reqNoDigits(txtFirst.getText(), "First Name"));

            String bday = reqPattern(txtBday.getText(), "Birthday", "\\d{1,2}/\\d{1,2}/\\d{4}", "Use M/d/yyyy (e.g., 3/4/2006).");
            e.setBirthday(parseDate(bday));
            e.setAddress(txtAddress.getText().trim());
            e.setPhoneNumber(reqPattern(txtPhone.getText(), "Phone Number", "\\d{3}-\\d{3}-\\d{3}", "Must follow ###-###-### (e.g., 786-868-477)."));

            e.setSssNo(optPattern(maskedOrEmpty(txtSss), "SSS No.", "\\d{2}-\\d{7}-\\d{1}", "Use ##-#######-# (e.g., 34-1234567-8)."));
            e.setPhilhealthNo(reqPattern(txtPhil.getText(), "PhilHealth #", "\\d{2}-\\d{9}-\\d{1}", "Must follow ##-#########-# (e.g., 11-123456789-1)."));
            e.setTinNo(reqPattern(txtTin.getText(), "TIN #", "\\d{3}-\\d{3}-\\d{3}-\\d{3}", "Must follow ###-###-###-### (e.g., 273-970-941-000)."));
            e.setPagibigNo(optPattern(maskedOrEmpty(txtPagibig), "Pag-IBIG No.", "\\d{4}-\\d{4}-\\d{4}", "Use ####-####-#### (e.g., 1234-5678-9012)."));

            e.setStatus(String.valueOf(cmbStatus.getSelectedItem()));
            e.setPosition(reqNoDigits(txtPosition.getText(), "Position"));
            // Supervisor can be blank in some orgs; if provided, must not contain digits
            e.setImmediateSupervisor(optNoDigits(txtSupervisor.getText(), "Immediate Supervisor"));

            e.setBasicSalary(parseMoney(txtBasicSalary.getText()));
            e.setGrossSemiMonthlyRate(parseMoney(txtGross.getText()));
            e.setHourlyRate(parseMoney(txtHourly.getText()));

            // IMPORTANT: HR does NOT set rice/phone/clothing here.
            // Service will preserve them for update; and set to 0 on add.

            result = e;
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static MaskFormatter createMask(String mask) {
        try {
            MaskFormatter mf = new MaskFormatter(mask);
            mf.setPlaceholderCharacter('_');
            return mf;
        } catch (ParseException e) {
            throw new IllegalStateException("Invalid mask: " + mask, e);
        }
    }

    private static void initMaskedField(JFormattedTextField f) {
        f.setColumns(15);
        f.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
    }

    /**
     * If user didn't complete the mask, treat as empty (optional field).
     */
    private static String maskedOrEmpty(JFormattedTextField f) {
        if (f == null) return "";
        String t = String.valueOf(f.getText()).trim();
        return (t.contains("_")) ? "" : t;
    }

    private LocalDate parseDate(String s) {
        return LocalDate.parse(s.trim(), dateFmt);
    }

    private double parseMoney(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        String cleaned = s.trim().replace(",", "").replace("\"", "");
        return Double.parseDouble(cleaned);
    }

    private String req(String s, String field) {
        if (s == null || s.trim().isEmpty()) throw new IllegalArgumentException(field + " is required.");
        return s.trim();
    }

    private String reqNoDigits(String s, String field) {
        String v = req(s, field);
        // Letters, spaces, dot, hyphen, apostrophe only. No digits.
        if (!v.matches("[A-Za-z][A-Za-z .'-]*")) {
            throw new IllegalArgumentException(field + " must contain letters only (no numbers/special chars).");
        }
        return v;
    }

    private String optNoDigits(String s, String field) {
        String v = (s == null) ? "" : s.trim();
        if (v.isEmpty()) return "";
        if (!v.matches("[A-Za-z][A-Za-z .'-]*")) {
            throw new IllegalArgumentException(field + " must contain letters only (no numbers/special chars).");
        }
        return v;
    }


    private String reqPattern(String s, String field, String regex, String hint) {
        String v = req(s, field);
        if (!v.matches(regex)) {
            throw new IllegalArgumentException(field + " format is invalid. " + hint);
        }
        return v;
    }

    private String optPattern(String s, String field, String regex, String hint) {
        String v = (s == null) ? "" : s.trim();
        if (v.isEmpty()) return "";
        if (!v.matches(regex)) {
            throw new IllegalArgumentException(field + " format is invalid. " + hint);
        }
        return v;
    }

    private String nz(String s) {
        return (s == null) ? "" : s;
    }
}