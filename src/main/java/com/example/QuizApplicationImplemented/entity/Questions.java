package com.example.QuizApplicationImplemented.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false , length = 1000)
    private String questionTitle;

    @Column(nullable = false , length = 1000)
    private String category;

    @Column(nullable = false , length = 50)
    private String difficultyLevel;

    @Column(nullable = false , length = 500)
    private String rightAnswer;

    @Column(nullable = false , length = 500)
    private String option1;

    @Column(nullable = false , length = 500)
    private String option2;

    @Column(nullable = false , length = 500)
    private String option3;

    @Column(nullable = false , length = 500)
    private String option4;

    @ManyToMany(mappedBy = "questions" , fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Quiz> quizzes;

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
