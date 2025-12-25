package com.project.back_end.services;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey signingKey;

    public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @PostConstruct
    private void init() {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String identifier) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000L); // 7 days

        return Jwts.builder()
                .setSubject(identifier)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractIdentifier(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token, String userType) {
        try {
            String identifier = extractIdentifier(token);

            switch (userType.toLowerCase()) {
                case "admin":
                    Admin admin = adminRepository.findByUsername(identifier);
                    return admin != null;
                case "doctor":
                    Doctor doctor = doctorRepository.findByEmail(identifier);
                    return doctor != null;
                case "patient":
                    Patient patient = patientRepository.findByEmail(identifier);
                    return patient != null;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private SecretKey getSigningKey() {
        return this.signingKey;
    }

    public Long extractPatientId(String token) {
        String email = extractIdentifier(token);
        Patient patient = patientRepository.findByEmail(email);
        return patient != null ? patient.getId() : null;
    }

    public Long extractDoctorId(String token) {
        String email = extractIdentifier(token);
        Doctor doctor = doctorRepository.findByEmail(email);
        return doctor != null ? doctor.getId() : null;
    }
}

