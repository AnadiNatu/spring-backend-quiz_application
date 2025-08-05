package com.example.QuizApplicationImplemented.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "questions")
public class Questions {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionTitle;
    private String category;
    private String difficultyLevel;
    private String rightAnswer;
    private String option1;
    private String option2;
    private String option3;
    private String option4;

    @ManyToMany(mappedBy = "questions" , fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Quiz> quizzes = new ArrayList<>();

    @Override
    public String toString() {
        return "Questions{" +
                "id=" + id +
                ", questionTitle='" + questionTitle + '\'' +
                ", category='" + category + '\'' +
                ", difficultyLevel='" + difficultyLevel + '\'' +
                ", rightAnswer='" + rightAnswer + '\'' +
                ", option1='" + option1 + '\'' +
                ", option2='" + option2 + '\'' +
                ", option3='" + option3 + '\'' +
                ", option4='" + option4 + '\'' +
                '}';
    }
}
