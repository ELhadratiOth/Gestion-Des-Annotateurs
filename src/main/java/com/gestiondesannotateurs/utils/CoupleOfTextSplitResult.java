package com.gestiondesannotateurs.utils;

import com.gestiondesannotateurs.entities.Coupletext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@AllArgsConstructor
public class CoupleOfTextSplitResult {
    private final List<Coupletext> adminList;
    private final List<Coupletext> othersList;

}
