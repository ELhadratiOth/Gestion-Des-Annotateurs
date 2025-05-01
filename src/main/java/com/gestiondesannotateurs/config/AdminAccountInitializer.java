package com.gestiondesannotateurs.config;


import com.gestiondesannotateurs.entities.Admin;
import com.gestiondesannotateurs.repositories.AdminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountInitializer implements CommandLineRunner {

    @Autowired
    private  AdminRepo adminRepo;
    @Value("${admin.first.name}")
    private String adminFirstName;
    @Value("${admin.last.name}")
    private String adminLastName;
    @Value("${admin.login}")
    private String adminLogin;
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
        if (adminRepo.findByEmail(adminEmail).isEmpty()) {
            Admin admin = new Admin();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setFirstName(adminFirstName);
            admin.setLastName(adminLastName);
            admin.setLogin(adminLogin);
            admin.setActive(true);
            adminRepo.save(admin);
            System.out.println("Admin account created successfully.");
        } else {
            System.out.println("Admin account already exists.");
        }
    }
}