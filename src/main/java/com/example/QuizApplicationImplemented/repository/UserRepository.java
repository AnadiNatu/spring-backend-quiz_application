package com.example.QuizApplicationImplemented.repository;

import com.example.QuizApplicationImplemented.entity.Users;
import com.example.QuizApplicationImplemented.enums.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    @Query("SELECT u FROM Users u WHERE LOWER(u.username) = LOWER(:username)")
    Optional<Users> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM Users u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%' , :name , '%'))")
    Optional<Users> findByName(@Param("name") String name);

    @Query("SELECT u FROM Users u WHERE u.userRoles = :userRoles")
    List<Users> findAllUserByRoles(@Param("userRoles") UserRoles userRoles);

    @Query("SELECT u FROM Users u WHERE (LOWER(u.username) = LOWER(:username) AND u.userRoles = :userRoles )")
    Optional<Users> findByUserRolesAndUsername(@Param("username") String username ,@Param("userRoles") UserRoles userRoles);

    @Query("SELECT u FROM Users u WHERE u.userRoles = :userRoles )")
    List<Users> findByUserRoles(@Param("userRoles") UserRoles userRoles);
    @Query("SELECT u FROM Users u ORDER BY u.ranking ASC")
    List<Users> findAllOrderByRankingAsc();

    @Query("SELECT u FROM Users u WHERE u.overallPercentage DESC")
    List<Users> findAllOrderByOverallPercentageDesc();

    @Query("SELECT u FROM Users u WHERE u.totalQuizTaken >= :minQuizzes")
    List<Users> findUsersWithMinQuizzesTaken(@Param("minPercentage") Double minPercentage);

    @Query("SELECT COUNT(u) FROM Users u WHERE u.userRoles = :userRoles")
    Long countByUserRoles(@Param("userRoles") UserRoles userRoles);

    @Query("SELECT u FROM Users u WHERE u.resetToken = :token")
    Optional<Users> findByResetToken(@Param("token")String token);
}
