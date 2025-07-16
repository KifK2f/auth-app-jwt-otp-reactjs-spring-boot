package com.friedo.authentication.repository;

import com.friedo.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//    Spring Data JPA utilise Optional pour Éviter les ambiguïtés entre "non trouvé" (Optional.empty()) et "erreur SQL".

    Optional<User> findByEmail(String email);
    Optional<User> findByOtpVerificationCode(String otpVerificationCode);
}
