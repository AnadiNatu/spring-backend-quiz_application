package com.example.QuizApplicationImplemented.dto.applicationDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionDto {
    // To display all the information about the questions
    private Long id;
    private String category;
    private String difficultyLevel;
    private String rightAnswer;
    private String questionTitle;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private List<Long> quizId;
}



