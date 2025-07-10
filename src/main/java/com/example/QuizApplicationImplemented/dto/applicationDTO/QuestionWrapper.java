package com.example.QuizApplicationImplemented.dto.applicationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionWrapper {
//  DTOs used while a participant is giving the test
    private String questionTitle;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
}
