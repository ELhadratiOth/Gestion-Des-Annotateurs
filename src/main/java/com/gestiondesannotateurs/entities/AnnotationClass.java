package com.gestiondesannotateurs.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "annotations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnnotationClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String choosenLabel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annotator_id")
    private Person annotator;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupletext_id")
    private Coupletext coupletext;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private Boolean isAdmin = false ;
    // afficher les ananotations de tous les annotateurs by datasetid
    // affichage de annotateurID ET  label coupletext

}