package org.revature.revado.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.revature.revado.dto.UserLoginDTO;
import org.revature.revado.dto.UserRegisterDTO;
import org.revature.revado.dto.UserResponseDTO;
import org.revature.revado.entity.User;
import org.revature.revado.repository.UserRepository;
import org.revature.revado.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    // Fake repository used only for testing.
    @Mock
    private UserRepository userRepository;

    // Fake password encoder so we control hash/match behavior.
    @Mock
    private PasswordEncoder passwordEncoder;

    // Fake JWT utility so no real token logic runs in the unit test.
    @Mock
    private JwtUtil jwtUtil;

    // Real service object with the fake dependencies injected into it.
    @InjectMocks
    private UserService userService;

    // Tests registering a new user and returning the saved user details.
    @Test
    void registerUser_shouldSaveUserAndReturnResponseDto() {
        // Arrange: build input data for registration.
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setFirstname("Vishruti");
        registerDTO.setLastname("Patel");
        registerDTO.setUsername("vishruti");
        registerDTO.setPassword("pass123");

        // Arrange: create the user object we expect the repository to return.
        User savedUser = new User();
        savedUser.setId("user-1");
        savedUser.setFirstname("Vishruti");
        savedUser.setLastname("Patel");
        savedUser.setUsername("vishruti");
        savedUser.setPassword("hashedPassword");

        // Arrange: define how mocked dependencies should behave.
        when(userRepository.existsByUsername("vishruti")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act: call the real service method.
        UserResponseDTO result = userService.registerUser(registerDTO);

        // Assert: check the returned DTO values.
        assertNotNull(result);
        assertEquals("user-1", result.getId());
        assertEquals("Vishruti", result.getFirstname());
        assertEquals("Patel", result.getLastname());
        assertEquals("vishruti", result.getUsername());

        // Verify: confirm expected dependency calls happened.
        verify(userRepository).existsByUsername("vishruti");
        verify(passwordEncoder).encode("pass123");
        verify(userRepository).save(any(User.class));
    }

    // Tests rejecting registration when the username already exists.
    @Test
    void registerUser_shouldThrowExceptionWhenUsernameAlreadyExists() {
        // Arrange: create input with a username that already exists.
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setUsername("vishruti");

        when(userRepository.existsByUsername("vishruti")).thenReturn(true);

        // Act + Assert: service should throw when username is taken.
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.registerUser(registerDTO)
        );

        assertEquals("Username already taken!", exception.getMessage());
        verify(userRepository).existsByUsername("vishruti");
        verify(userRepository, never()).save(any(User.class));
    }

    // Tests logging in successfully and returning a JWT token for valid credentials.
    @Test
    void loginUser_shouldReturnTokenWhenCredentialsAreValid() {
        // Arrange: create login request.
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setUsername("vishruti");
        loginDTO.setPassword("pass123");

        // Arrange: create a stored user from the repository.
        User user = new User();
        user.setUsername("vishruti");
        user.setPassword("hashedPassword");

        // Arrange: define successful login behavior.
        when(userRepository.findByUsername("vishruti")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass123", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("vishruti")).thenReturn("jwt-token");

        // Act: call the real login method.
        String result = userService.loginUser(loginDTO);

        // Assert: valid credentials should return a token.
        assertEquals("jwt-token", result);
        verify(userRepository).findByUsername("vishruti");
        verify(passwordEncoder).matches("pass123", "hashedPassword");
        verify(jwtUtil).generateToken("vishruti");
    }

    // Tests rejecting login when the provided password does not match the stored password.
    @Test
    void loginUser_shouldThrowExceptionWhenPasswordIsWrong() {
        // Arrange: create login input with a wrong password.
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setUsername("vishruti");
        loginDTO.setPassword("wrongpass");

        // Arrange: repository still finds the user.
        User user = new User();
        user.setUsername("vishruti");
        user.setPassword("hashedPassword");

        when(userRepository.findByUsername("vishruti")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "hashedPassword")).thenReturn(false);

        // Act + Assert: wrong password should throw an exception.
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.loginUser(loginDTO)
        );

        assertEquals("Invalid Login Credentials", exception.getMessage());
        // Verify: token should never be created for invalid login.
        verify(jwtUtil, never()).generateToken(anyString());
    }
}
