package com.example.QuizApplicationImplemented.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_stats")
public class UserStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false)
    private Integer totalQuizzesTaken;

    @Column(nullable = false)
    private Integer totalCorrectAnswers;

    @Column(nullable = false)
    private Integer totalWrongAnswers;

    @Column(nullable = false)
    private Double averageScore;

    @Column(nullable = false)
    private Double categoryPercentage;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    protected void onCreateOrUpdate() {
        lastUpdated = LocalDateTime.now();
        if (totalQuizzesTaken == null) totalQuizzesTaken = 0;
        if (totalCorrectAnswers == null) totalCorrectAnswers = 0;
        if (totalWrongAnswers == null) totalWrongAnswers = 0;
        if (averageScore == null) averageScore = 0.0;
        if (categoryPercentage == null) categoryPercentage = 0.0;
    }

    @Override
    public String toString() {
        return "UserStats{" +
                "id=" + id +
                ", user=" + user +
                ", category='" + category + '\'' +
                ", totalQuizzesTaken=" + totalQuizzesTaken +
                ", totalCorrectAnswers=" + totalCorrectAnswers +
                ", totalWrongAnswers=" + totalWrongAnswers +
                ", averageScore=" + averageScore +
                ", categoryPercentage=" + categoryPercentage +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
