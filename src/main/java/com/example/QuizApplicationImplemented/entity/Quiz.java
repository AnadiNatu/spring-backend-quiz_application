package com.example.QuizApplicationImplemented.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String category;
    private String difficultyLevel;

    // QUIZ CREATOR
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users")
    private Users createdBy;

    @ManyToMany
    @JoinTable(name = "quiz_questions",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id"))
    private List<Questions> questions;

    // THE LIST OF PARTICIPANTS
    @ManyToMany
    @JoinTable(name = "quiz_users" ,
    joinColumns = @JoinColumn(name = "quiz_id")
    ,inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<Users> participants;

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", difficultyLevel='" + difficultyLevel + '\'' +
                '}';
    }
}
