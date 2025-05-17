package com.gestiondesannotateurs.controllers;
import com.gestiondesannotateurs.services.KappaEvaluationServiceImpl;
import com.gestiondesannotateurs.shared.GlobalResponse;
import com.gestiondesannotateurs.shared.Exceptions.GlobalSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/kappa")
public class KappaEvaluationController {
    @Autowired
    private KappaEvaluationServiceImpl kappaService;

    @PostMapping("/calculate")
    public ResponseEntity<GlobalResponse<Double>> calculateKappa(
            @RequestBody List<List<Integer>> annotations){
        double kappa=kappaService.calculateKappa(annotations);

        return GlobalSuccessHandler.success("Kappa score calculated successfully",kappa);
    }
}
