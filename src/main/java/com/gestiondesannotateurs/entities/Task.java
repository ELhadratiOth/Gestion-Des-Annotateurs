package com.gestiondesannotateurs.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "annotator_id", nullable = false)
//    private Annotator annotator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dataset_id", nullable = false)
    private Dataset dataset;
}