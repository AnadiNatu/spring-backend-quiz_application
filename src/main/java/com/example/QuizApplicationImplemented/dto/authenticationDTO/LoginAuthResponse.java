package com.example.QuizApplicationImplemented.dto.authenticationDTO;


import com.example.QuizApplicationImplemented.enums.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginAuthResponse {

    private Long id;
    private String jwt;
    private UserRoles userRoles;

}
