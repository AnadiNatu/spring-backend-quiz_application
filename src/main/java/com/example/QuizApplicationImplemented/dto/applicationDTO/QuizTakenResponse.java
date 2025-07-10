package com.example.QuizApplicationImplemented.dto.applicationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizTakenResponse {
    private String quizTitle;
    private String userName;
    private List<ResponseDto> responseList;
}


