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

    Optional<Users> findByUserRoles(UserRoles userRoles);

    @Query("SELECT u FROM Users u WHERE u.userRoles = :userRoles AND u.username = :username")
    Optional<Users> findAdminsByRoleAndUsername(@Param("userRoles") UserRoles userRoles , @Param("username") String username);

}
