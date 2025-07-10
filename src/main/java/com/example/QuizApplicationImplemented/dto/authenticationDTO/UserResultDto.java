package com.example.QuizApplicationImplemented.dto.authenticationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResultDto {

    private Long userId;
    private String username;
    private int totalQuestions;
    private int correctAnswer;
    private double percentage;

}
