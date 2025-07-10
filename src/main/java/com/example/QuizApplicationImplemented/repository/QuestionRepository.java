package com.example.QuizApplicationImplemented.repository;

import com.example.QuizApplicationImplemented.entity.Questions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Questions, Long> {

    @Query("SELECT q FROM Questions q WHERE LOWER(q.category) LIKE LOWER(:category)")
    List<Questions> findByCategory(@Param("category") String category);

    @Query("SELECT q FROM Questions q WHERE LOWER(q.questionTitle) = LOWER(:questionTitle)")
    Optional<Questions> findQuestionByQuestionTitle(@Param("questionTitle")String questionTitle);

    @Query("SELECT q FROM Questions q WHERE LOWER(q.difficultyLevel) = LOWER(:difficultyLevel)")
    List<Questions> findByDifficultyLevel(String difficultyLevel);

    @Query("SELECT q FROM Questions q WHERE LOWER(q.category) = LOWER(:category) AND LOWER(q.difficultyLevel) = LOWER(:difficultyLevel)")
    List<Questions> findByCategoryAndDifficultyLevel(
            @Param("category") String category,
            @Param("difficultyLevel") String difficultyLevel
    );

    @Query("SELECT COUNT(q) FROM Questions q WHERE LOWER(q.category) = LOWER(:category) AND LOWER(q.difficultyLevel) = LOWER(:difficultyLevel)")
    int countByCategoryAndDifficulty(
            @Param("category") String category,
            @Param("difficultyLevel") String difficultyLevel
    );

}
