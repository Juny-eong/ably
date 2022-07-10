package com.ably.assignment.verification.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<Verification, Long> {
    Optional<Verification> findByPhoneNumberAndCreatedAtGreaterThan(Long phoneNumber, LocalDateTime time);
}
