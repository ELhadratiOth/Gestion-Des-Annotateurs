package com.gestiondesannotateurs.shared.Exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomResponseException  extends RuntimeException{
    private  int  code = 404 ;
    public   String msg ;

}
