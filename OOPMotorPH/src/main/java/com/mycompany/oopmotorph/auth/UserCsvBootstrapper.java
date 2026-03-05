package com.mycompany.oopmotorph.auth;

import com.mycompany.oopmotorph.common.CsvUtils;
import com.mycompany.oopmotorph.employee.model.EmployeeRecord;
import com.mycompany.oopmotorph.employee.repository.EmployeeCsvRepository;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Generates/updates Data/users.csv from Data/EmployeeData.csv.
 *
 * Login rule:
 *  - username = Employee #
 *  - default password = MotorPH@<Employee#>
 *
 * Role rule (Option B):
 *  - Chief Officers => ADMIN (but app opens SupervisorFrame for ADMIN)
 *  - HR positions  => HR_STAFF
 *  - Payroll positions => PAYROLL_STAFF
 *  - everyone else => EMPLOYEE
 *
 * Category rule:
 *  - from EmployeeData.csv Status (Regular / Probational)
 */
public class UserCsvBootstrapper {

    // Username,Password,Role,EmployeeNo,LastName,FirstName,Position,Category
    private static final String HEADER =
            "Username,Password,Role,EmployeeNo,LastName,FirstName,Position,Category";

    /**
     * Ensures users.csv exists and contains an account for every employee in employeeDataCsv.
     * Existing rows are preserved; missing employees are appended.
     */
    public static void ensureUsersCsv(Path usersCsvPath, Path employeeDataCsv) throws IOException {
        if (usersCsvPath.getParent() != null) Files.createDirectories(usersCsvPath.getParent());

        // Create file with header if missing/empty
        if (Files.notExists(usersCsvPath) || Files.size(usersCsvPath) == 0) {
            Files.writeString(usersCsvPath, HEADER + System.lineSeparator(), StandardCharsets.UTF_8);
        }

        // Load existing usernames
        Set<String> existing = new HashSet<>();
        CsvUserRepository repo = new CsvUserRepository(usersCsvPath);
        for (User u : repo.findAll()) {
            existing.add(u.getUsername().toLowerCase(Locale.ROOT));
        }

        // Load employees
        EmployeeCsvRepository empRepo = new EmployeeCsvRepository(employeeDataCsv);
        List<EmployeeRecord> employees = empRepo.findAll();

        // Append missing
        try (BufferedWriter bw = Files.newBufferedWriter(
                usersCsvPath,
                StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {

            for (EmployeeRecord e : employees) {
                String empNo = safe(e.getEmployeeNo());
                if (empNo.isEmpty()) continue;

                String username = empNo;
                if (existing.contains(username.toLowerCase(Locale.ROOT))) continue;

                String password = "MotorPH@" + empNo;
                Role role = roleFromPosition(e.getPosition());

                String last = safe(e.getLastName());
                String first = safe(e.getFirstName());
                String position = safe(e.getPosition());
                String category = normalizeCategory(e.getStatus());

                bw.write(String.join(",",
                        CsvUtils.escapeCsv(username),
                        CsvUtils.escapeCsv(password),
                        CsvUtils.escapeCsv(role.name()),
                        CsvUtils.escapeCsv(empNo),
                        CsvUtils.escapeCsv(last),
                        CsvUtils.escapeCsv(first),
                        CsvUtils.escapeCsv(position),
                        CsvUtils.escapeCsv(category)
                ));
                bw.newLine();

                existing.add(username.toLowerCase(Locale.ROOT));
            }
        }
    }

    private static Role roleFromPosition(String positionRaw) {
        String p = safe(positionRaw).toLowerCase(Locale.ROOT);

        // ADMIN (Chief Officers)
        if (p.equals("chief executive officer")
                || p.equals("chief operating officer")
                || p.equals("chief finance officer")
                || p.equals("chief marketing officer")) {
            return Role.ADMIN;
        }

        // HR
        if (p.equals("hr manager")
                || p.equals("hr team leader")
                || p.equals("hr rank and file")) {
            return Role.HR_STAFF;
        }

        // Payroll
        if (p.equals("payroll manager")
                || p.equals("payroll team leader")
                || p.equals("payroll rank and file")) {
            return Role.PAYROLL_STAFF;
        }

        // The rest
        return Role.EMPLOYEE;
    }

    private static String normalizeCategory(String statusRaw) {
        String s = safe(statusRaw).toLowerCase(Locale.ROOT);
        if (s.contains("regular")) return "Regular";
        // Accept probation/probational/probationary
        if (s.contains("prob")) return "Probational";
        return safe(statusRaw);
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
