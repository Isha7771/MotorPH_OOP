package com.mycompany.oopmotorph.ticket.repository;

import com.mycompany.oopmotorph.ticket.model.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepository {

    List<Ticket> findAll();

    Optional<Ticket> findById(String id);

    void save(Ticket ticket);
}

