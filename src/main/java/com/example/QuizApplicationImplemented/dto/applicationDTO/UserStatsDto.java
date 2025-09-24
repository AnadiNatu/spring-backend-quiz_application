package com.example.QuizApplicationImplemented.dto.applicationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsDto {

    private Long id;
    private Long userId;
    private String username;
    private String category;
    private Integer totalQuizzesTaken;
    private Integer totalCorrectAnswers;
    private Integer totalWrongAnswers;
    private Double averageScore;
    private Double categoryPercentage;
    private LocalDateTime lastUpdated;

}


