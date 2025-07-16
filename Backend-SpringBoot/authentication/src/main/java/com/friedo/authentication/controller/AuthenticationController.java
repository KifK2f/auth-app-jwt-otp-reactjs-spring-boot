package com.friedo.authentication.controller;

import com.friedo.authentication.dto.LoginUserDto;
import com.friedo.authentication.dto.RegisterUserDto;
import com.friedo.authentication.dto.VerifyUserDto;
import com.friedo.authentication.model.User;
import com.friedo.authentication.response.LoginResponse;
import com.friedo.authentication.service.serviceImpl.AuthenticationServiceImpl;
import com.friedo.authentication.service.serviceImpl.JwtServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtServiceImpl jwtService;
    private final AuthenticationServiceImpl authenticationService;

    public AuthenticationController(JwtServiceImpl jwtService, AuthenticationServiceImpl authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto){
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto){
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto){
        try{
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Compte vérifié avec succès");
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerifiedCode(@RequestParam String email){
        try{
            authenticationService.resendOtpVerificationCode(email);
            return ResponseEntity.ok("Code de vérification OTP envoyé");
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
