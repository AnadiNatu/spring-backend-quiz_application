package com.example.QuizApplicationImplemented.dto.applicationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatedQuizDto {
// This DTO is used after the quiz is created by , ADMIN or CREATOR . And the quiz details is sent to frontend via this DTO
    private String title;
    private String category;
    private String difficultyLevel;
    private String creatorName;
    private Integer totalQuestions;
    private Double averageScore;
    private Double rating;
    private LocalDateTime createdAt;
    private List<QuestionDto> questionList;
}
