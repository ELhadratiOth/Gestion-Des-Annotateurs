package com.gestiondesannotateurs.dtos;


import lombok.Data;


public record TaskToDoDto ( Long id ,  Long annotatorId , String annotatorName ) {

}