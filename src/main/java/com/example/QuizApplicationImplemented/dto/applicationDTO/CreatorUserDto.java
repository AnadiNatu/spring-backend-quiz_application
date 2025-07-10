package com.example.QuizApplicationImplemented.dto.applicationDTO;

import com.example.QuizApplicationImplemented.enums.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatorUserDto {
//    THis DTO is will be used to get a List of Questions of a Quiz Created by the
    private String creatorName;
    private UserRoles userRoles;
    private List<String> questionTitleList;

}
