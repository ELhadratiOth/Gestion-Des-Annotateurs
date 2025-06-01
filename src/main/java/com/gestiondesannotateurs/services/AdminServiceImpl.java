package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.dtos.AdminDto;
import com.gestiondesannotateurs.dtos.AdminDtoo;
import com.gestiondesannotateurs.dtos.AdminTask;
import com.gestiondesannotateurs.dtos.CoupleOfTextWithAnnotation;
import com.gestiondesannotateurs.entities.Admin;
import com.gestiondesannotateurs.entities.AnnotationClass;
import com.gestiondesannotateurs.entities.Coupletext;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.interfaces.AdminService;
import com.gestiondesannotateurs.repositories.AdminRepo;
import com.gestiondesannotateurs.repositories.AnnotationRepo;
import com.gestiondesannotateurs.repositories.CoupleOfTextRepo;
import com.gestiondesannotateurs.repositories.DatasetRepo;
import com.gestiondesannotateurs.shared.Exceptions.AdminNotFoundException;
import com.gestiondesannotateurs.shared.Exceptions.AnnotatorNotFoundException;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    @Autowired
    private EmailService emailService;

    @Autowired
    private AnnotationRepo annotationRepo;

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
        String token = UUID.randomUUID().toString();
        try {

            newAdmin.setFirstName(adminDto.getFirstName());
            newAdmin.setLastName(adminDto.getLastName());
            newAdmin.setEmail(adminDto.getEmail());
//            newAdmin.setPassword(passwordEncoder.encode(adminDto.getPassword()));
            newAdmin.setActive(true);
            newAdmin.setActive(true);
            newAdmin.setVerified(false);
            newAdmin.setRole("ADMIN");
            newAdmin.setAccountCreationToken(token);
            emailService.sendAccountCreationEmail(newAdmin.getEmail(), token);

            return adminRepository.save(newAdmin);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public Admin updateAdmin(Long adminId, AdminDtoo adminDto) {
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

        if (adminDto.getLogin() != null && !adminDto.getLogin().equals(existingAdmin.getUsername())) {
            existingAdmin.setUserName(adminDto.getLogin());
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

        admin.setActive(!admin.isActive());
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
    public List<Coupletext> getAnonnotatedCoupletexts(Long datasetId) {

        Optional<Dataset> dataset = datasetRepo.findById(datasetId);
        if(dataset.isEmpty()){
            throw new CustomResponseException(404 , "Dataset not found");
        }
        List<Coupletext> coupletexts = coupleOfTextRepo.getAllByDatasetAndIsAnnotatedByAdmin(dataset.get() , true );
        System.out.println(coupletexts);

        return coupletexts  ;
    }

    @Override
    public List<CoupleOfTextWithAnnotation> getListOfCoupleOfTextWithThereAnnotation(Long datasetId) {
        List<Coupletext> coupletexts = getAnonnotatedCoupletexts(datasetId); // only the affecteds to  the  admin

        Dataset dataset =  datasetRepo.findById(datasetId).orElseThrow(() -> new CustomResponseException(404 , "Dataset not found"));

        List<CoupleOfTextWithAnnotation> coupleOfTextWithAnnotations = new ArrayList<>();
        for (int i = 0; i < coupletexts.size(); i++) {
            Coupletext coupletext = coupletexts.get(i);
            System.out.println("this  is the coupletext id = " + coupletext.getId() );
            Optional<AnnotationClass> annotation = annotationRepo.findAdminsAnnotationAnnotationByCoupleOfText(coupletext.getId());
//            System.out.println("this  is the annotation i= " + annotation.get().getChoosenLabel() );

            if(annotation.isEmpty()){
                CoupleOfTextWithAnnotation dto = new CoupleOfTextWithAnnotation(
                        coupletext.getId(),
                        null,
                        coupletext.getTextA(),
                        coupletext.getTextB(),
                        null,
                        dataset.getName(),
                        dataset.getLabel().getName(),
                        dataset.getLabel().getClasses()

                        // not annotated yet
                );
                coupleOfTextWithAnnotations.add(dto);
//                System.out.println("this  is the dto = " + dto );
            }
            else{
                CoupleOfTextWithAnnotation dto = new CoupleOfTextWithAnnotation(
                        coupletext.getId(),
                        annotation.get().getId(),
                        coupletext.getTextA(),
                        coupletext.getTextB(),
                        annotation.get().getChoosenLabel(),
                        dataset.getName(),
                        dataset.getLabel().getName(),
                        dataset.getLabel().getClasses()
                );
                coupleOfTextWithAnnotations.add(dto);

            }
    }
        System.out.println(coupleOfTextWithAnnotations.size());
        return coupleOfTextWithAnnotations ;
    }

    @Override
    public List<AdminTask> getListOfTasksForAdmin() {
        List<Dataset> datasets = datasetRepo.findAll();

        List<AdminTask> adminTasks = new ArrayList<>();

        for(Dataset dataset : datasets){
            List<CoupleOfTextWithAnnotation> coupleOfTextWithAnnotations =    getListOfCoupleOfTextWithThereAnnotation(dataset.getId());
            System.out.println("dataset in search :" + dataset.getName());
            int counter = 0 ;
            for(CoupleOfTextWithAnnotation coupleOfTextWithAnnotation : coupleOfTextWithAnnotations){
                if (coupleOfTextWithAnnotation.annotationId() != null){
                    counter++;
                }
            }

            Long adminRowsInDataset = dataset.getSize() > 10 ? 10L : dataset.getSize()   ;
            Double advancement = (Double) (counter / (double) adminRowsInDataset * 100);
            System.out.println("advancement :" + advancement);
            String status = advancement == 100.0 ? "Completed" :  advancement == 0.0 ? "Not Start" : "In Progress" ;
            String action = status.equals("Completed") ? "Review" : status.equals("Not Start") ? "Start" : "Continue" ;
//            String availableLabelClasses = dataset.getLabel().getClasses();

            AdminTask adminTask = new AdminTask(
                    dataset.getId(),
                    dataset.getName(),
                    dataset.getDescription(),
                    dataset.getLabel().getName(),
                    advancement,
                    status,
                    action,
                    adminRowsInDataset
            );
            adminTasks.add(adminTask);
        }
        return adminTasks;
    }


}
