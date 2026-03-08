package com.mycompany.oopmotorph.ticket.repository;

import com.mycompany.oopmotorph.ticket.model.Ticket;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TicketCsvRepository implements TicketRepository {

    private static final DateTimeFormatter DATE_TIME_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path csvPath;

    public TicketCsvRepository(Path csvPath) {
        this.csvPath = Objects.requireNonNull(csvPath);
    }

    @Override
    public List<Ticket> findAll() {
        ensureFileWithHeader();

        try (BufferedReader br = Files.newBufferedReader(csvPath)) {
            String header = br.readLine();
            if (header == null) {
                return List.of();
            }

            List<Ticket> result = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] c = line.split(",", -1);
                String id = get(c, 0);
                String empNo = get(c, 1);
                String last = get(c, 2);
                String first = get(c, 3);
                String title = get(c, 4);
                String desc = get(c, 5);
                String status = get(c, 6);
                LocalDateTime createdAt = parseDateTime(get(c, 7));
                LocalDateTime updatedAt = parseDateTime(get(c, 8));
                String handledNo = get(c, 9);
                String handledName = get(c, 10);

                Ticket t = new Ticket(
                        id, empNo, last, first, title, desc,
                        status, createdAt, updatedAt, handledNo, handledName
                );
                result.add(t);
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read " + csvPath, e);
        }
    }

    @Override
    public Optional<Ticket> findById(String id) {
        return findAll().stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    @Override
    public void save(Ticket ticket) {
        ensureFileWithHeader();
        List<String> lines = readAllLinesSafe();

        if (lines.isEmpty()) {
            lines.add(headerLine());
        }

        String header = lines.get(0);
        List<String> body = new ArrayList<>(lines.subList(1, lines.size()));
        boolean updated = false;

        for (int i = 0; i < body.size(); i++) {
            String[] c = body.get(i).split(",", -1);
            String existingId = get(c, 0);
            if (existingId.equals(ticket.getId())) {
                body.set(i, toCsv(ticket));
                updated = true;
                break;
            }
        }

        if (!updated) {
            body.add(toCsv(ticket));
        }

        writeAll(header, body);
    }

    // ---------------- helpers ----------------

    private void ensureFileWithHeader() {
        try {
            if (!Files.exists(csvPath)) {
                Files.createDirectories(csvPath.getParent());
                Files.writeString(csvPath, headerLine());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to init file " + csvPath, e);
        }
    }

    private List<String> readAllLinesSafe() {
        try {
            if (!Files.exists(csvPath)) {
                return new ArrayList<>();
            }
            return Files.readAllLines(csvPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read " + csvPath, e);
        }
    }

    private void writeAll(String header, List<String> body) {
        try {
            List<String> out = new ArrayList<>();
            out.add(header);
            out.addAll(body);
            Files.write(csvPath, out);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write " + csvPath, e);
        }
    }

    private String headerLine() {
        return String.join(",",
                "TicketId",
                "EmployeeNo",
                "EmployeeLastName",
                "EmployeeFirstName",
                "Title",
                "Description",
                "Status",
                "CreatedAt",
                "UpdatedAt",
                "HandledByEmployeeNo",
                "HandledByName"
        );
    }

    private String toCsv(Ticket t) {
        return String.join(",",
                safe(t.getId()),
                safe(t.getEmployeeNo()),
                safe(t.getEmployeeLastName()),
                safe(t.getEmployeeFirstName()),
                safe(t.getTitle()),
                safe(t.getDescription()),
                safe(t.getStatus()),
                formatDateTime(t.getCreatedAt()),
                formatDateTime(t.getUpdatedAt()),
                safe(t.getHandledByEmployeeNo()),
                safe(t.getHandledByName())
        );
    }

    private String get(String[] c, int idx) {
        if (c == null || idx < 0 || idx >= c.length) return "";
        String s = c[idx];
        return s == null ? "" : s.trim();
    }

    private String safe(String s) {
        if (s == null) return "";
        return s.replace(",", " ").trim();
    }

    private LocalDateTime parseDateTime(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDateTime.parse(s.trim(), DATE_TIME_FMT);
        } catch (Exception e) {
            return null;
        }
    }

    private String formatDateTime(LocalDateTime dt) {
        return dt == null ? "" : dt.format(DATE_TIME_FMT);
    }
}
