/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.auth;

import com.mycompany.oopmotorph.employee.model.EmployeeRecord;
import com.mycompany.oopmotorph.employee.repository.EmployeeCsvRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class UserProvisioningService {

    private final EmployeeCsvRepository employeeRepo;
    private final UserRepository userRepo;
    private final RoleMapper roleMapper;

    public UserProvisioningService(EmployeeCsvRepository employeeRepo,
                                   UserRepository userRepo,
                                   RoleMapper roleMapper) {
        this.employeeRepo = employeeRepo;
        this.userRepo = userRepo;
        this.roleMapper = roleMapper;
    }

    public void ensureUsersExist() throws IOException {
        List<EmployeeRecord> employees = employeeRepo.findAll();

        Set<String> existing = new HashSet<>();
        for (User u : userRepo.findAll()) {
            existing.add(u.getUsername().toLowerCase(Locale.ROOT));
        }

        for (EmployeeRecord e : employees) {
            String empNo = safe(e.getEmployeeNo());
            if (empNo.isEmpty()) continue;

            String username = empNo; // rule: username = employee #
            if (existing.contains(username.toLowerCase(Locale.ROOT))) continue;

            String defaultPass = defaultPasswordFor(empNo);
            Role role = roleMapper.fromPosition(e.getPosition());

            User newUser = new User(
                    username,
                    defaultPass,
                    role,
                    empNo,
                    safe(e.getLastName()),
                    safe(e.getFirstName()),
                    safe(e.getPosition()),
                    normalizeCategory(e.getStatus())
            );

            userRepo.append(newUser);
            existing.add(username.toLowerCase(Locale.ROOT));
        }
    }

    public String defaultPasswordFor(String employeeNo) {
        return "MotorPH@" + employeeNo;
    }

    private static String normalizeCategory(String statusRaw) {
        String s = safe(statusRaw).toLowerCase(Locale.ROOT);
        if (s.contains("regular")) return "Regular";
        return "Probationary"; // your CSV uses Probational/Probationary variants
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}