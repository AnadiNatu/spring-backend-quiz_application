package com.example.QuizApplicationImplemented.entity;

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
    @JoinColumn(name = "created_by_user_id" , nullable = false)
    private Users createdBy;

    @ManyToMany(cascade = {CascadeType.MERGE , CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinTable(name = "quiz_questions",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id"))
    private List<Questions> questions = new ArrayList<>();

    // THE LIST OF PARTICIPANTS
    @ManyToMany(fetch = FetchType.LAZY , cascade = {CascadeType.MERGE , CascadeType.PERSIST})
    @JoinTable(name = "quiz_users" ,
            joinColumns = @JoinColumn(name = "quiz_id")
            ,inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<Users> participants = new ArrayList<>();

    public void addQuestion(Questions question){
        if (!questions.contains(question)) {
            questions.add(question);
        }
        if (!question.getQuizzes().contains(this)) {
            question.getQuizzes().add(this);
        }
    }

    public void addParticipant(Users user) {
        if (!participants.contains(user)) {
            participants.add(user);
        }
    }

    public void addQuestions(List<Questions> questions){
        for (Questions q : questions){
            addQuestion(q);
        }
    }
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


