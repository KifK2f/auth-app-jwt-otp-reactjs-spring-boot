package com.friedo.authentication.service.serviceImpl;

import com.friedo.authentication.dto.LoginUserDto;
import com.friedo.authentication.dto.RegisterUserDto;
import com.friedo.authentication.dto.VerifyUserDto;
import com.friedo.authentication.model.User;
import com.friedo.authentication.repository.UserRepository;
import com.friedo.authentication.service.IAuthenticationService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
public class AuthenticationServiceImpl implements IAuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailServiceImpl emailService;

    public AuthenticationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailServiceImpl emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    @Override
    public User signup(RegisterUserDto input) {
        User user = new User(input.getUsername(), input.getEmail(), passwordEncoder.encode(input.getPassword()));
        user.setOtpVerificationCode(generateOtpVerificationCode());
        user.setOtpVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        sendVerificationEmail(user);
        return userRepository.save(user);
    }

    @Override
    public User authenticate(LoginUserDto input) {
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!user.isEnabled()){
            throw new RuntimeException("Compte non vérifié. Veuillez vérifier votre compte.");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return user;
    }

    @Override
    public void verifyUser(VerifyUserDto input) {
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            if (user.getOtpVerificationCodeExpiresAt().isBefore(LocalDateTime.now())){
                throw new RuntimeException("Le code de vérification a expiré");
            }
            if (user.getOtpVerificationCode().equals(input.getOtpVerificationCode())){
                user.setEnabled(true);
                user.setOtpVerificationCode(null);
                user.setOtpVerificationCodeExpiresAt(null);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Code de vérification otp invalide");
            }
        } else {
            throw new RuntimeException("Utilisateur non trouvé");
        }
    }

    @Override
    public void resendOtpVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            if (user.isEnabled()){
                throw new RuntimeException("Le compte est déjà vérifié");
            }
            user.setOtpVerificationCode(generateOtpVerificationCode());
            user.setOtpVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Utilisateur non trouvé");
        }
    }

    @Override
    public void sendVerificationEmail(User user) {
        String logoUrl = "https://cdn-icons-png.flaticon.com/128/12894/12894030.png";
        String subject = "Vérification de compte";
        String otpVerificationCode = "CODE DE VÉRIFICATION OTP " + user.getOtpVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family:Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                //Logo
                + "<div style=\"text-align: center; margin-bottom: 20px;\">"
                + "<img src=\"" + logoUrl + "\" alt=\"Logo d'OssanMarketplace\" style=\"max-width: 150px;\">"
                + "</div>"

                + "<h2 style=\"color: #333;\">Bienvenue sur notre application !</h2>"
                + "<p style=\"font-size: 16px;\">Pour continuer, veuillez saisir le code de vérification OTP ci-dessous :</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Code de vérification OTP:</h3>"
                + "<p style=\"font-size:18px; font-weight:bold; color:#007bff;\">" + otpVerificationCode + "</p>"

                // Footer
                + "<p style=\"font-size: 14px; color: #999999; text-align: center;\">"
                + "Si vous n'avez pas demandé ce code OTP, veuillez ignorer cet e-mail."
                + "</p>"

                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
        try{
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
            System.out.println("Email de vérification envoyé à "+user.getEmail());
        } catch (MessagingException e){
//            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi de l'e-mail de vérification à " + user.getEmail());
            log.error("Erreur lors de l'envoi de l'e-mail de vérification à " + user.getEmail(), e);
        }
    }

    private String generateOtpVerificationCode(){
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

}
