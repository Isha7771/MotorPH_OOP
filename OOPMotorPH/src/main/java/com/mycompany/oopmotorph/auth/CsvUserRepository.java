/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.auth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CsvUserRepository implements UserRepository {

    private static final String HEADER =
            "username,password,role,employeeNo,lastName,firstName,position,category";

    private final Path csvPath;

    public CsvUserRepository(Path csvPath) {
        this.csvPath = csvPath;
    }

    @Override
    public List<User> findAll() throws IOException {
        if (Files.notExists(csvPath)) return new ArrayList<>();

        List<User> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",", -1);
                String username = get(p, 0);
                String password = get(p, 1);
                Role role = Role.fromString(get(p, 2));
                String empNo = get(p, 3);
                String last = get(p, 4);
                String firstName = get(p, 5);
                String position = get(p, 6);
                String category = get(p, 7);

                if (!username.isEmpty()) {
                    out.add(new User(username, password, role, empNo, last, firstName, position, category));
                }
            }
        }
        return out;
    }

    @Override
    public Optional<User> findByUsername(String username) throws IOException {
        if (username == null) return Optional.empty();
        String q = username.trim();
        return findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(q))
                .findFirst();
    }

    /**
     * Appends a single user record to the CSV.
     * - Creates the file (and header) if missing.
     * - Optional safety: does not append if username already exists.
     */
    @Override
    public void append(User user) throws IOException {
        if (user == null) return;

        ensureFileWithHeader();

        // Optional: prevent duplicates (safe for provisioning)
        if (user.getUsername() != null && findByUsername(user.getUsername()).isPresent()) {
            return;
        }

        String line = toCsvLine(user);

        try (BufferedWriter bw = Files.newBufferedWriter(
                csvPath,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND
        )) {
            bw.write(line);
            bw.newLine();
        }
    }

    /**
     * Rewrites the entire CSV (header + rows).
     */
    @Override
    public void saveAll(List<User> users) throws IOException {
        ensureParentDir();

        try (BufferedWriter bw = Files.newBufferedWriter(
                csvPath,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        )) {
            bw.write(HEADER);
            bw.newLine();

            if (users != null) {
                for (User u : users) {
                    if (u == null) continue;
                    if (safe(u.getUsername()).isEmpty()) continue;
                    bw.write(toCsvLine(u));
                    bw.newLine();
                }
            }
        }
    }

    private void ensureFileWithHeader() throws IOException {
        ensureParentDir();

        if (Files.notExists(csvPath)) {
            Files.createFile(csvPath);
        }

        // If empty file, write header
        if (Files.size(csvPath) == 0) {
            try (BufferedWriter bw = Files.newBufferedWriter(
                    csvPath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING
            )) {
                bw.write(HEADER);
                bw.newLine();
            }
        }
    }

    private void ensureParentDir() throws IOException {
        Path parent = csvPath.getParent();
        if (parent != null && Files.notExists(parent)) {
            Files.createDirectories(parent);
        }
    }

    private static String toCsvLine(User u) {
        // Column order MUST match HEADER and findAll() parsing
        String username = escape(safe(u.getUsername()));
        String password = escape(safe(u.getPassword()));
        String role = escape(u.getRole() == null ? "" : u.getRole().toString());
        String empNo = escape(safe(u.getEmployeeNo()));
        String last = escape(safe(u.getLastName()));
        String first = escape(safe(u.getFirstName()));
        String position = escape(safe(u.getPosition()));
        String category = escape(safe(u.getCategory()));

        return String.join(",",
                username, password, role, empNo, last, first, position, category
        );
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    // Minimal escape: if you ever had commas in fields, you’d need full CSV quoting.
    // Your data looks comma-safe, but this prevents accidental newlines.
    private static String escape(String s) {
        return s.replace("\n", " ").replace("\r", " ");
    }

    private static String get(String[] p, int idx) {
        if (p == null || idx < 0 || idx >= p.length) return "";
        return p[idx] == null ? "" : p[idx].trim();
    }
}