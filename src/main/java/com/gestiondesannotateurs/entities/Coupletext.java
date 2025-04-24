package com.gestiondesannotateurs.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "couple_of_texts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coupletext {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String textA;
    private String textB;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private TaskToDo task;
    @OneToMany(mappedBy = "coupletext", fetch = FetchType.LAZY)
    private List<AnnotationClass> annotations = new ArrayList<>();
}
