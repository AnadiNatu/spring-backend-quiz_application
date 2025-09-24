package com.example.QuizApplicationImplemented.dto.applicationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizDto {
// All details concerning the quiz details
    private Long id;
    private String title;
    private String category;
    private String difficultyLevel;
    private Long creatorUserId;
    private String creatorUserName;
    private Integer totalQuestions;
    private Double averageScore;
    private Double rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> questionIds;
    private List<String> participantUserNames;
    private Integer totalParticipant;

}
