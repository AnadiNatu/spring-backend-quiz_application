package com.example.QuizApplicationImplemented.dto.applicationDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDto {
// This marks the question and answer the quiz taker has selected
    private String questionTitle;
    private String selectedAnswer;
}
