package com.perfect.hepdeskapp.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket,Long> {
    @Query("SELECT t FROM Ticket t WHERE t.id = :id")
    public Ticket findTicketById(@Param("id") Long id);
}
