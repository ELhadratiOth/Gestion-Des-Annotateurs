package com.gestiondesannotateurs.repositories;

import com.gestiondesannotateurs.entities.AnnotationClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnnotationRepo extends JpaRepository<AnnotationClass, Long> {
    List<AnnotationClass> findByAnnotatorId(Long annotatorId);
    List<AnnotationClass>findByAnnotatorIdAndCoupletextId(Long annotatorId,Long CoupleOfTextId);
    @Query("SELECT a FROM AnnotationClass a WHERE a.coupletext.dataset.id = :datasetId")
    List<AnnotationClass> findByDatasetId(@Param("datasetId") Long datasetId);

}
