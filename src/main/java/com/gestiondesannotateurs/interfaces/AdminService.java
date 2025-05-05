package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.dtos.AdminDto;
import com.gestiondesannotateurs.entities.Admin;

import java.util.List;

public interface AdminService {
    Admin getAdminById(Long adminId);
    List<Admin> getAllAdmins();
    Admin createAdmin(AdminDto adminDto);
    Admin updateAdmin(Long adminId, AdminDto adminDto);
    void deleteAdmin(Long adminId);
    public void deactivateAdmin(Long id);
    public void activateAdmin(Long id);
}