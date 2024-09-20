package com.shared.idgeneration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.shared.idgeneration.service.IdGenerationService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/id-generation")
public class IdGenerationController {

    @Autowired
    private IdGenerationService idGenerationService;

    @GetMapping("/generate")
    public ResponseEntity<String> generateId(@RequestParam(required = false) String idType) {
        if (idType == null || idType.isEmpty()) {
            idType = "DEFAULT"; // Provide a default ID type if not provided
        }
        
        try {
            return ResponseEntity.ok(idGenerationService.generateId(idType));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Generates a supervisor ID specifically.
     *
     * @return The generated supervisor ID.
     */
    @GetMapping("/generate-supervisor-id")
    public ResponseEntity<String> generateSupervisorId() {
        return ResponseEntity.ok(idGenerationService.generateId("SUP"));
    }

    /**
     * Generates a project ID specifically.
     *
     * @return The generated project ID.
     */
    @GetMapping("/generate-project-id")
    public ResponseEntity<String> generateProjectId() {
        return ResponseEntity.ok(idGenerationService.generateId("PRO"));
    }
}
