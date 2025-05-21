package com.gestiondesannotateurs.entities;


import com.fasterxml.jackson.annotation.JsonProperty;
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
    @Column(unique = true, nullable = false)
    private String textA;
    @Column(unique = true, nullable = false)
    private String textB;

    @Column()
//    @Column(nullable = false)
    private Boolean isAnnotatedByAdmin = false;
    @Column

    private Boolean isDuplicated = false;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "dataset_id")
    private Dataset dataset;




    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "couple_text_and_task",
            joinColumns = @JoinColumn(name = "coupletext_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id")
    )
    private List<TaskToDo>  tasks;
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    @OneToMany(mappedBy = "coupletext", fetch = FetchType.LAZY)
//    private List<AnnotationClass> annotations = new ArrayList<>();
//

    public void addTask(TaskToDo task) {
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        if (!tasks.contains(task)) {
            tasks.add(task);
            task.getCoupletexts().add(this);
        }
    }

}
