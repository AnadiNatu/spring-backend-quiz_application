package com.example.QuizApplicationImplemented.dto.authenticationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginAuthRequest {

    private String username ;
    private String password ;

}
