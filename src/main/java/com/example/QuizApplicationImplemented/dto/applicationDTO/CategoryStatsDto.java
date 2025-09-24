package com.example.QuizApplicationImplemented.dto.applicationDTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryStatsDto {

    private String category;
    private Integer totalQuestions;
    private Integer totalQuizzes;
    private Integer totalParticipants;
    private Double averageScore;

}
