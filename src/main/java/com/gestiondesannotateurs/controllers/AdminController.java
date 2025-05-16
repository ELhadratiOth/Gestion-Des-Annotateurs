package com.gestiondesannotateurs.controllers;
import com.gestiondesannotateurs.dtos.AdminDto;
import com.gestiondesannotateurs.dtos.CoupleOfTextWithAnnotation;
import com.gestiondesannotateurs.entities.Admin;
import com.gestiondesannotateurs.entities.AnnotationClass;
import com.gestiondesannotateurs.entities.Coupletext;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.interfaces.AdminService;
import com.gestiondesannotateurs.repositories.AnnotationRepo;
import com.gestiondesannotateurs.repositories.CoupleOfTextRepo;
import com.gestiondesannotateurs.shared.Exceptions.CustomResponseException;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import com.gestiondesannotateurs.shared.GlobalResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admins")
public class AdminController {
    @Autowired
    private AdminService adminService;


    @GetMapping("/{adminId}")
    @PreAuthorize("hasAnyRole('SUPER-ADMIN','ADMIN')")
    public ResponseEntity<GlobalResponse<Admin>> getAdminDetails(@PathVariable Long adminId) {
        Admin admin = adminService.getAdminById(adminId);
        return GlobalSuccessHandler.success("Admin details retrieved successfully", admin);
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER-ADMIN')")
    public ResponseEntity<GlobalResponse<List<Admin>>> getAllAdminDetails() {
        List<Admin> admins = adminService.getAllAdmins();
        return GlobalSuccessHandler.success("All admins retrieved successfully", admins);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER-ADMIN')")
    public ResponseEntity<GlobalResponse<Admin>> createAdminDetails(@Valid @RequestBody AdminDto adminDto) {
        Admin createdAdmin = adminService.createAdmin(adminDto);
        return GlobalSuccessHandler.created("Admin created successfully", createdAdmin);
    }
    @PutMapping("/{adminId}")
    @PreAuthorize("hasAnyRole('SUPER-ADMIN','ADMIN')")
    public ResponseEntity<GlobalResponse<Admin>> updateAdminDetails(
            @PathVariable Long adminId,
            @Valid @RequestBody AdminDto adminDto) {
        Admin updatedAdmin = adminService.updateAdmin(adminId, adminDto);
        return GlobalSuccessHandler.success("Admin updated successfully", updatedAdmin);
    }
    @DeleteMapping("/{adminId}")
    @PreAuthorize("hasAnyRole('SUPER-ADMIN')")
    public ResponseEntity<GlobalResponse<String>> deleteAdminDetails(@PathVariable Long adminId) {
        adminService.deleteAdmin(adminId);
        return GlobalSuccessHandler.deleted("Admin supprimé avec succès");
    }
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('SUPER-ADMIN')")
    public ResponseEntity<GlobalResponse<Admin>> deactivateAdmin(@PathVariable Long id) {
        adminService.deactivateAdmin(id);
        Admin admin = adminService.getAdminById(id);
        return GlobalSuccessHandler.success("Admin désactivé", admin);
    }
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('SUPER-ADMIN')")
    public ResponseEntity<GlobalResponse<Admin>> activateAdmin(@PathVariable Long id) {
        adminService.activateAdmin(id);
        Admin admin = adminService.getAdminById(id);
        return GlobalSuccessHandler.success("Admin activé", admin);
    }


    @GetMapping("/coupleoftextannotated/{datasetId}")
    @PreAuthorize("hasAnyRole('SUPER-ADMIN','ADMIN')")
    public ResponseEntity<GlobalResponse<List<CoupleOfTextWithAnnotation>>> getAdminsCoupleOfTextWithTheirAnnotationsByDatasetId(@PathVariable Long datasetId) {
        List<CoupleOfTextWithAnnotation>  coupleOfTextWithAnnotations = adminService.getListOfCoupleOfTextWithThereAnnotation(datasetId);
        return GlobalSuccessHandler.success(
                "Successfully retrieved admin coupletexts for dataset id = " + datasetId,
                coupleOfTextWithAnnotations
        );
    }


}
