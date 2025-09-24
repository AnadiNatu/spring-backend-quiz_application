package com.example.QuizApplicationImplemented.dto.applicationDTO;

import com.example.QuizApplicationImplemented.enums.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {

    private Long id;
    private String name;
    private String username;
    private Integer age;
    private UserRoles userRoles;
    private Integer totalQuizCreated;
    private Integer totalQuizTaken;
    private Integer ranking;
    private Double overallPercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private byte[] profilePhoto;

}
