package com.gestiondesannotateurs.services;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestiondesannotateurs.dtos.AnnotatorDto;
import com.gestiondesannotateurs.entities.Annotator;
import com.gestiondesannotateurs.interfaces.AnnotatorService;
import com.gestiondesannotateurs.repositories.AnnotatorRepo;
import com.gestiondesannotateurs.shared.Exceptions.AnnotatorNotFoundException;

@Service
public class AnnotatorServiceImpl implements AnnotatorService {
	
    @Autowired
    private AnnotatorRepo annotatorRepository;
    
    @Autowired
    private  BCryptPasswordEncoder passwordEncoder;

    @Override
    public Annotator getAnnotatorById(Long annotatorId) {
        return annotatorRepository.findById(annotatorId)
                .orElseThrow(() -> new AnnotatorNotFoundException(annotatorId));
    }

    @Override
    public List<Annotator> getAllAnnotators() {
        return annotatorRepository.findAll();
    }

    @Override
    public Annotator createAnnotator( AnnotatorDto annotator) {
    	Annotator newAnnotator = new Annotator();
		newAnnotator.setFirstName(annotator.getFirstName());
		newAnnotator.setLastName(annotator.getLastName());
		newAnnotator.setEmail(annotator.getEmail());
		newAnnotator.SetLogin(annotator.getLogin());
        newAnnotator.setSpammer(false);
        newAnnotator.setActive(true);
    	if (annotatorRepository.existsByEmail(newAnnotator.getEmail())) {
			throw new AnnotatorNotFoundException("Un annotateur avec cet email existe déjà: " + annotator.getEmail());
		}
        return annotatorRepository.save(annotator);
    }

    @Override
    public Annotator updateAnnotator(Long annotatorId, AnnotatorDto annotator) {
    	Annotator existingAnnotator = annotatorRepository.findById(annotatorId)
				.orElseThrow(() -> new AnnotatorNotFoundException("Annotateur non trouvé avec l'ID: " + annotatorId));
    	
    	Annotator currentAnnotator=existingAnnotator.get();
    	String firstName = annotator.getFirstName();
    	if (firstName != null) {
			currentAnnotator.setFirstName(firstName);
		}
    	String lastName = annotator.getLastName();
    	if (lastName != null) {
    		currentAnnotator.setLastName(lastName);
    	}
    	String login = annotator.getLogin();
    	if (login != null) {
    		currentAnnotator.setLogin(login);
    	}
    	String email = annotator.getEmail();
    	if (email != null) {
			currentAnnotator.setEmail(email);
		}
    	return annotatorRepository.save(currentAnnotator);
    }

    @Override
    public void deleteAnnotator(Long annotatorId) {
        if (!annotatorRepository.existsById(annotatorId)) {
            throw new AnnotatorNotFoundException(annotatorId);
        }
        annotatorRepository.deleteById(annotatorId);
    }
    
    public void markAsSpammer(Long id) {
        Annotator annotator = annotatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Annotateur introuvable"));

        annotator.setSpammer(true);
        annotatorRepository.save(annotator);
    }
    
    public void deactivateAnnotator(Long id) {
		Annotator annotator = annotatorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Annotateur introuvable"));

		annotator.setActive(false);
		annotatorRepository.save(annotator);
    
    
	}
}