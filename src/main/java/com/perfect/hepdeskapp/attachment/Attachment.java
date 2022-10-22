package com.perfect.hepdeskapp.attachment;

import com.perfect.hepdeskapp.documentation.Documentation;
import com.perfect.hepdeskapp.ticket.Ticket;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "attachment",schema = "public")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String url;
    @ManyToMany(mappedBy = "attachmentSet")
    private List<Ticket> ticketList = new ArrayList<>();
    @ManyToMany(mappedBy = "documentationAttachmentsSet")
    private  List<Documentation> documentationList = new ArrayList<>();
    public Attachment(){


    }

    public Attachment(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
