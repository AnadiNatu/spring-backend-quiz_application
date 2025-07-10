package com.example.QuizApplicationImplemented.dto.applicationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizDto {
// All details concerning the quiz details
    private String title;
    private String category;
    private String difficultyLevel;
    private Long creatorUserId;
    private String creatorUserName;
    private List<Long> questionsQuizIds;
    private List<String> participantUserName;

}
