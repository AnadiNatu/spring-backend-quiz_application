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
@Table(name = "responses")
public class Responses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ElementCollection
    private List<Long> questionId;

    @ElementCollection
    private List<String> selectedAnswer;

    @Override
    public String toString() {
        return "Responses{" +
                "id=" + id +
                ", user=" + user +
                ", quiz=" + quiz +
                ", questionId=" + questionId +
                ", selectedAnswer=" + selectedAnswer +
                '}';
    }
}
