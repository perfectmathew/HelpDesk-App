package com.perfect.hepdeskapp.config;

import com.perfect.hepdeskapp.HepDeskAppApplication;
import com.perfect.hepdeskapp.user.User;
import com.perfect.hepdeskapp.user.UserDetail;
import com.perfect.hepdeskapp.user.UserDetailService;
import com.perfect.hepdeskapp.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

@Controller
public class SettingsController {
    final
    Environment environment;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    UserRepository userRepository;
    public SettingsController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/admin/settings")
    public String settings(Model model){
        model.addAttribute("dbServer",environment.getProperty("spring.datasource.url"));
        model.addAttribute("dbUser",environment.getProperty("spring.datasource.username"));
        model.addAttribute("dbPassword",environment.getProperty("spring.datasource.password"));
        model.addAttribute("maxFileSize",environment.getProperty("spring.servlet.multipart.max-file-size"));
        model.addAttribute("maxRequestFileSize",environment.getProperty("spring.servlet.multipart.max-request-size"));
        model.addAttribute("smtpServer",environment.getProperty("spring.mail.host"));
        model.addAttribute("smtpStatus",environment.getProperty("smtp.status"));
        model.addAttribute("smtpPort",environment.getProperty("spring.mail.port"));
        model.addAttribute("smtpUsername",environment.getProperty("spring.mail.username"));
        model.addAttribute("smtpPassword",environment.getProperty("spring.mail.password"));
        model.addAttribute("appVersion",environment.getProperty("app.version"));
        model.addAttribute("serverPort",environment.getProperty("server.port"));
        model.addAttribute("maintenanceMode",environment.getProperty("app.maintenance.mode"));
        return "admin/admin_config";
    }
    @GetMapping("/admin/settings/save-changes")
    public String save_changes(Model model, @RequestParam("db_server") String db_server, @RequestParam("db_username") String db_username,
                               @RequestParam("db_password") String db_password, @RequestParam("smtp_server_status") String smtp_server_status,
                               @RequestParam("smtp_server") String smtp_server, @RequestParam("smtp_server_port") int smtp_server_port,
                               @RequestParam("smtp_username") String smtp_username,
                               @RequestParam("smtp_password") String smtp_password, @RequestParam("site_server_port") String site_server_port,
                              @RequestParam("site_maintenance_mode") String site_maintenance_mode, @RequestParam("site_max_file_size") String site_max_file_size,
                              @RequestParam("site_request_max_file_size") String site_request_max_file_size) {
        String path = System.getProperty("user.dir") + "/target/application.properties";
        if (checkDbConnection(db_server,db_username,db_password).equals("Successful")){
            if (smtp_server_status.equals("ON")) {
                if (!checkSMTPConnection(smtp_server,smtp_server_port,smtp_username,smtp_password).equals("Successful")) {
                    model.addAttribute("error","An error occurred while saving the configuration. Reason: Connection to smtp server cannot be established.");
                    return "admin/server_restart";
                }
            }
            CreateConfigurationFile(path,db_server,db_username,db_password,site_server_port,site_maintenance_mode,site_max_file_size,site_request_max_file_size,smtp_server_status,smtp_server,String.valueOf(smtp_server_port),smtp_username,smtp_password);
            HepDeskAppApplication.restart();
        }else {
            model.addAttribute("error","An error occurred while saving the configuration. Reason: Unable to connect to the database.");
            return "admin/server_restart";
        }

        model.addAttribute("message","The configuration was successfully changed. The server was automatically reset. We wish you a nice experience with our system.");
        return "admin/server_restart";
    }
    @GetMapping("/admin/settings/restart")
    public String restart(){
        HepDeskAppApplication.restart();
        return "redirect:/admin/settings";
    }
    @GetMapping("/admin/settings/update")
    public String update(){

        return "redirect:/admin/settings";
    }
    @PostMapping("/admin/settings/checkPassword")
    @ResponseBody
    public String checkAdminPassword(@RequestParam("password") String password){
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String email = ((UserDetail)principal).getUsername();
        User user = userRepository.findUserByEmail(email);
        if(bCryptPasswordEncoder.matches(password, user.getPassword())){
            return "Successful";
        }else {
            return "Password not match";
        }
    }
    @GetMapping("/admin/settings/check-smtp")
    @ResponseBody
    public String checkSMTPConnection(@RequestParam("smtp_server") String smtp_server,
                                      @RequestParam("smtp_server_port") int smtp_server_port,
                                      @RequestParam("smtp_username") String smtp_username,
                                      @RequestParam("smtp_password") String smtp_password){
        try {
            Properties props = new Properties();
            // required for gmail
            props.put("mail.smtp.starttls.enable","true");
            props.put("mail.smtp.auth", "true");

            Session session = Session.getInstance(props, null);
            Transport transport = session.getTransport("smtp");
            transport.connect(smtp_server, smtp_server_port, smtp_username, smtp_password);
            transport.close();
        }
        catch(MessagingException e) {
            e.printStackTrace();
            return "Failed";
        }
        return "Successful";
    }
    @GetMapping("/admin/settings/check-db")
    @ResponseBody
    public String checkDbConnection(@RequestParam("url") String url, @RequestParam("username") String username,
                                    @RequestParam("password") String password) {
        if (!url.contains("jdbc:postgresql")) return "This app support only postgresql database at this moment!";
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Statement stmt = null;
            ResultSet resultset = null;
            try {
                stmt = connection.createStatement();
                resultset = stmt.executeQuery("select current_database();");
                if (stmt.execute("select current_database();")) {
                    resultset = stmt.getResultSet();
                }

            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
            finally {

                if (resultset != null) {
                    resultset.close();

                }

                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException ignored) { }

                }

                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the server!", e);
        }
        return "Successful";
    }
    public void CreateConfigurationFile(String path, String db_connection_string, String db_username, String db_password,
    String site_server_port, String site_maintenance_mode, String site_max_file_size, String site_request_max_file_size,
                                        String smtp_config_status, String smtp_server, String smtp_port,
                                        String smtp_username, String smtp_password){
            try {
                File myConfiguration = new File(path);
                if (myConfiguration.createNewFile()) {

                } else {
                    WriteConfig("app.maintenance.mode = "+site_maintenance_mode+"\n" +
                            "app.version = 1.0a\n" +
                            "spring.datasource.url= "+db_connection_string+"\n" +
                            "spring.datasource.username= "+db_username+"\n" +
                            "spring.datasource.password= "+db_password+"\n" +
                            "spring.datasource.driver-class-name= org.postgresql.Driver\n" +
                            "spring.jpa.hibernate.ddl-auto= update\n" +
                            "spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation= true\n" +
                            "spring.web.resources.add-mappings=true\n" +
                            "server.port="+site_server_port+"\n" +
                            "smtp.status = "+smtp_config_status+"\n" +
                            "spring.mail.host="+smtp_server+"\n" +
                            "spring.mail.port=587\n" +
                            "spring.mail.username="+smtp_username+"\n" +
                            "spring.mail.password="+smtp_password+"\n" +
                            "spring.mail.properties.mail.transport.protocol=smtp\n" +
                            "spring.mail.properties.mail.smtp.port="+smtp_port+"\n" +
                            "spring.mail.properties.mail.smtp.auth=true\n" +
                            "spring.mail.properties.mail.smtp.starttls.enable=true\n" +
                            "spring.mail.properties.mail.smtp.starttls.required=true\n" +
                            "spring.servlet.multipart.enabled=true\n" +
                            "spring.servlet.multipart.max-file-size="+site_max_file_size+"\n" +
                            "spring.servlet.multipart.max-request-size="+site_request_max_file_size+"",path);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    public void WriteConfig(String content, String path){
        try {
            FileWriter Writer = new FileWriter(path);
            Writer.write(content);
            Writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
