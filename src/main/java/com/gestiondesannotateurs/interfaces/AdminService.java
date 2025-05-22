package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.dtos.AdminDto;
import com.gestiondesannotateurs.dtos.AdminDtoo;
import com.gestiondesannotateurs.dtos.CoupleOfTextWithAnnotation;
import com.gestiondesannotateurs.entities.Admin;
import com.gestiondesannotateurs.entities.Coupletext;
import com.gestiondesannotateurs.entities.Dataset;

import java.util.List;

public interface AdminService {
    Admin getAdminById(Long adminId);
    List<Admin> getAllAdmins();
    Admin createAdmin(AdminDto adminDto);
    Admin updateAdmin(Long adminId, AdminDtoo adminDto);
    void deleteAdmin(Long adminId);
    public void deactivateAdmin(Long id);
    public void activateAdmin(Long id);

    public List<Coupletext> getAnonnotatedCoupletexts(Long datasetId);

    List<CoupleOfTextWithAnnotation>  getListOfCoupleOfTextWithThereAnnotation(Long datasetId) ;
}