package com.gestiondesannotateurs.utils;

import com.gestiondesannotateurs.entities.Coupletext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;
import java.util.List;

@Component
public class AdminAnnotationSplitter {
    public static CoupleOfTextSplitResult splitCoupletexts(List<Coupletext> coupletexts) {
        List<Coupletext> adminList = new ArrayList<>();
        List<Coupletext> othersList = new ArrayList<>();

        if (coupletexts == null || coupletexts.isEmpty()) {
            return new CoupleOfTextSplitResult(adminList, othersList);
        }

        if (coupletexts.size() > 10) {
            adminList = new ArrayList<>(coupletexts.subList(0, 10));
            othersList = new ArrayList<>(coupletexts.subList(10, coupletexts.size()));
        } else {
            int splitPoint = Math.min(3, coupletexts.size());
            adminList = new ArrayList<>(coupletexts.subList(0, splitPoint));
            othersList = new ArrayList<>(coupletexts.subList(splitPoint, coupletexts.size()));
        }

        return new CoupleOfTextSplitResult(adminList, othersList);
    }
}
