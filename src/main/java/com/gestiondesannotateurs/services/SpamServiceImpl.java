package com.gestiondesannotateurs.services;

import com.gestiondesannotateurs.interfaces.SpamService;
import org.springframework.stereotype.Service;


@Service
public class SpamServiceImpl implements SpamService {


    @Override
    public boolean scanAnnotatorsBasedOnAdminAnnotations() {
        return false;
    }
}
