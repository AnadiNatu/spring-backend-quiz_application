package com.example.QuizApplicationImplemented.exceptions.UserException;

public class UserWithNameNotFound extends RuntimeException {

    private static final long seriesVersionUID = 2;

    public UserWithNameNotFound(String message){ super(message);}
}
