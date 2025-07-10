package com.example.QuizApplicationImplemented.exceptions;

public class ResponseNotReceivedException extends RuntimeException{
    private static final long seriesFinalVersionUID = 13;

    public ResponseNotReceivedException(String message ){super(message);}
}
