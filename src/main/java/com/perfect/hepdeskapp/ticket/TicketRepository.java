package com.perfect.hepdeskapp.ticket;

import com.perfect.hepdeskapp.department.Department;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket,Long> {
    @Query("SELECT t FROM Ticket t WHERE t.id = :id")
    public Ticket findTicketById(@Param("id") Long id);
    @Query("SELECT t FROM Ticket t WHERE t.id = :id AND t.access_token = :token")
    public Ticket findTicketByIdAndAccess_token(@Param("id") Long id, @Param("token") String token);
    @Query("SELECT t FROM Ticket t ORDER BY t.ticket_time DESC")
    public List<Ticket> findAllByTicket_time();
    @Query("SELECT t FROM Ticket t")
    public List<Ticket> findAllByTicket_time(Sort sort);
    @Query("SELECT t FROM Ticket t WHERE t.department = :department")
    public List<Ticket> findTicketsByDepartment(@Param("department")Department department,Sort sort);
    @Query("SELECT t FROM Ticket t WHERE t.status.status = :status")
    public List<Ticket> findAllByStatus(@Param("status") String status);
    @Query("SELECT t FROM Ticket t WHERE t.status.id = :status")
    public List<Ticket> findAllByStatus(@Param("status") Long status);
    @Query("SELECT t FROM Ticket t WHERE t.status.status = :status")
    public List<Ticket> findAllByStatus(@Param("status") String status, Sort sort);
}
