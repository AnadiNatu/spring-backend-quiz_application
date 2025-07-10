package com.example.QuizApplicationImplemented.dto.applicationDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateQuizDto {
// This DTO is carrying the info for creating the quiz
private String quizTitle;
private String category;
private String difficultyLevel;
private int noOfQuestions;

}
