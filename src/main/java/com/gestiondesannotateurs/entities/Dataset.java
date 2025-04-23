package com.gestiondesannotateurs.entities;

import com.gestiondesannotateurs.entities.Label;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "datasets")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Dataset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false )
    private LocalDate creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "label_id", nullable = false)
    private Label label;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "author_id")
//    private Admin admin;

    public Long getLabel() {
        return label.getId();
    }
}