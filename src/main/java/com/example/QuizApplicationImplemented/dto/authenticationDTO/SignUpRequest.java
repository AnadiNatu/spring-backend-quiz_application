package com.example.QuizApplicationImplemented.dto.authenticationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    private String name;
    private String username;
    private String password;
    private int age;
    private int roleNumber;
}
