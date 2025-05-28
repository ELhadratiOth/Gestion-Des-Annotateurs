package com.gestiondesannotateurs.dtos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

  public record AnnotatorTeamDto(
          String firstName,
          String lastName,
          String userName,
          String email
  ) {

}
