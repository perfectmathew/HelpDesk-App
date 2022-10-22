package com.perfect.hepdeskapp.documentation;

import com.perfect.hepdeskapp.ticket.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface DocumentationRepository extends JpaRepository<Documentation, Long> {
    @Query("SELECT d FROM Documentation d WHERE d.ticket = :ticket")
    public Documentation findDocumentationByTicket(@Param("ticket") Ticket ticket);
    @Query("SELECT d FROM Documentation d WHERE d.id = :id")
    public Documentation findDocumentationById(@Param("id") Long id);
}
