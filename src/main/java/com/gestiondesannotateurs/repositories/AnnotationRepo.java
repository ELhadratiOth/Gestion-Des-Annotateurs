package com.gestiondesannotateurs.repositories;

import com.gestiondesannotateurs.entities.AnnotationClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
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
    List<AnnotationClass> findByAnnotatorIdSharedWithAdmin(Long annotatorId , Long coupleOfTextId);


    @Query("SELECT COUNT(a) FROM AnnotationClass a WHERE a.createdAt >= :startTime")
    long countAnnotationsInLast24Hours(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT a FROM AnnotationClass a WHERE a.coupletext.id = :coupleofTextId AND a.isAdmin = true ")
    Optional<AnnotationClass> findIfAlreadyAnnotatedByAdmin(@Param("coupleofTextId")  Long coupleofTextId);

    @Query("SELECT DISTINCT CAST(a.createdAt AS LocalDate) FROM AnnotationClass a WHERE a.annotator.id = :annotatorId")
    List<LocalDate> findDistinctAnnotationDatesByAnnotator(@Param("annotatorId") Long annotatorId);

    @Query("SELECT a FROM AnnotationClass a WHERE a.annotator.id = :annotatorId ORDER BY a.createdAt DESC LIMIT 3")
    List<AnnotationClass> findTop5RecentAnnotationsByAnnotator(@Param("annotatorId") Long annotatorId);


    @Query("SELECT a FROM AnnotationClass a ORDER BY a.createdAt DESC LIMIT 3")
    List<AnnotationClass> findTop5RecentTeamAnnotations();

    @Query("SELECT COUNT(a) FROM AnnotationClass a WHERE a.annotator.id = :annotatorId AND a.createdAt >= :startTime")
    long countAnnotationsByAnnotatorSince(@Param("annotatorId") Long annotatorId,
                                          @Param("startTime") LocalDateTime startTime);


    Collection<Object> findTop5ByAnnotatorIdOrderByCreatedAtDesc(Long annotatorId);
}
