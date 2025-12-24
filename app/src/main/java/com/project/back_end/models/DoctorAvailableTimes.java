package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class DoctorAvailableTimes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "Doctor cannot be null")
    @ManyToOne
    private Doctor doctor;

    @NotNull(message = "Available time cannot be null")
    private String available_times;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public String getAvailable_times() {
        return available_times;
    }

    public void setAvailable_times(String available_times) {
        this.available_times = available_times;
    }
}