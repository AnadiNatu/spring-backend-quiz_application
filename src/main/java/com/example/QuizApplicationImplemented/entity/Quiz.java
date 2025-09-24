package com.example.QuizApplicationImplemented.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    @Column(nullable = false , length = 200)
    private String title;

    @Column(nullable = false , length = 100)
    private String category;

    @Column(nullable = false , length = 50)
    private String difficultyLevel;

    @Column(nullable = false)
    private Integer totalQuestions;

    @Column(nullable = false)
    private Double averageScore;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;


    // QUIZ CREATOR
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id" , nullable = false)
    private Users createdBy;

    @ManyToMany(cascade = {CascadeType.MERGE , CascadeType.PERSIST} , fetch = FetchType.LAZY)
    @JoinTable(name = "quiz_questions",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id"))
    private List<Questions> questions = new ArrayList<>();

    // THE LIST OF PARTICIPANTS
    @ManyToMany(cascade = {CascadeType.MERGE , CascadeType.PERSIST} , fetch = FetchType.LAZY)
    @JoinTable(name = "quiz_participants" ,
    joinColumns = @JoinColumn(name = "quiz_id")
    ,inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<Users> participants = new ArrayList<>();

    @OneToMany(mappedBy = "quiz" , fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    private List<QuizResult> results = new ArrayList<>();

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (averageScore == null) averageScore = 0.0;
        if (rating == null) rating = 0.0;
        if (totalQuestions == null) totalQuestions = 0;
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }

    public void addQuestion(Questions question){
        if (!questions.contains(question)){
            questions.add(question);
        }

        if (!question.getQuizzes().contains(this)){
            question.getQuizzes().add(this);
        }
    }

    public void addParticipant(Users user){
        if (!participants.contains(user)){
            participants.add(user);
        }

        if (!user.getQuizzesTaken().contains(this)){
            user.getQuizzesTaken().add(this);
        }
    }

    public void addQuestions(List<Questions> questions){
        for (Questions q : questions){
            addQuestion(q);
        }
    }

    public void addParticipants(List<Users> users){
        for (Users u : users){
            addParticipant(u);
        }
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", difficultyLevel='" + difficultyLevel + '\'' +
                ", totalQuestions=" + totalQuestions + '\'' +
                ", averageScore=" + averageScore + '\'' +
                ", rating=" + rating +
                '}';
    }
}
