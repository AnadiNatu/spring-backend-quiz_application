package com.example.QuizApplicationImplemented.dto.applicationDTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizStatsDto {

    private Long quizId;
    private String title;
    private String category;
    private String difficultyLevel;
    private Integer totalQuestions;
    private Integer totalParticipants;
    private Double averageScore;
    private Double rating;
    private String creatorName;

}
