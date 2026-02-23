package org.revature.revado.controller;

import org.revature.revado.dto.UserLoginDTO;
import org.revature.revado.dto.UserRegisterDTO;
import org.revature.revado.dto.UserResponseDTO;
import org.revature.revado.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController{

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody UserRegisterDTO userRegisterDTO){

        System.out.println("firstname: " + userRegisterDTO.getFirstname());
        System.out.println("lastname: " + userRegisterDTO.getLastname());
        System.out.println("username: " + userRegisterDTO.getUsername());
        System.out.println("password: " + userRegisterDTO.getPassword());

        UserResponseDTO userResponseDTO = userService.registerUser(userRegisterDTO); // controllerSendDtoToService

        return userResponseDTO;

    }
    @PostMapping("/login")
    public String login(@RequestBody UserLoginDTO loginDTO){

        System.out.println("Login username: " + loginDTO.getUsername());
        System.out.println("Login password: " + loginDTO.getPassword());

        String  str = userService.loginUser(loginDTO);

        return str;
    }

}
