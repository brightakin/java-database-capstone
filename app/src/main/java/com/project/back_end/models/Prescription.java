package com.project.back_end.models;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "prescriptions")
public class Prescription {
    @Id
    private String id;

    @Size(min = 3, max = 100, message = "Patient name must be between 3 and 100 character")
    private String patientName;

    @NotNull(message = "Appointment id cannot be null")
    private long appointmentId;

    @Size(min = 3, max = 100, message = "Medication name must be between 3 and 100 character")
    private String medication;

    @Size(min = 3, max = 100, message = "Dosage must be between 3 and 100 character")
    private String dosage;

    @Size(max = 200, message = "Notes must be a maximum of 200 characters")
    private String doctorNotes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }

}
