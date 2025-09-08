package com.example.QuizApplicationImplemented.mapper;

import com.example.QuizApplicationImplemented.dto.applicationDTO.*;
import com.example.QuizApplicationImplemented.dto.authenticationDTO.UsersDto;
import com.example.QuizApplicationImplemented.entity.Questions;
import com.example.QuizApplicationImplemented.entity.Quiz;
import com.example.QuizApplicationImplemented.entity.Responses;
import com.example.QuizApplicationImplemented.entity.Users;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class Mapper {

    public UsersDto mapFromUserToUserDTO(Users users) {

        UsersDto usersDto = new UsersDto();

        usersDto.setId(users.getId());
        usersDto.setName(users.getName());
        usersDto.setUsername(users.getUsername());
        usersDto.setPassword(users.getPassword());
        usersDto.setAge(users.getAge());
        usersDto.setUserRoles(users.getUserRoles());

        return usersDto;
    }

    public Questions toCreateQuestionDto(CreateQuestionDto question){

        Questions createdQuestion = new Questions();

        createdQuestion.setQuestionTitle(question.getQuestionTitle());
        createdQuestion.setCategory(question.getCategory());
        createdQuestion.setDifficultyLevel(question.getDifficultyLevel());
        createdQuestion.setRightAnswer(question.getRightAnswer());
        createdQuestion.setOption1(question.getOption1());
        createdQuestion.setOption2(question.getOption2());
        createdQuestion.setOption3(question.getOption3());
        createdQuestion.setOption4(question.getOption4());
        createdQuestion.setQuizzes(new ArrayList<>());

//        createdQuestion.setQuizzes(null);
//        Why new ArrayList<>() instead of null?
//        It's usually better to initialize to an empty list (unless you're using strict null checks elsewhere). This avoids potential NullPointerException issues.
        return createdQuestion;
    }

    public Quiz toCreateQuiz(CreateQuizDto quizDto , Users creator , List<Questions> selectedQuestions){

        Quiz quiz = new Quiz();
        quiz.setTitle(quizDto.getQuizTitle());
        quiz.setCategory(quizDto.getCategory());
        quiz.setDifficultyLevel(quizDto.getDifficultyLevel());
        quiz.setCreatedBy(creator);
        quiz.setParticipants(new ArrayList<>());
        quiz.setQuestions(selectedQuestions);
        for (Questions q : selectedQuestions) {
            System.out.println("Selected Question: ID=" + q.getId());
        }

        return quiz;
    }

//        for (Questions q : selectedQuestions){
//            if (q.getQuizzes() == null) q.setQuizzes(new ArrayList<>());
//            q.getQuizzes().add(quiz);
//        }


        // Service Function Code
//        List<Questions> selectedQuestions = questionRepository
//    .findRandomByCategoryAndDifficulty(quizDto.getCategory(), quizDto.getDifficultyLevel(), quizDto.getNoOfQuestions());
//
//Users creator = userService.getLoggedInUser(); // however you fetch the logged-in user
//
//Quiz quizEntity = quizMapper.toCreateQuiz(quizDto, creator, selectedQuestions);
//quizRepository.save(quizEntity);

    public CreatedQuizDto toCreatedQuizDto(Quiz quiz , List<Questions> questionsList){
        CreatedQuizDto quizDto = new CreatedQuizDto();
        List<QuestionDto> questionDtoList = new ArrayList<>();

        for (Questions question : questionsList){
            questionDtoList.add(toQuestionDto(question));
        }

        quizDto.setTitle(quiz.getTitle());
        quizDto.setCategory(quiz.getCategory());
        quizDto.setDifficultyLevel(quiz.getDifficultyLevel());
        quizDto.setCreatorName(quizDto.getCreatorName());
        quizDto.setQuestionList(questionDtoList);

        return quizDto;
    }

    public QuestionWrapper toQuestionWrapper(Questions questions){

        QuestionWrapper questionWrapper = new QuestionWrapper();

        questionWrapper.setQuestionTitle(questions.getQuestionTitle());
        questionWrapper.setOption1(questions.getOption1());
        questionWrapper.setOption2(questions.getOption2());
        questionWrapper.setOption3(questions.getOption3());
        questionWrapper.setOption4(questions.getOption4());

        return questionWrapper;

    }

    public QuestionDto toQuestionDto(Questions questions){
        List<Long> quizId = new ArrayList<>();
        if (questions.getQuizzes() != null){
            quizId = questions.getQuizzes().stream().map(Quiz::getId).collect(Collectors.toList());
        }

        QuestionDto questionDto = new QuestionDto();

                questionDto.setId(questions.getId());
                questionDto.setCategory(questions.getCategory());
                questionDto.setDifficultyLevel(questions.getDifficultyLevel());
                questionDto.setRightAnswer(questions.getRightAnswer());
                questionDto.setQuestionTitle(questions.getQuestionTitle());
                questionDto.setOption1(questions.getOption1());
                questionDto.setOption2(questions.getOption2());
                questionDto.setOption3(questions.getOption3());
                questionDto.setOption4(questions.getOption4());
                questionDto.setQuizId(quizId);

                return questionDto;
    }

    public QuizDto toQuizDto(Quiz quiz){

        QuizDto dto = new QuizDto();

        dto.setTitle(quiz.getTitle());
        dto.setCategory(quiz.getCategory());
        dto.setDifficultyLevel(quiz.getDifficultyLevel());

        if (quiz.getCreatedBy() != null) {
            dto.setCreatorUserId(quiz.getCreatedBy().getId());
            dto.setCreatorUserName(quiz.getCreatedBy().getName());
        }

        if (quiz.getQuestions() != null) {
            List<Long> questionIds = quiz.getQuestions()
                    .stream()
                    .map(Questions::getId)
                    .collect(Collectors.toList());
            dto.setQuestionsQuizIds(questionIds);
        } else {
            dto.setQuestionsQuizIds(Collections.emptyList());
        }

        if (quiz.getParticipants() != null) {
            List<String> participantNames = quiz.getParticipants()
                    .stream()
                    .map(Users::getName)
                    .collect(Collectors.toList());
            dto.setParticipantUserName(participantNames);
        } else {
            dto.setParticipantUserName(Collections.emptyList());
        }
        return dto;
    }

    public Quiz toQuizEntity(QuizDto dto, Users creator, List<Questions> questions, List<Users> participants) {
        Quiz quiz = new Quiz();

        quiz.setTitle(dto.getTitle());
        quiz.setCategory(dto.getCategory());
        quiz.setDifficultyLevel(dto.getDifficultyLevel());

        // Set creator, questions, and participants
        quiz.setCreatedBy(creator);
        quiz.setQuestions(questions != null ? questions : new ArrayList<>());
        quiz.setParticipants(participants != null ? participants : new ArrayList<>());

        return quiz;
    }

    public Responses toResponseEntity(QuizTakenResponse quizTakenResponse , Quiz quiz , Users users){

//        What to be sending from the service layer
//        Quiz quiz = quizRepository.findQuizByTitle(quizTakenResponse.getQuizTitle())
//            .orElseThrow(() -> new RuntimeException("Quiz not found with title: " + quizTakenResponse.getQuizTitle()));
//
//    // Fetch the User entity
//    Users user = userRepository.findByNameIgnoreCase(quizTakenResponse.getUserName())
//            .orElseThrow(() -> new RuntimeException("User not found with name: " + quizTakenResponse.getUserName()));

        List<Questions> quizQuestions = quiz.getQuestions();
        Map<String , Questions> questionTitleToEntityMap = quizQuestions
                .stream()
                .collect(Collectors.toMap(
                        q -> q.getQuestionTitle().toLowerCase(),
                        q -> q
                ));

        List<Long> questionIds = new ArrayList<>();
        List<String> selectedAnswers = new ArrayList<>();

        for (ResponseDto responseDto : quizTakenResponse.getResponseList()){
            String questionTitle = responseDto.getQuestionTitle();
            String selected = responseDto.getSelectedAnswer();

            if (selected == null) continue;

            Questions matchingQuestion = questionTitleToEntityMap.get(questionTitle.toLowerCase());
            if (matchingQuestion != null){
                questionIds.add(matchingQuestion.getId());
                selectedAnswers.add(selected);
            }
        }
        Responses response = new Responses();
        response.setUser(users);
        response.setQuiz(quiz);
        response.setQuestionId(questionIds);
        response.setSelectedAnswer(selectedAnswers);

        return response;
    }

    public List<Long> getQuestionIdsOfQuiz(Quiz quiz) {
        if (quiz.getQuestions() == null) return Collections.emptyList();

        return quiz.getQuestions().stream()
                .map(Questions::getId)
                .collect(Collectors.toList());
    }
}
