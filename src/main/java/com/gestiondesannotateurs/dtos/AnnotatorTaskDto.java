package com.gestiondesannotateurs.dtos;
import com.gestiondesannotateurs.entities.Annotator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
  public class AnnotatorTaskDto {
    private final Long taskId;
    private final Long annotatorId;
    private final String firstName;
    private final String lastName;
    private final String login;
    private final String email;
}
