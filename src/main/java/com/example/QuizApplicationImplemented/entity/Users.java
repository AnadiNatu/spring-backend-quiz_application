package com.example.QuizApplicationImplemented.entity;

import com.example.QuizApplicationImplemented.enums.UserRoles;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class Users implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false , length = 100)
    private String name;

    @Column(nullable = false , unique = true , length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Integer age;

    @Column(length = 255)
    private String resetToken;

    @Lob
    private byte[] profilePhoto;

    @Column(nullable = false)
    private Integer totalQuizCreated;

    @Column(nullable = false)
    private Integer totalQuizTaken;

    @Column(nullable = false)
    private Integer ranking;

    @Column(nullable = false)
    private Double overallPercentage;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private UserRoles userRoles;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Quiz> quizzesCreated = new ArrayList<>();

    @ManyToMany(mappedBy = "participants" , fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Quiz> quizzesTaken = new ArrayList<>();

    @OneToMany(mappedBy = "user" , fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    @JsonIgnore
    private List<QuizResult> quizResults = new ArrayList<>();

    @OneToMany(mappedBy = "user" , fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    @JsonIgnore
    private List<UserStats> userStats = new ArrayList<>();

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (totalQuizCreated == null) totalQuizCreated = 0;
        if (totalQuizTaken == null) totalQuizTaken = 0;
        if (ranking == null) ranking = 0;
        if (overallPercentage == null) overallPercentage = 0.0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userRoles.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                ", totalQuizCreated=" +totalQuizCreated+ '\''+
                ", totalQuizTaken=" +totalQuizTaken+ '\''+
                ", ranking" +ranking+ '\''+
                ", overallPercentage" +overallPercentage+ '\''+
                ", resetToken='" + resetToken + '\'' +
                ", userRoles=" + userRoles +
                '}';
    }
}