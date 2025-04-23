package com.gestiondesannotateurs.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "text_couples")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TextCouple {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private String text1;
    @Column()
    private String text2;


    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id" , nullable = false)
    private List<Task> tasks;




}
