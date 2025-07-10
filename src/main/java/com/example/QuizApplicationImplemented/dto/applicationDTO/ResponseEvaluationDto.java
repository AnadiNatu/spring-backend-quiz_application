package com.example.QuizApplicationImplemented.dto.applicationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseEvaluationDto {
// THis DTO can be used as a part of a list that is being sent to the front end if the quiz taker needs to evaluate the quiz answer tha he/she has given
    private String questionTitle;
    private String correctAnswer;
    private String participantAnswer;
}
