package com.gestiondesannotateurs.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.scheduling.config.Task;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("ANNOTATOR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Annotator extends Person {
    @OneToMany(mappedBy = "annotator", fetch = FetchType.LAZY)
    private List<TaskToDo> tasks = new ArrayList<>();
    @OneToMany(mappedBy = "annotator", fetch = FetchType.LAZY)
    private List<AnnotationClass> annotations = new ArrayList<>();
}
