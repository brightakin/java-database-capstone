package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.Service;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;
    private final TokenService tokenService;

    public PatientController(PatientService patientService, Service service, TokenService tokenService) {
        this.patientService = patientService;
        this.service = service;
        this.tokenService = tokenService;
    }

    // 3. Get patient details
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPatient(@RequestHeader("Authorization") String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode())
                    .body(Map.of("message", validation.getBody().get("message")));
        }

        return patientService.getPatientDetails(token);
    }

    // 4. Create new patient
    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody @Valid Patient patient) {
        boolean isValid = service.validatePatient(patient);
        if (!isValid) {
            return ResponseEntity.status(409).body(Map.of("message", "Patient already exists"));
        }

        int result = patientService.createPatient(patient);
        if (result == 0) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to create patient"));
        }
        return ResponseEntity.status(201).body(Map.of("message", "Patient created successfully"));
    }

    // 5. Patient login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid Login login) {
        return service.validatePatientLogin(login);
    }

    // 6. Get patient appointments
    @GetMapping("/appointments/{id}")
    public ResponseEntity<Map<String, Object>> getPatientAppointments(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode())
                    .body(Map.of("message", validation.getBody().get("message")));
        }

        return patientService.getPatientAppointment(id, token);
    }

    // 7. Filter patient appointments
    @GetMapping("/appointments/filter")
    public ResponseEntity<Map<String, Object>> filterPatientAppointments(
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String name,
            @RequestHeader("Authorization") String token
    ) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode())
                    .body(Map.of("message", validation.getBody().get("message")));
        }

        return service.filterPatient(condition, name, token);
    }
}



