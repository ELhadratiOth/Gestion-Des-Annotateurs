package com.gestiondesannotateurs.interfaces;

import com.gestiondesannotateurs.dtos.AdminDto;
import com.gestiondesannotateurs.dtos.AdminDtoo;
import com.gestiondesannotateurs.dtos.AdminTask;
import com.gestiondesannotateurs.dtos.CoupleOfTextWithAnnotation;
import com.gestiondesannotateurs.entities.Admin;
import com.gestiondesannotateurs.entities.Coupletext;

import java.util.List;

public interface AdminService {
    Admin getAdminById(Long adminId);
    List<Admin> getAllAdmins();
    Admin createAdmin(AdminDto adminDto);
    Admin updateAdmin(Long adminId, AdminDtoo adminDto);
    void deleteAdmin(Long adminId);
     void deactivateAdmin(Long id);
     void activateAdmin(Long id);

     List<Coupletext> getAnonnotatedCoupletexts(Long datasetId);

    List<CoupleOfTextWithAnnotation>  getListOfCoupleOfTextWithThereAnnotation(Long datasetId) ;

    List<AdminTask> getListOfTasksForAdmin();

}