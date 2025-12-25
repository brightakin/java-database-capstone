package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.AuthService;
import com.project.back_end.services.PrescriptionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final AuthService authService;
    private final AppointmentService appointmentService;

    public PrescriptionController(PrescriptionService prescriptionService, AuthService authService, AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.authService = authService;
        this.appointmentService = appointmentService;
    }

    // 3. Save a prescription
    @PostMapping
    public ResponseEntity<Map<String, String>> savePrescription(
            @RequestBody @Valid Prescription prescription,
            @RequestHeader("Authorization") String token
    ) {
        // Validate token for doctor role
        ResponseEntity<Map<String, String>> validation = authService.validateToken(token, "doctor");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode())
                    .body(Map.of("message", validation.getBody().get("message")));
        }

        // Update appointment status to indicate prescription added
        appointmentService.changeStatus(prescription.getAppointmentId(), 1);

        // Save prescription
        return prescriptionService.savePrescription(prescription);
    }

    // 4. Get a prescription by appointment ID
    @GetMapping("/{appointmentId}")
    public ResponseEntity<Map<String, Object>> getPrescription(
            @PathVariable Long appointmentId,
            @RequestHeader("Authorization") String token
    ) {
        // Validate token for doctor role
        ResponseEntity<Map<String, String>> validation = authService.validateToken(token, "doctor");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode())
                    .body(Map.of("message", validation.getBody().get("message")));
        }

        // Fetch prescription
        return prescriptionService.getPrescription(appointmentId);
    }
}

