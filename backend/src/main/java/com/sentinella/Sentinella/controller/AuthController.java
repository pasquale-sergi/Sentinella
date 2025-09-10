package com.sentinella.Sentinella.controller;

import com.sentinella.Sentinella.auth.JwtResponse;
import com.sentinella.Sentinella.auth.JwtUtils;
import com.sentinella.Sentinella.auth.LoginRequest;
import com.sentinella.Sentinella.auth.SignupRequest;
import com.sentinella.Sentinella.entity.UserApplication;
import com.sentinella.Sentinella.repository.UserRepository;
import com.sentinella.Sentinella.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        System.out.println("Received a login request with data: " + loginRequest.getUsername() + " " + loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.generateToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(new JwtResponse(token, userDetails.getId(),userDetails.getUsername(),userDetails.getEmail()));

    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){
        if(userRepository.existsByUsername((signupRequest.getUsername()))){
            return ResponseEntity.badRequest().body("Username is already in use");
        }
        if(userRepository.existsByEmail((signupRequest.getEmail()))){
            return ResponseEntity.badRequest().body("Email is already in use");
        }
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        UserApplication user = new UserApplication();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(encodedPassword);
        user.setEmail(signupRequest.getEmail());
        System.out.println("DEBUG: Signup User: " + signupRequest.getUsername() + ", Raw Pass: " + signupRequest.getPassword() + ", Encoded Pass: " + encodedPassword);
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

}