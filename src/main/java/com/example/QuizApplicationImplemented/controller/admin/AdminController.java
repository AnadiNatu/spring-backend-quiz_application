package com.example.QuizApplicationImplemented.controller.admin;

import com.example.QuizApplicationImplemented.dto.applicationDTO.*;
import com.example.QuizApplicationImplemented.dto.authenticationDTO.UsersDto;
import com.example.QuizApplicationImplemented.exceptions.QuestionException.QuestionCreationException;
import com.example.QuizApplicationImplemented.exceptions.QuestionNotCreatedException;
import com.example.QuizApplicationImplemented.exceptions.QuizExcetion.QuizNotFoundException;
import com.example.QuizApplicationImplemented.exceptions.QuizExcetion.QuizProcessingErrorException;
import com.example.QuizApplicationImplemented.exceptions.ResponseNotReceivedException;
import com.example.QuizApplicationImplemented.exceptions.UserException.UserRoleIncorrectException;
import com.example.QuizApplicationImplemented.service.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/")
@CrossOrigin("*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/add")
    public ResponseEntity<QuestionDto> addQuestion(@RequestBody CreateQuestionDto dto) {
        try {
            QuestionDto createdQuestion = adminService.addQuestion(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdQuestion);
        } catch (QuestionCreationException ex) {
            return ResponseEntity.badRequest().build();
        } catch (QuestionNotCreatedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("create")
    public ResponseEntity<QuizDto> createQuiz(@RequestBody CreateQuizDto dto) {
        try {
            QuizDto createdQuiz = adminService.createQuiz(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdQuiz);
        } catch (QuizProcessingErrorException ex) {
            return ResponseEntity.badRequest().build();
        } catch (UserRoleIncorrectException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("created/{quizTitle}")
    public ResponseEntity<CreatedQuizDto> getCreatedQuiz(@PathVariable String quizTitle) {
        try {
            CreatedQuizDto quiz = adminService.getCreatedQuiz(quizTitle);
            return ResponseEntity.ok(quiz);
        } catch (QuizProcessingErrorException ex) {
            return ResponseEntity.badRequest().build();
        } catch (UserRoleIncorrectException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("questions/{quizTitle}")
    public ResponseEntity<CreatorUserDto> getAllQuestionsOfQuiz(@PathVariable String quizTitle) {
        try {
            CreatorUserDto creatorUserDto = adminService.getAllQuestionsOfQuiz(quizTitle);
            return ResponseEntity.ok(creatorUserDto);
        } catch (QuizProcessingErrorException | QuizNotFoundException ex) {
            return ResponseEntity.badRequest().build();
        } catch (UserRoleIncorrectException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("creator/all")
    public ResponseEntity<List<QuizDto>> getAllQuizByCreator() {
        try {
            List<QuizDto> quizzes = adminService.getAllTheQuizByCreator();
            return ResponseEntity.ok(quizzes);
        } catch (QuizProcessingErrorException | QuizNotFoundException ex) {
            return ResponseEntity.badRequest().build();
        } catch (UserRoleIncorrectException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("quiz/all")
    public ResponseEntity<List<QuizDto>> getAllQuiz(){
        try{
            List<QuizDto> quizzes = adminService.getAllQuiz();
            return ResponseEntity.ok(quizzes);
        } catch (QuizProcessingErrorException | QuizNotFoundException ex){
            return ResponseEntity.badRequest().build();
        }catch (UserRoleIncorrectException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("participant/{quizTitle}")
    public ResponseEntity<List<QuestionWrapper>> getQuizForParticipant(@PathVariable String quizTitle) {
        try {
            List<QuestionWrapper> questions = adminService.gettingCreatedQuizForParticipant(quizTitle);
            return ResponseEntity.ok(questions);
        } catch (QuizProcessingErrorException | QuizNotFoundException ex) {
            return ResponseEntity.badRequest().build();
        } catch (UserRoleIncorrectException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // Response Management Endpoints
    @PostMapping("responses/submit")
    public ResponseEntity<List<ResponseEvaluationDto>> submitQuizResponse(@RequestBody QuizTakenResponse response) {
        try {
            List<ResponseEvaluationDto> evaluation = adminService.savingResponseResponse(response);
            return ResponseEntity.ok(evaluation);
        } catch (QuizNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (ResponseNotReceivedException ex) {
            return ResponseEntity.badRequest().build();
        } catch (UserRoleIncorrectException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Result Management Endpoints
    @GetMapping("results/user/{quizTitle}")
    public ResponseEntity<ResultDto> getUserResult(@PathVariable String quizTitle) {
        try {
            ResultDto result = adminService.getResultForAUser(quizTitle);
            return ResponseEntity.ok(result);
        } catch (QuizNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (UserRoleIncorrectException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("results/all/{quizTitle}")
    public ResponseEntity<List<ResultDto>> getAllUsersResults(@PathVariable String quizTitle) {
        try {
            List<ResultDto> results = adminService.getResultsForAllUsers(quizTitle);
            return ResponseEntity.ok(results);
        } catch (QuizNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (UserRoleIncorrectException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("getQuiz/{title}")
    public ResponseEntity<QuizDto> getQuizByQuizTitle(@PathVariable(name = "title") String title){
        return ResponseEntity.ok(adminService.getQuizByQuizTitle(title));
    }

    @GetMapping("getQuizTitles")
    public ResponseEntity<List<String>> getAllQuizTitles(){
        return ResponseEntity.ok(adminService.getAllQuizTitles());
    }

    @GetMapping("getTakenQuiz")
    public ResponseEntity<List<String>> getAllTakenQuizTitles(){
        return ResponseEntity.ok(adminService.getAllTheQuizForParticipant());
    }

    @GetMapping("count/category/{category}")
    public ResponseEntity<Integer> getQuestionCountByCategory(@PathVariable String category){
        try{
            int count = adminService.getQuestionCountByCategory(category);
            return ResponseEntity.ok(count);
        }catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("count/category/{category}/difficulty/{difficultyLevel}")
    public ResponseEntity<Integer> getQuestionCountByCategoryAndDifficulty(@PathVariable String category , @PathVariable String difficultyLevel){
        try{
            int count = adminService.getQuestionByCategoryAndDifficulty(category, difficultyLevel);
            return ResponseEntity.ok(count);
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("categories")
    public ResponseEntity<List<String>> getAllCategories(){
        try{
            List<String> categories = adminService.getAllCategories();
            return ResponseEntity.ok(categories);
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("user")
    public ResponseEntity<UsersDto> getUserDetails(){
        return ResponseEntity.ok(adminService.getUserDetails());
    }

}
