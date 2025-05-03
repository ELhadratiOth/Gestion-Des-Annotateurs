package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.AdminDto;
import com.gestiondesannotateurs.entities.Admin;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.interfaces.AdminService;
import com.gestiondesannotateurs.repositories.AdminRepo;
import com.gestiondesannotateurs.shared.Exceptions.AdminNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl  implements AdminService{
    @Autowired
    private AdminRepo adminRepository;// crreer que admin repo
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Admin getAdminById(Long adminId) {
        return adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminNotFoundException(adminId));
    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public Admin createAdmin(AdminDto adminDto) {
        if (adminRepository.existsByEmail(adminDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Admin newAdmin = new Admin();
        newAdmin.setFirstName(adminDto.getFirstName());
        newAdmin.setLastName(adminDto.getLastName());
        newAdmin.setEmail(adminDto.getEmail());
        newAdmin.setLogin(adminDto.getLogin());
        newAdmin.setPassword(passwordEncoder.encode("1234")); // Default password
        newAdmin.setActive(true);

        return adminRepository.save(newAdmin);
    }

    @Override
    public Admin updateAdmin(Long adminId, AdminDto adminDto) {
        return null;
    }

    @Override
    public void deleteAdmin(Long adminId) {
        if (!adminRepository.existsById(adminId)) {
            throw new AdminNotFoundException(adminId);
        }
        adminRepository.deleteById(adminId);
    }

    public void deactivateAdmin(Long id) {
            Admin admin= adminRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Annotateur introuvable"));

            admin.setActive(false);
            adminRepository.save(admin);
    }

}
