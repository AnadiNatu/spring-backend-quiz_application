package com.example.QuizApplicationImplemented.repository;

import com.example.QuizApplicationImplemented.entity.Responses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseRepository extends JpaRepository<Responses, Long> {

    @Query("SELECT r FROM Responses r JOIN r.user u WHERE u.id = :userId")
    List<Responses> findByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM Responses r JOIN r.user u WHERE u.name = :name")
    List<Responses> findByUserName(@Param("userName")String userName);

    @Query("SELECT r FROM Responses r JOIN r.quiz q WHERE q.id = :quizId")
    List<Responses> findByQuizId(@Param("quizId") Long quizId);
    
    @Query("SELECT r FROM Responses r JOIN r.user u JOIN r.quiz q WHERE LOWER(u.name)=LOWER(:name) AND LOWER(q.title) LIKE LOWER(:title)")
    List<Responses> findResponseByUserAndQuizTitle(@Param("name")String userName , @Param("title")String quizTitle);

}
