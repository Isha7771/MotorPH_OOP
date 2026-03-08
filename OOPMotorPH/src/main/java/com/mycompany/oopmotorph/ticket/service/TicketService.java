package com.mycompany.oopmotorph.ticket.service;

import com.mycompany.oopmotorph.ticket.model.Ticket;
import com.mycompany.oopmotorph.ticket.repository.TicketRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TicketService {

    private final TicketRepository repo;

    public TicketService(TicketRepository repo) {
        this.repo = repo;
    }

    // Employee submit
    public Ticket submitTicket(String employeeNo,
                               String lastName,
                               String firstName,
                               String title,
                               String description) {

        String id = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        Ticket t = new Ticket(
                id,
                employeeNo,
                lastName,
                firstName,
                title,
                description,
                "OPEN",
                now,
                now,
                "",
                ""
        );
        repo.save(t);
        return t;
    }

    // List tickets for one employee
    public List<Ticket> findByEmployee(String employeeNo) {
        return repo.findAll().stream()
                .filter(t -> t.getEmployeeNo().equals(employeeNo))
                .collect(Collectors.toList());
    }

    // IT: list all tickets
    public List<Ticket> findAll() {
        return repo.findAll();
    }

    // IT: update status
    public void updateStatus(String ticketId,
                             String newStatus,
                             String itEmployeeNo,
                             String itFullName) {

        Ticket t = repo.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticketId));

        t.setStatus(newStatus);
        t.setUpdatedAt(LocalDateTime.now());
        t.setHandledByEmployeeNo(itEmployeeNo);
        t.setHandledByName(itFullName);

        repo.save(t);
    }
}
