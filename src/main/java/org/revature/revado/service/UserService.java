package org.revature.revado.service;

import org.revature.revado.dto.UserLoginDTO;
import org.revature.revado.dto.UserRegisterDTO;
import org.revature.revado.dto.UserResponseDTO;
import org.revature.revado.entity.User;
import org.revature.revado.repository.UserRepository;
import org.revature.revado.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // for hashing + matching
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // 1. Logic for Register Button
    public UserResponseDTO registerUser(UserRegisterDTO registerDTO) {

        // this method should convert DTO to Entity

        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new RuntimeException("Username already taken!");
        }

        User newUser = new User();
        newUser.setId(java.util.UUID.randomUUID().toString());
        newUser.setUsername(registerDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerDTO.getPassword()));  // Hash password before saving
        newUser.setFirstname(registerDTO.getFirstname());
        newUser.setLastname(registerDTO.getLastname());

        User user = userRepository.save(newUser); // savedUser is an Entity

        //convertEntityToDto
        UserResponseDTO userResponseDTO = convertEntityToDto(user);

        return userResponseDTO;
    }

    // 2. Logic for Login Button
    public String loginUser(UserLoginDTO loginDTO) {
        // Find user by username
        Optional<User> foundUser = userRepository.findByUsername(loginDTO.getUsername());

        if (foundUser.isPresent()) {
            User user = foundUser.get();
            // Compare raw password with hashed password
            if (passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                // Return JWT token instead of text message
                return jwtUtil.generateToken(user.getUsername());
            }
        }

        // If user not found OR password wrong
        throw new RuntimeException("Invalid Login Credentials");
    }

    // Helper to clean up the data before sending it out
    private UserResponseDTO convertEntityToDto(User savedUser) {

        // this method convert entity to DTO
        UserResponseDTO userResponseDTO = new UserResponseDTO();

        userResponseDTO.setId(savedUser.getId());
        userResponseDTO.setFirstname(savedUser.getFirstname());
        userResponseDTO.setLastname(savedUser.getLastname());
        userResponseDTO.setUsername(savedUser.getUsername());
        return userResponseDTO;
    }
}