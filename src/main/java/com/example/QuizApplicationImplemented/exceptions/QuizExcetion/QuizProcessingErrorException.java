package com.example.QuizApplicationImplemented.exceptions.QuizExcetion;

public class QuizProcessingErrorException extends RuntimeException {

    private static final long seriesVersionUID = 5;

    public QuizProcessingErrorException(String message){super(message);}
}
