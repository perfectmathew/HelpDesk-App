package com.perfect.hepdeskapp.documentation;

import com.perfect.hepdeskapp.attachment.Attachment;
import com.perfect.hepdeskapp.ticket.Ticket;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "documentation", schema = "public")
public class Documentation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "ticketid")
    private Ticket ticket;
    @ManyToMany
    @JoinTable(
            name = "documentation_attachments",
            joinColumns = @JoinColumn(name = "documentation_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private Set<Attachment> documentationAttachmentsSet = new HashSet<>();
    private String description;
    public  Documentation(Ticket ticket, String description){
        this.ticket = ticket;
        this.description = description;
    }
    public Documentation(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Attachment> getDocumentationAttachmentsSet() {
        return documentationAttachmentsSet;
    }
    public void deleteAttachmentFromDocumentation(Attachment attachment)  { this.documentationAttachmentsSet.remove(attachment); }

    public void addAttachmentToDocumentation(Attachment attachment){
        this.documentationAttachmentsSet.add(attachment);
    }

    public void setDocumentationAttachmentsSet(Set<Attachment> documentationAttachmentsSet) {
        this.documentationAttachmentsSet = documentationAttachmentsSet;
    }
}
