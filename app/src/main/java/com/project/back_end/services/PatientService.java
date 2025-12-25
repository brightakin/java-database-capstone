package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {

        Map<String, Object> response = new HashMap<>();

        String email = tokenService.extractIdentifier(token);

        Patient patient = patientRepository.findByEmail(email);

        if (patient == null || patient.getId() == id) {
            response.put("message", "Unauthorized request");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        List<Appointment> appointments = appointmentRepository.findByPatientId(id);

        List<AppointmentDTO> appointmentDTOs =
                appointments.stream().map(this::convertToDTO).collect(Collectors.toList());

        response.put("appointments", appointmentDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        List<Appointment> appointments;
        LocalDateTime now = LocalDateTime.now();

        if ("past".equalsIgnoreCase(condition)) {
            appointments = appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(patientId, 1);
        } else if ("future".equalsIgnoreCase(condition)) {
            appointments = appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(patientId, 0);
        } else {
            response.put("message", "Invalid condition: use 'past' or 'future'");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("appointments",
                appointments.stream().map(this::convertToDTO).collect(Collectors.toList()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();

        List<Appointment> appointments = appointmentRepository
                .filterByDoctorNameAndPatientId(name, patientId);

        response.put("appointments",
                appointments.stream().map(this::convertToDTO).collect(Collectors.toList()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition,
                                                                          String name,
                                                                          long patientId) {
        Map<String, Object> response = new HashMap<>();
        List<Appointment> appointments;

        if ("past".equalsIgnoreCase(condition)) {
            appointments = appointmentRepository
                    .filterByDoctorNameAndPatientIdAndStatus(name, patientId, 1);
        } else if ("future".equalsIgnoreCase(condition)) {
            appointments = appointmentRepository
                    .filterByDoctorNameAndPatientIdAndStatus(name, patientId, 0);
        } else {
            response.put("message", "Invalid condition: use 'past' or 'future'");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("appointments",
                appointments.stream().map(this::convertToDTO).collect(Collectors.toList()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();
        Long id = tokenService.extractPatientId(token);

        Optional<Patient> patient = patientRepository.findById(id);
        if (patient == null) {
            response.put("message", "Patient not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.put("patient", patient);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private AppointmentDTO convertToDTO(Appointment appointment) {
        return new AppointmentDTO(
                appointment.getId(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getName(),
                appointment.getPatient().getId(),
                appointment.getPatient().getName(),
                appointment.getPatient().getEmail(),
                appointment.getPatient().getPhone(),
                appointment.getPatient().getAddress(),
                appointment.getAppointmentTime(),
                appointment.getStatus()
        );
    }
}

