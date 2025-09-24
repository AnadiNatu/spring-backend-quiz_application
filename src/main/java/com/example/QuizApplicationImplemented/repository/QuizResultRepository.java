package com.example.QuizApplicationImplemented.repository;

import com.example.QuizApplicationImplemented.entity.Quiz;
import com.example.QuizApplicationImplemented.entity.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult , Long> {

    @Query("SELECT qr FROM QuizResult qr WHERE qr.user.id = :userId")
    List<QuizResult> findByUserId(@Param("userId") Long userId);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.quiz.id = :quizId")
    List<QuizResult> findByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.user.id = :userId AND qr.quiz.id = :quizId")
    Optional<QuizResult> findByUserIdAndQuizId(@Param("userId") Long userId , @Param("quiId") Long quizId);

    @Query("SELECT qr FROM QuizResult qr WHERE LOWER(qr.user.username) = LOWER(:username)")
List<QuizResult> findByUsername(@Param("username")String username);

    @Query("SELECT qr FROM QuizResult qr WHERE LOWER(qr.quiz.title) = LOWER(:quizTitle)")
    List<QuizResult> findByQuizTitle(@Param("quizTitle")String quizTitle);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.user.id = :userId ORDER BY qr.completedAt DESC")
    List<QuizResult> findByUserIdOrderByCompletedAtDesc(@Param("userId")Long userId);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.quiz.id = :quizId ORDER BY qr.scorePercentage DESC")
    List<QuizResult> findByQuizIdOrderByScoreDesc(@Param("quizId") Long quizId);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.scorePercentage >= :minPercentage")
    List<QuizResult> findByScorePercentageGreaterThanEqual(@Param("scorePercentage")Double scorePercentage);

    @Query("SELECT AVERAGE(qr.scorePercentage) FROM QuizResult qr WHERE qr.quiz.id = :quizId")
    Double findAverageScoreByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT AVG(qr.scorePercentage) FROM QuizResult qr WHERE qr.user.id = :userId")
    Double findAverageScoreByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(qr) FROM QuizResult qr WHERE qr.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(qr) FROM QuizResult qr WHERE qr.quiz.id = :quizId")
    Long countByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT qr FROM QuizResult qr WHERE qr.completedAt BETWEEN :startDate AND :endDate")
    List<QuizResult> findByCompletedAtBetween(@Param("startDate")LocalDateTime startDate , @Param("endDae") LocalDateTime endDate);
}
