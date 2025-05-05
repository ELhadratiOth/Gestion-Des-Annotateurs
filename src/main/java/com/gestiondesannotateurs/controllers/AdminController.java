package com.gestiondesannotateurs.controllers;
import com.gestiondesannotateurs.dtos.AdminDto;
import com.gestiondesannotateurs.entities.Admin;
import com.gestiondesannotateurs.services.adminServiceImpl;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import com.gestiondesannotateurs.shared.GlobalResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {
    @Autowired
    private adminServiceImpl adminService;

    @GetMapping("/{adminId}")
    public ResponseEntity<GlobalResponse<Admin>> getAdminDetails(@PathVariable Long adminId) {
        Admin admin = adminService.getAdminById(adminId);
        return GlobalSuccessHandler.success("Admin details retrieved successfully", admin);
    }
    @GetMapping
    public ResponseEntity<GlobalResponse<List<Admin>>> getAllAdminDetails() {
        List<Admin> admins = adminService.getAllAdmins();
        return GlobalSuccessHandler.success("All admins retrieved successfully", admins);
    }

    @PostMapping
    public ResponseEntity<GlobalResponse<Admin>> createAdminDetails(@Valid @RequestBody AdminDto adminDto) {
        Admin createdAdmin = adminService.createAdmin(adminDto);
        return GlobalSuccessHandler.created("Admin created successfully", createdAdmin);
    }
    @PutMapping("/{adminId}")
    public ResponseEntity<GlobalResponse<Admin>> updateAdminDetails(
            @PathVariable Long adminId,
            @Valid @RequestBody AdminDto adminDto) {
        Admin updatedAdmin = adminService.updateAdmin(adminId, adminDto);
        return GlobalSuccessHandler.success("Admin updated successfully", updatedAdmin);
    }
    @DeleteMapping("/{adminId}")
    public ResponseEntity<GlobalResponse<String>> deleteAdminDetails(@PathVariable Long adminId) {
        adminService.deleteAdmin(adminId);
        return GlobalSuccessHandler.deleted("Admin supprimé avec succès");
    }
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<GlobalResponse<Admin>> deactivateAnnotator(@PathVariable Long id) {
        adminService.deactivateAdmin(id);
        Admin admin = adminService.getAdminById(id);
        return GlobalSuccessHandler.success("Annotateur désactivé", admin);
    }
    @PatchMapping("/{id}/activate")
    public ResponseEntity<GlobalResponse<Admin>> activateAnnotator(@PathVariable Long id) {
        adminService.activateAdmin(id);
        Admin admin = adminService.getAdminById(id);
        return GlobalSuccessHandler.success("Annotateur activé", admin);
    }
}
