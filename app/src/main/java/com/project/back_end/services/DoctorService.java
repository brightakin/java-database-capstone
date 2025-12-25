package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService
    ) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {

        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) return Collections.emptyList();

        Doctor doctor = doctorOpt.get();

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<Appointment> bookedAppointments =
                appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                        doctorId, start, end
                );

        List<String> bookedTimes = bookedAppointments.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString())
                .toList();

        List<String> available = new ArrayList<>(doctor.getAvailableTimes());
        available.removeIf(slot -> bookedTimes.stream().anyMatch(slot::contains));

        return available;
    }

    public int saveDoctor(Doctor doctor) {
        try {
            Doctor existing = doctorRepository.findByEmail(doctor.getEmail());
            if (existing != null) return -1;

            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public int updateDoctor(Doctor doctor) {
        try {
            Optional<Doctor> existing = doctorRepository.findById(doctor.getId());
            if (existing.isEmpty()) return -1;

            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    public int deleteDoctor(long id) {
        try {
            Optional<Doctor> doctor = doctorRepository.findById(id);
            if (doctor.isEmpty()) return -1;

            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {

        Map<String, String> response = new HashMap<>();

        Doctor doctor = doctorRepository.findByEmail(login.getEmail());

        if (doctor == null || !doctor.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid email or password");
            return ResponseEntity.badRequest().body(response);
        }

        String token = tokenService.generateToken(login.getEmail());
        response.put("token", token);
        response.put("message", "Login successful");

        return ResponseEntity.ok(response);
    }

    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike("%" + name + "%");
        result.put("doctors", doctors);
        return result;
    }

    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        List<Doctor> doctors =
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);

        doctors = filterDoctorByTime(doctors, amOrPm);

        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        return result;
    }

    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameLike("%" + name + "%");

        doctors = filterDoctorByTime(doctors, amOrPm);

        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        return result;
    }

    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        Map<String, Object> result = new HashMap<>();

        List<Doctor> doctors =
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);

        result.put("doctors", doctors);
        return result;
    }

    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);

        doctors = filterDoctorByTime(doctors, amOrPm);

        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        return result;
    }

    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        Map<String, Object> result = new HashMap<>();

        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);

        result.put("doctors", doctors);
        return result;
    }

    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        List<Doctor> doctors = doctorRepository.findAll();

        doctors = filterDoctorByTime(doctors, amOrPm);

        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        return result;
    }

    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        if (amOrPm == null) return doctors;

        String keyword = amOrPm.equalsIgnoreCase("AM") ? "AM" : "PM";

        return doctors.stream()
                .filter(d -> d.getAvailableTimes() != null &&
                        d.getAvailableTimes().stream().anyMatch(t -> t.contains(keyword)))
                .toList();
    }
}

