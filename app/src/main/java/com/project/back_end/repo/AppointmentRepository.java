package com.project.back_end.repo;

import com.project.back_end.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Get appointments for a doctor within time range
    @Query("""
           SELECT a FROM Appointment a
           LEFT JOIN FETCH a.doctor d
           LEFT JOIN FETCH d.availableTimes
           WHERE d.id = :doctorId
           AND a.appointmentTime BETWEEN :start AND :end
           """)
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(Long doctorId,
                                                              LocalDateTime start,
                                                              LocalDateTime end);

    @Query("""
           SELECT a FROM Appointment a
           LEFT JOIN FETCH a.doctor d
           LEFT JOIN FETCH a.patient p
           WHERE d.id = :doctorId
           AND LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%'))
           AND a.appointmentTime BETWEEN :start AND :end
           """)
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            Long doctorId,
            String patientName,
            LocalDateTime start,
            LocalDateTime end
    );


    // Delete all appointments for a doctor
    @Modifying
    @Transactional
    void deleteAllByDoctorId(Long doctorId);


    // Get appointments by patient id
    List<Appointment> findByPatientId(Long patientId);


    // Get appointments by patient + status ordered by time
    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);


    // Filter by doctor name + patient id
    @Query("""
           SELECT a FROM Appointment a
           JOIN a.doctor d
           WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%'))
           AND a.patient.id = :patientId
           """)
    List<Appointment> filterByDoctorNameAndPatientId(String doctorName, Long patientId);


    // Filter by doctor name + patient + status
    @Query("""
           SELECT a FROM Appointment a
           JOIN a.doctor d
           WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%'))
           AND a.patient.id = :patientId
           AND a.status = :status
           """)
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(String doctorName,
                                                              Long patientId,
                                                              int status);


    // Update appointment status
    @Modifying
    @Transactional
    @Query("UPDATE Appointment a SET a.status = :status WHERE a.id = :id")
    void updateStatus(int status, long id);
}

