package com.example.QuizApplicationImplemented.exceptions.QuizExcetion;

public class QuizWithNameOrIdNotFoundException extends RuntimeException {

    private static final long seriesVersionUID = 9;

    public QuizWithNameOrIdNotFoundException(String message){super(message);}
}
