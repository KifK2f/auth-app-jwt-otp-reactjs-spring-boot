package com.friedo.authentication.service;

import com.friedo.authentication.dto.LoginUserDto;
import com.friedo.authentication.dto.RegisterUserDto;
import com.friedo.authentication.dto.VerifyUserDto;
import com.friedo.authentication.model.User;

public interface IAuthenticationService {
    User signup(RegisterUserDto input);

    User authenticate(LoginUserDto input);

    void verifyUser(VerifyUserDto input);

    void resendOtpVerificationCode(String email);

    void sendVerificationEmail(User user);
}
