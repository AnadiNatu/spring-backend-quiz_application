package com.example.QuizApplicationImplemented.repository;

import com.example.QuizApplicationImplemented.entity.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStats , Long> {
    @Query("SELECT us FROM UserStats us WHERE us.user.id = :userId")
    List<UserStats> findByUserId(@Param("userId") Long userId);

    @Query("SELECT us FROM UserStats us WHERE LOWER(us.user.username) = LOWER(:username)")
    List<UserStats> findByUsername(@Param("username") String username);

    @Query("SELECT us FROM UserStats us WHERE us.user.id = :userId AND LOWER(us.category) = LOWER(:category)")
    Optional<UserStats> findByUserIdAndCategory(@Param("userId") Long userId, @Param("category") String category);

    @Query("SELECT us FROM UserStats us WHERE LOWER(us.category) = LOWER(:category)")
    List<UserStats> findByCategory(@Param("category") String category);

    @Query("SELECT us FROM UserStats us WHERE us.averageScore >= :minScore")
    List<UserStats> findByAverageScoreGreaterThanEqual(@Param("minScore") Double minScore);

    @Query("SELECT us FROM UserStats us WHERE us.categoryPercentage >= :minPercentage")
    List<UserStats> findByCategoryPercentageGreaterThanEqual(@Param("minPercentage") Double minPercentage);

    @Query("SELECT us FROM UserStats us ORDER BY us.averageScore DESC")
    List<UserStats> findAllOrderByAverageScoreDesc();

    @Query("SELECT us FROM UserStats us WHERE LOWER(us.category) = LOWER(:category) ORDER BY us.categoryPercentage DESC")
    List<UserStats> findByCategoryOrderByPercentageDesc(@Param("category") String category);

    @Query("SELECT DISTINCT us.category FROM UserStats us ORDER BY us.category")
    List<String> findAllCategories();

    @Query("SELECT AVG(us.averageScore) FROM UserStats us WHERE LOWER(us.category) = LOWER(:category)")
    Double findAverageScoreByCategory(@Param("category") String category);
}
