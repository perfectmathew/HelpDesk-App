package com.perfect.hepdeskapp.documentation;

import com.perfect.hepdeskapp.attachment.Attachment;
import com.perfect.hepdeskapp.attachment.AttachmentRepository;
import com.perfect.hepdeskapp.config.FileUploadService;
import com.perfect.hepdeskapp.ticket.Ticket;
import com.perfect.hepdeskapp.ticket.TicketRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class DocumentationController {
    final
    DocumentationRepository documentationRepository;
    final
    TicketRepository ticketRepository;
    final
    AttachmentRepository attachmentRepository;


    public DocumentationController(DocumentationRepository documentationRepository, TicketRepository ticketRepository, AttachmentRepository attachmentRepository) {
        this.documentationRepository = documentationRepository;
        this.ticketRepository = ticketRepository;
        this.attachmentRepository = attachmentRepository;
    }

    // ADMIN AND DEPARTMENT_BOSS API FOR DOCUMENTATION //

    @PostMapping("/manager/api/addDocumentation")
    public String addDocumentation(@RequestParam("ticket") Long ticket, @RequestParam("content") String docContent,
     @RequestParam(value = "attachments", required = false) MultipartFile[] files) throws IOException {
        Documentation documentation = new Documentation();
        Ticket ticketObject = ticketRepository.findTicketById(ticket);
        documentation.setTicket(ticketObject);
        documentation.setDescription(docContent);
        if(files != null){
            List<Attachment> attachments = new ArrayList<>();
            Path root = Paths.get("src","main","webapp","WEB-INF", "uploads");
            String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk"
                    +"lmnopqrstuvwxyz@";
            Random rnd = new Random();
            StringBuilder hash = new StringBuilder(10);
            for (int i = 0; i < 10; i++) hash.append(chars.charAt(rnd.nextInt(chars.length())));
            String uploadDir = root + "/" + hash;
            for(MultipartFile file : files){
                if(!file.isEmpty()){
                    FileUploadService.saveFile(uploadDir,file.getOriginalFilename(),file);
                    attachments.add(new Attachment(file.getOriginalFilename(),"/uploads/"+hash+"/"+file.getOriginalFilename()));
                }
            }
            attachmentRepository.saveAllAndFlush(attachments);
            documentation.getDocumentationAttachmentsSet().addAll(attachments);
        }
        documentationRepository.saveAndFlush(documentation);
        return "redirect:/t/"+ticket;
    }

    @PostMapping("/manager/api/editDocumentation")
    public String editDocumentation(@RequestParam("documentationid") Long documentationId, @RequestParam("ticketid")  Long ticket, @RequestParam("content") String docContent,
                                    @RequestParam(value = "attachments", required = false) MultipartFile[] files) throws IOException{
       Documentation documentation = documentationRepository.findDocumentationById(documentationId);
       documentation.setDescription(docContent);
       if(files!=null){
           List<Attachment> attachments = new ArrayList<>();
           Attachment attachment = attachmentRepository.findAttachmentByDocumentation(documentation.getId()).get(0);
           if(attachment!=null){
               String result = attachment.getUrl();
               String[] r = result.split("/");
               Path path = Paths.get("src","main","webapp","WEB-INF","uploads",r[2]);
               String uploadDir = path + "/";
               for(MultipartFile file : files){
                   if(!file.isEmpty()){
                       FileUploadService.saveFile(uploadDir,file.getOriginalFilename(),file);
                       attachments.add(new Attachment(file.getOriginalFilename(),"/uploads/"+r[2]+"/"+file.getOriginalFilename()));
                   }
               }
               attachmentRepository.saveAllAndFlush(attachments);
               documentation.getDocumentationAttachmentsSet().addAll(attachments);
           }
       }
       documentationRepository.saveAndFlush(documentation);
        return "redirect:/t/"+ticket;
    }

    @GetMapping("/manager/api/deleteDocumentation/{documentationId}")
    public String deleteDocumentation(@PathVariable("documentationId") Long documentation){
        Documentation documentationObject = documentationRepository.findDocumentationById(documentation);
        Long ticket = documentationObject.getTicket().getId();
        if(documentationObject.getDocumentationAttachmentsSet() != null && !documentationObject.getDocumentationAttachmentsSet().isEmpty())
        {
            Attachment attachment = attachmentRepository.findAttachmentByDocumentation(documentationObject.getId()).get(0);
            String result = attachment.getUrl();
            String[] r = result.split("/");
            Path path = Paths.get("src","main","webapp","WEB-INF","uploads",r[2]);
            FileUploadService.deleteDirectory(path.toFile());

        }
        documentationRepository.delete(documentationObject);
        return "redirect:/t/"+ticket;
    }
    @PostMapping("/manager/api/deleteAttachmentFromDocumentation")
    @ResponseBody
    public String deleteAttachmentFromDocumentation(@RequestParam("documentation_id") Long documentation_id, @RequestParam("attachment_id") Long attachment_id){
        Documentation documentation = documentationRepository.findDocumentationById(documentation_id);
        documentation.deleteAttachmentFromDocumentation(attachmentRepository.getReferenceById(attachment_id));
        Attachment attachment = attachmentRepository.findAttachmentByDocumentation(documentation.getId()).get(0);
        String result = attachment.getUrl();
        String[] r = result.split("/");
        Path path = Paths.get("src","main","webapp","WEB-INF","uploads",r[2]);
        String uploadDir = path + "/"+attachmentRepository.getAttachmentById(attachment_id).getName();
        File file = new File(uploadDir);
        FileUploadService.deleteFile(file);
        documentationRepository.saveAndFlush(documentation);
        attachmentRepository.delete(attachmentRepository.getAttachmentById(attachment_id));
        return "Successfully";
    }

    // WORKER API FOR DOCUMENTATION //

    @PostMapping("/worker/api/addDocumentation")
    public String workerAddDocumentation(@RequestParam("ticket") Long ticket, @RequestParam("content") String docContent,
                                         @RequestParam(value = "attachments", required = false) MultipartFile[] files) throws IOException {
        Documentation documentation = new Documentation();
        Ticket ticketObject = ticketRepository.findTicketById(ticket);
        documentation.setTicket(ticketObject);
        documentation.setDescription(docContent);
        if(files != null){
            List<Attachment> attachments = new ArrayList<>();
            Path root = Paths.get("src","main","webapp","WEB-INF", "uploads");
            String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk"
                    +"lmnopqrstuvwxyz@";
            Random rnd = new Random();
            StringBuilder hash = new StringBuilder(10);
            for (int i = 0; i < 10; i++) hash.append(chars.charAt(rnd.nextInt(chars.length())));
            String uploadDir = root + "/" + hash;
            for(MultipartFile file : files){
                if(!file.isEmpty()){
                    FileUploadService.saveFile(uploadDir,file.getOriginalFilename(),file);
                    attachments.add(new Attachment(file.getOriginalFilename(),"/uploads/"+hash+"/"+file.getOriginalFilename()));
                }
            }
            attachmentRepository.saveAllAndFlush(attachments);
            documentation.getDocumentationAttachmentsSet().addAll(attachments);
        }
        documentationRepository.saveAndFlush(documentation);
        return "redirect:/t/"+ticket;
    }
    @PostMapping("/worker/api/editDocumentation")
    public String workerEditDocumentation(@RequestParam("documentationid") Long documentationId, @RequestParam("ticketid")  Long ticket, @RequestParam("content") String docContent,
                                          @RequestParam(value = "attachments", required = false) MultipartFile[] files) throws IOException{
        Documentation documentation = documentationRepository.findDocumentationById(documentationId);
        documentation.setDescription(docContent);
        if(files!=null){
            List<Attachment> attachments = new ArrayList<>();
            Attachment attachment = attachmentRepository.findAttachmentByDocumentation(documentation.getId()).get(0);
            if(attachment!=null){
                String result = attachment.getUrl();
                String[] r = result.split("/");
                Path path = Paths.get("src","main","webapp","WEB-INF","uploads",r[2]);
                String uploadDir = path + "/";
                for(MultipartFile file : files){
                    if(!file.isEmpty()){
                        FileUploadService.saveFile(uploadDir,file.getOriginalFilename(),file);
                        attachments.add(new Attachment(file.getOriginalFilename(),"/uploads/"+r[2]+"/"+file.getOriginalFilename()));
                    }
                }
                attachmentRepository.saveAllAndFlush(attachments);
                documentation.getDocumentationAttachmentsSet().addAll(attachments);
            }
        }
        documentationRepository.saveAndFlush(documentation);
        return "redirect:/t/"+ticket;
    }
}
