package com.example.QuizApplicationImplemented.dto.applicationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class  QuizGivenDto {
//    No Valid Usage for now
    private String questionTitle;
    private String rightAnswer;
    private String selectedAnswer;

}
