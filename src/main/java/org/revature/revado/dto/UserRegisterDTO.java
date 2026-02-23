package org.revature.revado.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private String firstname;
    private String lastname;
    private String username;
    private String password;
}
