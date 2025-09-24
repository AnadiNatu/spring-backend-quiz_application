package com.example.QuizApplicationImplemented.dto.applicationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizResultDto {

    private Long id;
    private Long userId;
    private String username;
    private Long quizId;
    private String quizTitle;
    private Integer totalQuestions;
    private Integer correctAnswer;
    private Integer wrongAnswer;
    private Double scorePercentage;
    private LocalDateTime completeAt;
    private List<Long> questionIds;
    private List<String> selectedAnswers;

}
