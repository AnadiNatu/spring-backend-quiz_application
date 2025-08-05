package com.example.QuizApplicationImplemented.repository;

import com.example.QuizApplicationImplemented.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    @Query("SELECT q FROM Quiz q JOIN q.questions qs WHERE qs.category = :category")
    List<Quiz> findAllByCategory(@Param("category") String category);

    @Query("SELECT q FROM Quiz q WHERE LOWER(q.title) = LOWER(:title)")
    Optional<Quiz> findQuizByTitle(@Param("title") String title);

    @Query("SELECT q FROM Quiz q JOIN q.participants p WHERE LOWER(p.name) = LOWER(:name)")
    List<Quiz> findQuizzesByParticipantName( @Param("name")String name);

//    @Query("SELECT q FROM Quiz q JOIN q.createdBy u WHERE LOWER(u.name) = LOWER(:name)")
//    List<Quiz> findQuizzesByCreatorName(@Param("name")String name);

    @Query("SELECT q FROM Quiz q WHERE q.id = :quizId")
    Optional<Quiz> findQuizById(@Param("quizId") Long quizId);

    @Query("SELECT q FROM Quiz q JOIN q.createdBy u WHERE LOWER(u.username) = LOWER(:name)")
    List<Quiz> findAllQuizCreatedByUser(@Param("name")String name);

    @Query("SELECT q FROM Quiz q WHERE LOWER(q.category) = LOWER(:category) AND LOWER(q.difficultyLevel) = LOWER(:difficultyLevel)")
    List<Quiz> findAllQuizByCategoryAndDifficulty(@Param("category")String category , @Param("difficultyLevel")String difficultyLevel);
}
