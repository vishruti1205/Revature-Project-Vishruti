package org.revature.revado.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserResponseDTO {
    private String id;
    private String firstname;
    private String lastname;
    private String username;
}
