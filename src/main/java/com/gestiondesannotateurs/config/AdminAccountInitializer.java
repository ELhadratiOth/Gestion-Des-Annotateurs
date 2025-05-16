package com.gestiondesannotateurs.config;
import com.gestiondesannotateurs.entities.Admin;
import com.gestiondesannotateurs.entities.AnnotationClass;
import com.gestiondesannotateurs.entities.Othman;
import com.gestiondesannotateurs.repositories.AdminRepo;
import com.gestiondesannotateurs.repositories.OthmanRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountInitializer implements CommandLineRunner {

    @Autowired
    private  AdminRepo adminRepo;

    @Autowired
    private OthmanRepo othmanRepo;
    @Value("${admin.first.name}")
    private String adminFirstName;
    @Value("${admin.last.name}")
    private String adminLastName;
    @Value("${admin.login}")
    private String adminUserName;
    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.password}")
    private String adminPassword;

    private final PasswordEncoder passwordEncoder;

    public AdminAccountInitializer(AdminRepo adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepo = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void run(String... args) {
        if (!othmanRepo.existsByEmail(adminEmail)) {
            Othman admin = new Othman();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setFirstName(adminFirstName);
            admin.setLastName(adminLastName);
            admin.setUserName(adminUserName);
            admin.setActive(true);
            admin.setRole("SUPER_ADMIN");
            othmanRepo.save(admin);
            System.out.println("Super Admin account created successfully.");
        } else {
            System.out.println("super admin account already exists.");
        }
    }

}