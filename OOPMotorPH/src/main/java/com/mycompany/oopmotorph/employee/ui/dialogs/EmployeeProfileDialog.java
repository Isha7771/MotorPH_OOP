package com.mycompany.oopmotorph.employee.ui.dialogs;

import com.mycompany.oopmotorph.attendance.model.AttendanceRecord;
import com.mycompany.oopmotorph.attendance.repository.AttendanceCsvRepository;
import com.mycompany.oopmotorph.common.CsvPaths;
import com.mycompany.oopmotorph.employee.model.EmployeeRecord;
import com.mycompany.oopmotorph.employee.repository.EmployeeCsvRepository;
import com.mycompany.oopmotorph.leave.model.LeaveRequest;
import com.mycompany.oopmotorph.leave.repository.LeaveCsvRepository;
import com.mycompany.oopmotorph.leave.service.LeaveService;
import com.mycompany.oopmotorph.overtime.model.OvertimeRequest;
import com.mycompany.oopmotorph.overtime.repository.OvertimeCsvRepository;
import com.mycompany.oopmotorph.overtime.service.OvertimeService;
import com.mycompany.oopmotorph.payroll.model.PayslipRow;
import com.mycompany.oopmotorph.payroll.repository.PayslipCsvRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Read-only view of an employee's data + history.
 * Used when clicking the header name on any dashboard.
 */
public class EmployeeProfileDialog extends JDialog {

    private final String employeeNo;

    private final EmployeeCsvRepository employeeRepo = new EmployeeCsvRepository(CsvPaths.employeeDataCsv());
    private final AttendanceCsvRepository attendanceRepo = new AttendanceCsvRepository(CsvPaths.attendanceCsv());
    private final OvertimeService overtimeService = new OvertimeService(new OvertimeCsvRepository(CsvPaths.overtimeCsv()));
    private final LeaveService leaveService = new LeaveService(new LeaveCsvRepository(CsvPaths.leaveCsv()));
    private final PayslipCsvRepository payslipRepo = new PayslipCsvRepository(CsvPaths.payslipsCsv());

    public EmployeeProfileDialog(Window owner, String employeeNo) {
        super(owner, "Employee Profile", ModalityType.APPLICATION_MODAL);
        this.employeeNo = employeeNo;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setContentPane(buildRoot());
        setSize(900, 560);
        setLocationRelativeTo(owner);
    }

    private JComponent buildRoot() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Info", buildInfoTab());
        tabs.addTab("Attendance", buildAttendanceTab());
        tabs.addTab("Overtime", buildOvertimeTab());
        tabs.addTab("Leave", buildLeaveTab());
        tabs.addTab("Payslips", buildPayslipTab());

        root.add(tabs, BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnClose);
        root.add(bottom, BorderLayout.SOUTH);

        return root;
    }

    private JComponent buildInfoTab() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 6, 4, 6);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        EmployeeRecord e = loadEmployee();

        int r = 0;
        addInfoRow(p, gc, r++, "Employee #", nz(e.getEmployeeNo()));
        addInfoRow(p, gc, r++, "Name", nz(e.getFullName()));
        addInfoRow(p, gc, r++, "Birthday", e.getBirthday() == null ? "" : e.getBirthday().toString());
        addInfoRow(p, gc, r++, "Status", nz(e.getStatus()));
        addInfoRow(p, gc, r++, "Position", nz(e.getPosition()));
        addInfoRow(p, gc, r++, "Immediate Supervisor", nz(e.getImmediateSupervisor()));
        addInfoRow(p, gc, r++, "Phone", nz(e.getPhoneNumber()));
        addInfoRow(p, gc, r++, "Address", nz(e.getAddress()));
        addInfoRow(p, gc, r++, "SSS", nz(e.getSssNo()));
        addInfoRow(p, gc, r++, "PhilHealth", nz(e.getPhilhealthNo()));
        addInfoRow(p, gc, r++, "Pag-IBIG", nz(e.getPagibigNo()));
        addInfoRow(p, gc, r++, "TIN", nz(e.getTinNo()));
        addInfoRow(p, gc, r++, "Basic Salary", String.valueOf(e.getBasicSalary()));
        addInfoRow(p, gc, r++, "Gross Semi-monthly", String.valueOf(e.getGrossSemiMonthlyRate()));
        addInfoRow(p, gc, r++, "Hourly Rate", String.valueOf(e.getHourlyRate()));

        return new JScrollPane(p);
    }

    private void addInfoRow(JPanel p, GridBagConstraints gc, int row, String label, String value) {
        gc.gridx = 0;
        gc.gridy = row;
        gc.weightx = 0;
        p.add(new JLabel(label + ":"), gc);

        gc.gridx = 1;
        gc.weightx = 1;
        JTextField tf = new JTextField(value);
        tf.setEditable(false);
        p.add(tf, gc);
    }

    private JComponent buildAttendanceTab() {
        DefaultTableModel m = new DefaultTableModel(new Object[]{
                "Employee #", "Name", "Date", "Position", "Time In", "Time Out", "Total Hours", "Status", "Remarks"
        }, 0);
        JTable t = new JTable(m);
        t.setRowHeight(22);

        try {
            List<AttendanceRecord> rows = attendanceRepo.findAll().stream()
                    .filter(r -> nz(r.getEmployeeId()).equalsIgnoreCase(employeeNo))
                    .collect(Collectors.toList());
            for (AttendanceRecord r : rows) {
                m.addRow(new Object[]{
                        nz(r.getEmployeeId()),
                        nz(r.getEmployeeName()),
                        s(r.getDate()),
                        nz(r.getPosition()),
                        s(r.getTimeIn()),
                        s(r.getTimeOut()),
                        String.valueOf(r.getTotalHours()),
                        r.getStatus() == null ? "" : r.getStatus().name(),
                        nz(r.getRemarks())
                });
            }
        } catch (Exception ex) {
            m.addRow(new Object[]{"(error)", ex.getMessage(), "", "", "", "", "", "", ""});
        }

        return new JScrollPane(t);
    }

    private JComponent buildOvertimeTab() {
        DefaultTableModel m = new DefaultTableModel(new Object[]{
                "Request ID", "Employee", "Date", "Hours", "Reason", "Status"
        }, 0);
        JTable t = new JTable(m);
        t.setRowHeight(22);

        try {
            List<OvertimeRequest> rows = overtimeService.search("", Optional.empty()).stream()
                    .filter(r -> nz(r.getEmployeeName()).toLowerCase().contains(getEmployeeNameKey().toLowerCase()))
                    .collect(Collectors.toList());
            for (OvertimeRequest r : rows) {
                m.addRow(new Object[]{
                        nz(r.getRequestId()),
                        nz(r.getEmployeeName()),
                        s(r.getDate()),
                        nz(r.getHours()),
                        nz(r.getReason()),
                        r.getStatus() == null ? "" : r.getStatus().name()
                });
            }
        } catch (Exception ex) {
            m.addRow(new Object[]{"(error)", ex.getMessage(), "", "", "", ""});
        }

        return new JScrollPane(t);
    }

    private JComponent buildLeaveTab() {
        DefaultTableModel m = new DefaultTableModel(new Object[]{
                "Request ID", "Employee", "Type", "Date Filed", "Start", "End", "Status"
        }, 0);
        JTable t = new JTable(m);
        t.setRowHeight(22);

        try {
            List<LeaveRequest> rows = leaveService.search("", Optional.empty()).stream()
                    .filter(r -> nz(r.getEmployeeName()).toLowerCase().contains(getEmployeeNameKey().toLowerCase()))
                    .collect(Collectors.toList());
            for (LeaveRequest r : rows) {
                m.addRow(new Object[]{
                        nz(r.getRequestId()),
                        nz(r.getEmployeeName()),
                        r.getLeaveType() == null ? "" : r.getLeaveType().name(),
                        nz(r.getDateFiled()),
                        nz(r.getStartDate()),
                        nz(r.getEndDate()),
                        r.getStatus() == null ? "" : r.getStatus().name()
                });
            }
        } catch (Exception ex) {
            m.addRow(new Object[]{"(error)", ex.getMessage(), "", "", "", "", ""});
        }

        return new JScrollPane(t);
    }

    private JComponent buildPayslipTab() {
        DefaultTableModel m = new DefaultTableModel(new Object[]{
                "Payslip ID", "Employee #", "Pay Date", "Status", "Action"
        }, 0);
        JTable t = new JTable(m);
        t.setRowHeight(22);

        try {
            List<PayslipRow> rows = payslipRepo.findAll().stream()
                    .filter(r -> nz(r.getEmployeeNo()).equalsIgnoreCase(employeeNo))
                    .collect(Collectors.toList());
            for (PayslipRow r : rows) {
                m.addRow(new Object[]{
                        nz(r.getPayslipId()),
                        nz(r.getEmployeeNo()),
                        nz(r.getPayDate()),
                        nz(r.getStatus()),
                        nz(r.getAction())
                });
            }
        } catch (Exception ex) {
            m.addRow(new Object[]{"(error)", ex.getMessage(), "", "", ""});
        }

        return new JScrollPane(t);
    }

    private EmployeeRecord loadEmployee() {
        try {
            Optional<EmployeeRecord> found = employeeRepo.findByEmployeeNo(employeeNo);
            return found.orElseGet(EmployeeRecord::new);
        } catch (IOException e) {
            return new EmployeeRecord();
        }
    }

    private String getEmployeeNameKey() {
        EmployeeRecord e = loadEmployee();
        String name = nz(e.getFullName());
        return name.isBlank() ? employeeNo : name;
    }

    private static String s(Object o) {
        return o == null ? "" : o.toString().trim();
    }

    private static String nz(String s) {
        return s == null ? "" : s.trim();
    }
}