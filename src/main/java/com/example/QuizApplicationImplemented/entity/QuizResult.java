package com.example.QuizApplicationImplemented.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "quiz_results")
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id" , nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private Integer totalQuestions;

    @Column(nullable = false)
    private Integer correctAnswers;

    @Column(nullable = false)
    private Integer wrongAnswers;

    @Column(nullable = false)
    private Double scorePercentage;

    @ElementCollection
    @CollectionTable(name = "quiz_result_answers" , joinColumns = @JoinColumn(name = "quiz_result_id"))
    private List<Long> questionIds;

    @ElementCollection
    @CollectionTable(name = "quiz_result_answers" , joinColumns = @JoinColumn(name = "quiz_result_id"))
    private List<String> selectedAnswers;

    @Column(nullable = false)
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCrete(){
        completedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "QuizResult{" +
                "id=" + id +
                ", users=" + users +
                ", quiz=" + quiz +
                ", totalQuestions=" + totalQuestions +
                ", correctAnswers=" + correctAnswers +
                ", wrongAnswers=" + wrongAnswers +
                ", scorePercentage=" + scorePercentage +
                ", questionIds=" + questionIds +
                ", selectedAnswers=" + selectedAnswers +
                ", completedAt=" + completedAt +
                '}';
    }
}
