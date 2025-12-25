package com.project.back_end.controllers;


import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.Service;
import com.project.back_end.services.DoctorService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;

    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    @GetMapping("/availability/{user}/{doctorId}/{date}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestHeader("Authorization") String token
    ) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, user);
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode())
                    .body(Map.of("message", validation.getBody().get("message")));
        }

        List<String> availability = doctorService.getDoctorAvailability(doctorId, date);
        return ResponseEntity.ok(Map.of("availability", availability));
    }

    // 4. Get all doctors
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors() {
        List<Doctor> doctors = doctorService.getDoctors();
        return ResponseEntity.ok(Map.of("doctors", doctors));
    }

    // 5. Save doctor (admin only)
    @PostMapping
    public ResponseEntity<Map<String, String>> saveDoctor(
            @RequestBody @Valid Doctor doctor,
            @RequestHeader("Authorization") String token
    ) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode())
                    .body(Map.of("message", validation.getBody().get("message")));
        }

        int result = doctorService.saveDoctor(doctor);
        if (result == -1) {
            return ResponseEntity.status(409).body(Map.of("message", "Doctor already exists"));
        } else if (result == 0) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to save doctor"));
        }
        return ResponseEntity.status(201).body(Map.of("message", "Doctor saved successfully"));
    }

    // 6. Doctor login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody @Valid Login login) {
        return doctorService.validateDoctor(login);
    }

    // 7. Update doctor (admin only)
    @PutMapping
    public ResponseEntity<Map<String, String>> updateDoctor(
            @RequestBody @Valid Doctor doctor,
            @RequestHeader("Authorization") String token
    ) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode())
                    .body(Map.of("message", validation.getBody().get("message")));
        }

        int result = doctorService.updateDoctor(doctor);
        if (result == -1) {
            return ResponseEntity.status(404).body(Map.of("message", "Doctor not found"));
        } else if (result == 0) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to update doctor"));
        }
        return ResponseEntity.ok(Map.of("message", "Doctor updated successfully"));
    }

    // 8. Delete doctor (admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable long id,
            @RequestHeader("Authorization") String token
    ) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (validation.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(validation.getStatusCode())
                    .body(Map.of("message", validation.getBody().get("message")));
        }

        int result = doctorService.deleteDoctor(id);
        if (result == -1) {
            return ResponseEntity.status(404).body(Map.of("message", "Doctor not found"));
        } else if (result == 0) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to delete doctor"));
        }
        return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully"));
    }

    // 9. Filter doctors
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filterDoctors(
            @PathVariable(required = false) String name,
            @PathVariable(required = false) String time,
            @PathVariable(required = false) String speciality
    ) {
        Map<String, Object> result = service.filterDoctor(name, speciality, time);
        return ResponseEntity.ok(result);
    }
}
