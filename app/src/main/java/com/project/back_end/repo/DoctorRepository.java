package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Find doctor by email
    Doctor findByEmail(String email);


    // Find doctors where name contains (case-sensitive LIKE)
    List<Doctor> findByNameLike(String name);


    // Find doctors where name contains (ignore case) AND specialty matches (ignore case)
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(String name, String specialty);


    // Find doctors by specialty ignoring case
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
}
