package com.gestiondesannotateurs.repositories;

import com.gestiondesannotateurs.entities.AnnotationClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AnnotationRepo extends JpaRepository<AnnotationClass, Long> {
    List<AnnotationClass> findByAnnotatorId(Long annotatorId);
    List<AnnotationClass> findByCoupletextId(Long coupletextId);
    List<AnnotationClass>findByAnnotatorIdAndCoupletextId(Long annotatorId,Long CoupleOfTextId);
    @Query("SELECT a FROM AnnotationClass a WHERE a.coupletext.dataset.id = :datasetId")
    List<AnnotationClass> findByDatasetId(@Param("datasetId") Long datasetId);

    @Query("SELECT a FROM AnnotationClass a WHERE a.coupletext.dataset.id = :datasetId and a.isAdmin = true ")
    List<AnnotationClass>  findAdminsByDatasetId(@Param("datasetId") Long datasetId);


    @Query("SELECT a FROM AnnotationClass a WHERE a.coupletext.id = :coupleofTextId and a.isAdmin = true ")
    Optional<AnnotationClass> findAdminsAnnotationAnnotationByCoupleOfText(@Param("coupleofTextId") Long coupleofTextId);

    @Query("SELECT a FROM AnnotationClass a WHERE a.annotator.id = :annotatorId AND a.coupletext.id = :coupleOfTextId")
    Optional<AnnotationClass> findByAnnotatorIdSharedWithAdmin(Long annotatorId , Long coupleOfTextId);


    @Query("SELECT COUNT(a) FROM AnnotationClass a WHERE a.createdAt >= :startTime")
    long countAnnotationsInLast24Hours(@Param("startTime") LocalDateTime startTime);
}
