package com.example.QuizApplicationImplemented.dto.applicationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateQuestionDto {
// This DTO is used for creating a Question to be Added
    private String category;
    private String difficultyLevel;
    private String rightAnswer;
    private String questionTitle;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
}
