package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.AuthService;
import com.project.back_end.services.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/appointments") // Base path for all appointment-related endpoints
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AuthService authService;
    private final TokenService tokenService;
            
    public AppointmentController(AppointmentService appointmentService,
                                 AuthService authService,
                                 TokenService tokenService) {
        this.appointmentService = appointmentService;
        this.authService = authService;
        this.tokenService = tokenService;
    }

    // ------------------ 2️⃣ Get Appointments ------------------
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAppointments(
            @RequestParam(required = false) String pname,
            @RequestParam String date,
            @RequestHeader("Authorization") String token
    ) {
        // Validate token for doctor role
        ResponseEntity<Map<String, String>> validation = authService.validateToken(token, "doctor");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode()).body(Map.of("message", validation.getBody().get("message")));
        }

        LocalDate appointmentDate = LocalDate.parse(date);
        Map<String, Object> appointments = appointmentService.getAppointment(pname, appointmentDate, token);
        return ResponseEntity.ok(appointments);
    }

    // ------------------ 3️⃣ Book Appointment ------------------
    @PostMapping("/book")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @RequestBody Appointment appointment,
            @RequestHeader("Authorization") String token
    ) {
        // Validate token for patient role
        ResponseEntity<Map<String, String>> validation = authService.validateToken(token, "patient");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode()).body(Map.of("message", validation.getBody().get("message")));
        }

        int result = appointmentService.bookAppointment(appointment);
        if (result == 1) {
            return ResponseEntity.status(201).body(Map.of("message", "Appointment booked successfully"));
        } else {
            return ResponseEntity.status(400).body(Map.of("message", "Failed to book appointment"));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> updateAppointment(
            @RequestBody Appointment appointment,
            @RequestHeader("Authorization") String token
    ) {
        // Validate token for patient role
        ResponseEntity<Map<String, String>> validation = authService.validateToken(token, "patient");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode()).body(Map.of("message", validation.getBody().get("message")));
        }

        return appointmentService.updateAppointment(appointment.getId(), appointment, appointment.getPatient().getId());
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable long id,
            @RequestHeader("Authorization") String token
    ) {
        // Validate token for patient role
        ResponseEntity<Map<String, String>> validation = authService.validateToken(token, "patient");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode())
                    .body(Map.of("message", validation.getBody().get("message")));
        }

        // Extract patient ID from token
        Long patientId = tokenService.extractPatientId(token);

        // Call service with appointment ID and patient ID
        return appointmentService.cancelAppointment(id, patientId);
    }

}