package com.example.QuizApplicationImplemented.dto.authenticationDTO;

import com.example.QuizApplicationImplemented.enums.UserRoles;
import lombok.Data;

@Data
public class UsersDto {
    private Long id;
    private String name;
    private String username;
    private String password;
    private int age;
    private UserRoles userRoles;
}
