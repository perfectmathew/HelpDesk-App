package com.perfect.hepdeskapp.ticket;

import com.perfect.hepdeskapp.department.Department;
import com.perfect.hepdeskapp.status.Status;
import com.perfect.hepdeskapp.user.User;
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
    @Query("SELECT t FROM Ticket t WHERE t.status.status <> 'ARCHIVED' ORDER BY t.priority.id DESC, t.ticket_time")
    public List<Ticket> findAllByTicket_time();
    @Query("SELECT t FROM Ticket t WHERE t.status.status = 'ARCHIVED' ORDER BY t.ticket_time DESC ")
    public List<Ticket> findAllByStatusContainsArchivedOrderByTicket_time();
    @Query("SELECT t FROM Ticket t WHERE t.department = :department AND t.status.status = 'ARCHIVED' ORDER BY t.ticket_time DESC ")
    public List<Ticket> findTicketsByDepartmentAndStatusContainingArchivedOrderByTicket_time(@Param("department") Department department);
    @Query("SELECT t FROM Ticket t")
    public List<Ticket> findAllByTicket_time(Sort sort);
    @Query("SELECT t FROM Ticket t WHERE t.department = :department and t.status = :status ORDER BY t.priority.id DESC, t.ticket_time")
    public List<Ticket> findTicketsByDepartmentAndStatus(@Param("department") Department department, @Param("status") Status status);
    @Query("SELECT t FROM Ticket t WHERE t.department = :department AND t.status.status <> 'ARCHIVED' ORDER BY t.priority.id DESC, t.ticket_time")
    public List<Ticket> findTicketsByDepartmentAnAndStatusNotContainingArchive(@Param("department") Department department);
    @Query("SELECT t FROM Ticket t WHERE t.department.id = :department")
    public List<Ticket> findTicketsByDepartment(@Param("department") Long department);
    @Query("SELECT DISTINCT t FROM Ticket t JOIN t.userList ul JOIN ul.userTickets ut where ul.email = :email AND (t.status.status <> 'DONE' AND t.status.status <> 'ARCHIVED') ORDER BY t.priority.id DESC, t.ticket_time")
    public List<Ticket> findTicketsByUserListContaining(@Param("email")  String email);
    @Query("SELECT t FROM Ticket t WHERE t.status.status = :status")
    public List<Ticket> findAllByStatus(@Param("status") String status);
    @Query("SELECT t FROM Ticket t WHERE t.department = :department and t.status = :status")
    public List<Ticket> findTicketsByDepartmentAndStatusNotOrder(@Param("department") Department department, @Param("status") Status status);
    @Query("SELECT t FROM Ticket t WHERE t.status.status <> 'DONE' AND t.status.status <> 'ARCHIVED'")
    public List<Ticket> findAllByStatusNotContainingArchivedAndDone();
    @Query("SELECT t FROM Ticket t WHERE t.department = :department and t.status.status <> 'DONE' AND t.status.status <> 'ARCHIVED'")
    public List<Ticket> findAllByDepartmentAndStatusNotContainingArchivedAndDone(@Param("department") Department department);
    @Query("SELECT t FROM Ticket t WHERE t.status.id = :status")
    public List<Ticket> findAllByStatus(@Param("status") Long status);
    @Query("SELECT t FROM Ticket t WHERE t.status.status = :status")
    public List<Ticket> findAllByStatus(@Param("status") String status, Sort sort);
    @Query("SELECT DISTINCT t FROM Ticket t JOIN t.userList ul JOIN ul.userTickets ut where ul.email = :email AND t.status.status = :status")
    public List<Ticket> findTicketsByStatusAndUserListContaining(@Param("email")  String email, @Param("status") String status, Sort sort);
    @Query("SELECT DISTINCT t FROM Ticket t JOIN t.userList ul JOIN ul.userTickets ut where ul.email = :email ORDER BY t.priority.id DESC, t.ticket_time")
    public List<Ticket> findTicketsByStatusAndUserListContaining(@Param("email")  String email);
    @Query("SELECT t FROM Ticket t WHERE t.department = :department AND (t.status.status = 'DONE' and t.status.status =  'ARCHIVED')")
    public List<Ticket> ListOfDoneTickets(@Param("department") Department department);

}
