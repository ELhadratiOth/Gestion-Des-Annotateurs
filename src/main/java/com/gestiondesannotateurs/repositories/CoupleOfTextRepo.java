package com.gestiondesannotateurs.repositories;

import com.gestiondesannotateurs.entities.Coupletext;
import com.gestiondesannotateurs.entities.Dataset;
import com.gestiondesannotateurs.entities.TaskToDo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CoupleOfTextRepo extends JpaRepository<Coupletext,Long> {
    List<Coupletext> findByDataset(Dataset dataset);
    Page<Coupletext> findByDataset(Dataset dataset, Pageable pageable);
    @Query("SELECT c FROM Coupletext c JOIN c.tasks t WHERE t.id = :taskId")
    List<Coupletext> findByTasks_Id(@Param("taskId") Long taskId);
    @Query("SELECT c FROM Coupletext c JOIN c.tasks t WHERE t.id = :taskId")
    Page<Coupletext> findByTasks_Id(@Param("taskId") Long taskId, Pageable pageable);
    long countByTasks_Id(Long taskId);

    List<Coupletext> getAllByDatasetAndIsAnnotatedByAdmin(Dataset dataset , boolean isAnnotatedByAdmin);

}
