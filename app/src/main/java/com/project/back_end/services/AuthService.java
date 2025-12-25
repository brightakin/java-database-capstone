package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.*;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public AuthService(TokenService tokenService,
                       AdminRepository adminRepository,
                       DoctorRepository doctorRepository,
                       PatientRepository patientRepository,
                       DoctorService doctorService,
                       PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        if (!tokenService.validateToken(token, user)) {
            response.put("message", "Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        response.put("message", "Token is valid");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());

        if (admin == null || !admin.getPassword().equals(receivedAdmin.getPassword())) {
            response.put("message", "Invalid credentials");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String token = tokenService.generateToken(admin.getUsername());
        response.put("token", token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
    }

    public  int validateAppointment(Appointment appointment) {
        Long doctorId = appointment.getDoctor().getId();
        if (!doctorRepository.existsById(doctorId)) {
            return -1; // Doctor doesn't exist
        }

        List<String> availableSlots = doctorService.getDoctorAvailability(doctorId, appointment.getAppointmentTime().toLocalDate());
        String requestedTime = appointment.getAppointmentTime().toLocalTime().toString();

        if (availableSlots.contains(requestedTime)) {
            return 1; // Valid
        } else {
            return 0; // Time unavailable
        }
    }

    public boolean validatePatient(Patient patient) {
        return patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone()) == null;
    }

    // ------------------ 6️⃣ Validate Patient Login ------------------
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        Patient patient = patientRepository.findByEmail(login.getEmail());

        if (patient == null || !patient.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid credentials");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String token = tokenService.generateToken(patient.getEmail());
        response.put("token", token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        if (condition != null && !condition.isBlank() && name != null && !name.isBlank()) {
            return patientService.filterByDoctorAndCondition(condition, name, tokenService.extractPatientId(token));
        } else if (condition != null && !condition.isBlank()) {
            return patientService.filterByCondition(condition, tokenService.extractPatientId(token));
        } else if (name != null && !name.isBlank()) {
            return patientService.filterByDoctor(name, tokenService.extractPatientId(token));
        } else {
            // If no filter provided, return all appointments
            return patientService.getPatientAppointment(tokenService.extractPatientId(token), token);
        }
    }
}

