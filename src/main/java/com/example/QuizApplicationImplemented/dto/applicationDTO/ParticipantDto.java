package com.example.QuizApplicationImplemented.dto.applicationDTO;

import com.example.QuizApplicationImplemented.enums.UserRoles;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantDto {
    private Long id;
    private String name;
    private String username;
    private Integer age;
    private UserRoles userRoles;
    private Integer totalQuizTaken;
    private Double overallPercentage;
    private Integer ranking;
}


