package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.AdminDto;
import com.gestiondesannotateurs.entities.Admin;
import com.gestiondesannotateurs.entities.Coupletext;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.interfaces.AdminService;
import com.gestiondesannotateurs.repositories.AdminRepo;
import com.gestiondesannotateurs.repositories.CoupleOfTextRepo;
import com.gestiondesannotateurs.repositories.DatasetRepo;
import com.gestiondesannotateurs.shared.Exceptions.AdminNotFoundException;
import com.gestiondesannotateurs.shared.Exceptions.AnnotatorNotFoundException;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminRepo adminRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private CoupleOfTextRepo coupleOfTextRepo;
    @Autowired
    private DatasetRepo datasetRepo;

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
        Admin existingAdmin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminNotFoundException(adminId));

        // Mise à jour des champs seulement s'ils sont fournis et différents
        if (adminDto.getFirstName() != null && !adminDto.getFirstName().equals(existingAdmin.getFirstName())) {
            existingAdmin.setFirstName(adminDto.getFirstName());
        }

        if (adminDto.getLastName() != null && !adminDto.getLastName().equals(existingAdmin.getLastName())) {
            existingAdmin.setLastName(adminDto.getLastName());
        }

        if (adminDto.getEmail() != null && !adminDto.getEmail().equals(existingAdmin.getEmail())) {
            if (adminRepository.existsByEmail(adminDto.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
            existingAdmin.setEmail(adminDto.getEmail());
        }

        if (adminDto.getLogin() != null && !adminDto.getLogin().equals(existingAdmin.getLogin())) {
            existingAdmin.setLogin(adminDto.getLogin());
        }
        return adminRepository.save(existingAdmin);
    }

    @Override
    public void deleteAdmin(Long adminId) {
        if (!adminRepository.existsById(adminId)) {
            throw new AnnotatorNotFoundException(adminId);
        }
        adminRepository.deleteById(adminId);
    }
    @Override
    public void deactivateAdmin(Long id) {
        Admin admin= adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin introuvable"));

        admin.setActive(false);
        adminRepository.save(admin);
    }
    @Override
    public void activateAdmin(Long id) {
        Admin admin= adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin introuvable"));

        admin.setActive(true);
        adminRepository.save(admin);
    }

    @Override
    public List<Coupletext> getAnannotatedCoupletexts(Long datasetId) {

        Optional<Dataset> dataset = datasetRepo.findById(datasetId);
        if(dataset.isEmpty()){
            throw new CustomResponseException(404 , "Dataset not found");
        }
        List<Coupletext> coupletexts = coupleOfTextRepo.getAllByDatasetAndIsAnnotatedByAdmin(dataset.get() , true );
        System.out.println(coupletexts);

        return coupletexts  ;
    }



}
