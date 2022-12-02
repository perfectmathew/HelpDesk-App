package com.perfect.hepdeskapp.attachment;

import com.perfect.hepdeskapp.documentation.Documentation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment,Long> {
    @Query("SELECT DISTINCT a FROM Attachment a JOIN a.documentationList dl JOIN  dl.documentationAttachmentsSet WHERE  dl.id = :docid")
    public List<Attachment> findAttachmentByDocumentation(@Param("docid") Long id);
    @Query("SELECT a FROM Attachment a WHERE a.id = :id")
    public Attachment getAttachmentById(@Param("id")Long id);
}
