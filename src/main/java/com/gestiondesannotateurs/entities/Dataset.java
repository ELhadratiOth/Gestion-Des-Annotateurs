package com.gestiondesannotateurs.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gestiondesannotateurs.entities.Label;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "datasets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Dataset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Long size;
    private Double advancement = 90.0 ;
    private String filePath;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "label_id")
    private Label label;


    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "dataset", fetch = FetchType.LAZY)
    private List<TaskToDo> tasks = new ArrayList<>();
//
//    public Long getLabel() {
//        return label.getId();
//    }
}
