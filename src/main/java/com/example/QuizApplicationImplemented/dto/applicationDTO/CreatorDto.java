package com.example.QuizApplicationImplemented.dto.applicationDTO;

import com.example.QuizApplicationImplemented.enums.UserRoles;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatorDto {

    private Long id;
    private String name;
    private String username;
    private Integer age;
    private UserRoles userRoles;
    private Integer totalQuizCreated;
    private Double averageQuizRating;

}
