package com.example.QuizApplicationImplemented.mapper;

import com.example.QuizApplicationImplemented.dto.applicationDTO.*;
import com.example.QuizApplicationImplemented.dto.authenticationDTO.UsersDto;
import com.example.QuizApplicationImplemented.entity.*;
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

    public UserProfileDto toUserProfileDto(Users users) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(users.getId());
        dto.setName(users.getName());
        dto.setUsername(users.getUsername());
        dto.setAge(users.getAge());
        dto.setUserRoles(users.getUserRoles());
        dto.setTotalQuizCreated(users.getTotalQuizCreated());
        dto.setTotalQuizTaken(users.getTotalQuizTaken());
        dto.setRanking(users.getRanking());
        dto.setOverallPercentage(users.getOverallPercentage());
        dto.setCreatedAt(users.getCreatedAt());
        dto.setUpdatedAt(users.getUpdatedAt());
        dto.setProfilePhoto(users.getProfilePhoto());
        return dto;
    }

    public ParticipantDto toParticipantDto(Users users) {
        ParticipantDto dto = new ParticipantDto();
        dto.setId(users.getId());
        dto.setName(users.getName());
        dto.setUsername(users.getUsername());
        dto.setAge(users.getAge());
        dto.setUserRoles(users.getUserRoles());
        dto.setTotalQuizTaken(users.getTotalQuizTaken());
        dto.setOverallPercentage(users.getOverallPercentage());
        dto.setRanking(users.getRanking());
        return dto;
    }

    public CreatorDto toCreatorDto(Users users, Double averageRating) {
        CreatorDto dto = new CreatorDto();
        dto.setId(users.getId());
        dto.setName(users.getName());
        dto.setUsername(users.getUsername());
        dto.setAge(users.getAge());
        dto.setUserRoles(users.getUserRoles());
        dto.setTotalQuizCreated(users.getTotalQuizCreated());
        dto.setAverageQuizRating(averageRating != null ? averageRating : 0.0);
        return dto;
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

    public QuestionDto toQuestionDto(Questions questions){
        List<Long> quizIds = new ArrayList<>();

        if (questions.getQuizzes() != null){
            quizIds = questions.getQuizzes().stream().map(Quiz::getId).collect(Collectors.toList());
        }

        QuestionDto questionDto = new QuestionDto();
        questionDto.setId(questions.getId());
        questionDto.setCategory(questionDto.getCategory());
        questionDto.setDifficultyLevel(questions.getDifficultyLevel());
        questionDto.setRightAnswer(questions.getRightAnswer());
        questionDto.setQuestionTitle(questions.getQuestionTitle());
        questionDto.setOption1(questions.getOption1());
        questionDto.setOption2(questions.getOption2());
        questionDto.setOption3(questions.getOption3());
        questionDto.setOption4(questions.getOption4());
        questionDto.setQuizId(quizIds);
        return questionDto;
    }

    public Quiz toCreateQuiz(CreateQuizDto quizDto , Users creator , List<Questions> selectedQuestions){
// Using JWTUTil "loggedInUser" method to get the user , role check it and send it to this function

        Quiz quiz = new Quiz();
        quiz.setTitle(quizDto.getQuizTitle());
        quiz.setCategory(quizDto.getCategory());
        quiz.setDifficultyLevel(quizDto.getDifficultyLevel());
        quiz.setCreatedBy(creator);
        quiz.setQuestions(selectedQuestions);
        quiz.setParticipants(new ArrayList<>());

        return quiz;

        // Service Function Code
//        List<Questions> selectedQuestions = questionRepository
//    .findRandomByCategoryAndDifficulty(quizDto.getCategory(), quizDto.getDifficultyLevel(), quizDto.getNoOfQuestions());
//
//Users creator = userService.getLoggedInUser(); // however you fetch the logged-in user
//
//Quiz quizEntity = quizMapper.toCreateQuiz(quizDto, creator, selectedQuestions);
//quizRepository.save(quizEntity);
    }

//    public CreatedQuizDto toCreatedQuizDto(Quiz quiz , List<Questions> questionsList){
//        CreatedQuizDto quizDto = new CreatedQuizDto();
//        List<QuestionDto> questionDtoList = new ArrayList<>();
//
//        for (Questions question : questionsList){
//            questionDtoList.add(toQuestionDto(question));
//        }
//
//        quizDto.setTitle(quiz.getTitle());
//        quizDto.setCategory(quiz.getCategory());
//        quizDto.setDifficultyLevel(quiz.getDifficultyLevel());
//        quizDto.setCreatorName(quizDto.getCreatorName());
//        quizDto.setQuestionList(questionDtoList);
//
//        return quizDto;
//    }

    public QuestionWrapper toQuestionWrapper(Questions questions){

        QuestionWrapper questionWrapper = new QuestionWrapper();

        questionWrapper.setQuestionId(questions.getId());
        questionWrapper.setQuestionTitle(questionWrapper.getQuestionTitle());
        questionWrapper.setOption1(questionWrapper.getOption1());
        questionWrapper.setOption2(questionWrapper.getOption2());
        questionWrapper.setOption2(questionWrapper.getOption2());
        questionWrapper.setOption2(questionWrapper.getOption2());

        return questionWrapper;
    }

//    public QuestionDto toQuestionDto(Questions questions){
//        List<Long> quizId = new ArrayList<>();
//        if (questions.getQuizzes() != null){
//            quizId = questions.getQuizzes().stream().map(Quiz::getId).collect(Collectors.toList());
//        }
//
//        QuestionDto questionDto = new QuestionDto();
//
//                questionDto.setId(questions.getId());
//                questionDto.setCategory(questions.getCategory());
//                questionDto.setDifficultyLevel(questions.getDifficultyLevel());
//                questionDto.setRightAnswer(questions.getRightAnswer());
//                questionDto.setQuestionTitle(questions.getQuestionTitle());
//                questionDto.setOption1(questions.getOption1());
//                questionDto.setOption2(questions.getOption2());
//                questionDto.setOption3(questions.getOption3());
//                questionDto.setOption3(questions.getOption3());
//                questionDto.setQuizId(quizId);
//
//                return questionDto;
//    }

    public QuizDto toQuizDto(Quiz quiz) {
        QuizDto dto = new QuizDto();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setCategory(quiz.getCategory());
        dto.setDifficultyLevel(quiz.getDifficultyLevel());
        dto.setTotalQuestions(quiz.getTotalQuestions());
        dto.setAverageScore(quiz.getAverageScore());
        dto.setRating(quiz.getRating());
        dto.setCreatedAt(quiz.getCreatedAt());
        dto.setUpdatedAt(quiz.getUpdatedAt());

        if (quiz.getCreatedBy() != null) {
            dto.setCreatorUserId(quiz.getCreatedBy().getId());
            dto.setCreatorUserName(quiz.getCreatedBy().getName());
        }

        if (quiz.getQuestions() != null) {
            List<Long> questionIds = quiz.getQuestions().stream()
                    .map(Questions::getId)
                    .collect(Collectors.toList());
            dto.setQuestionIds(questionIds);
        } else {
            dto.setQuestionIds(Collections.emptyList());
        }

        if (quiz.getParticipants() != null) {
            List<String> participantNames = quiz.getParticipants().stream()
                    .map(Users::getName)
                    .collect(Collectors.toList());
            dto.setParticipantUserNames(participantNames);
            dto.setTotalParticipant(quiz.getParticipants().size());
        } else {
            dto.setParticipantUserNames(Collections.emptyList());
            dto.setTotalParticipant(0);
        }

        return dto;
    }

    public CreatedQuizDto toCreatedQuizDto(Quiz quiz ,List<Questions> questionsList){
        CreatedQuizDto quizDto = new CreatedQuizDto();

        List<QuestionDto> questionDtoList = new ArrayList<>();

        for (Questions question : questionsList){
            questionDtoList.add(toQuestionDto(question));
        }

        quizDto.setTitle(quiz.getTitle());
        quizDto.setCategory(quiz.getCategory());
        quizDto.setDifficultyLevel(quiz.getDifficultyLevel());
        quizDto.setTotalQuestions(quiz.getTotalQuestions());
        quizDto.setAverageScore(quiz.getAverageScore());
        quizDto.setRating(quizDto.getRating());
        quizDto.setCreatedAt(quiz.getCreatedAt());
        quizDto.setQuestionList(questionDtoList);

        if (quiz.getCreatedBy() != null){
            quizDto.setCreatorName(quiz.getCreatedBy().getName());
        }else {
            quizDto.setCreatorName("Unknown");
        }

        return quizDto;
    }

    public QuizStatsDto toQuizStatsDto(Quiz quiz) {
        QuizStatsDto dto = new QuizStatsDto();

        dto.setQuizId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setCategory(quiz.getCategory());
        dto.setDifficultyLevel(quiz.getDifficultyLevel());
        dto.setTotalQuestions(quiz.getTotalQuestions());
        dto.setTotalParticipants(quiz.getParticipants() != null ? quiz.getParticipants().size() : 0);
        dto.setAverageScore(quiz.getAverageScore());
        dto.setRating(quiz.getRating());
        dto.setCreatorName(quiz.getCreatedBy() != null ? quiz.getCreatedBy().getName() : "Unknown");

        return dto;
    }

    public QuizResult toQuizResult(QuizTakenResponse response , Quiz quiz , Users user , List<String> questionTitle , List<String> selectedAnswers , Integer correctAnswers , Integer wrongAnswer , Double percentage){

        QuizResult result = new QuizResult();
        result.setUsers(user);
        result.setQuiz(quiz);
        result.setTotalQuestions(quiz.getTotalQuestions());
        result.setCorrectAnswers(correctAnswers);
        result.setWrongAnswers(wrongAnswer);
        result.setScorePercentage(percentage);
        result.setQuestionIds(questionIds);
        result.setSelectedAnswers(selectedAnswers);

        return result;
    }

    public QuizResultDto toQuizResultDto(QuizResult result){
        QuizResultDto dto = new QuizResultDto();
        dto.setId(result.getId());
        dto.setUserId(result.getUsers().getId());
        dto.setUsername(result.getUsers().getUsername());
        dto.setQuizId(result.getQuiz().getId());
        dto.setQuizTitle(result.getQuiz().getTitle());
        dto.setTotalQuestions(result.getTotalQuestions());
        dto.setCorrectAnswer(result.getCorrectAnswers());
        dto.setWrongAnswer(result.getWrongAnswers());
        dto.setScorePercentage(result.getScorePercentage());
        dto.setCompleteAt(result.getCompletedAt());
        dto.setQuestionIds(result.getQuestionIds());
        dto.setSelectedAnswers(result.getSelectedAnswers());

        return dto;
    }

    public ResultDto toResultDto(QuizResult result) {

        ResultDto dto = new ResultDto();
        dto.setQuizResultId(result.getId());
        dto.setQuizId(result.getQuiz().getId());
        dto.setQuizTitle(result.getQuiz().getTitle());
        dto.setUserId(result.getUsers().getId());
        dto.setUserName(result.getUsers().getName());
        dto.setTotalQuestions(result.getTotalQuestions());
        dto.setCorrectAnswer(result.getCorrectAnswers());
        dto.setWrongAnswer(result.getWrongAnswers());
        dto.setScorePercentage(result.getScorePercentage());
        dto.setCompleteAt(result.getCompletedAt());
        return dto;

    }

    // User Stats mappings
    public UserStatsDto toUserStatsDto(UserStats stats) {
        UserStatsDto dto = new UserStatsDto();
        dto.setId(stats.getId());
        dto.setUserId(stats.getUser().getId());
        dto.setUsername(stats.getUser().getUsername());
        dto.setCategory(stats.getCategory());
        dto.setTotalQuizzesTaken(stats.getTotalQuizzesTaken());
        dto.setTotalCorrectAnswers(stats.getTotalCorrectAnswers());
        dto.setTotalWrongAnswers(stats.getTotalWrongAnswers());
        dto.setAverageScore(stats.getAverageScore());
        dto.setCategoryPercentage(stats.getCategoryPercentage());
        dto.setLastUpdated(stats.getLastUpdated());
        return dto;
    }

    public CategoryStatsDto toCategoryStatsDto(String category , Integer totalQuestions , Integer totalQuizzes , Integer totalParticipants , Double averageScore){
        CategoryStatsDto dto = new CategoryStatsDto();

        dto.setCategory(category);
        dto.setTotalQuestions(totalQuestions);
        dto.setTotalQuizzes(totalQuizzes);
        dto.setTotalParticipants(totalParticipants);
        dto.setAverageScore(averageScore);
        return dto;
    }

//    public QuizDto toQuizDto(Quiz quiz){
//
//        QuizDto dto = new QuizDto();
//
//        dto.setTitle(quiz.getTitle());
//        dto.setCategory(quiz.getCategory());
//        dto.setDifficultyLevel(quiz.getDifficultyLevel());
//
//        if (quiz.getCreatedBy() != null){
//            dto.setCreatorUserId(quiz.getCreatedBy().getId());
//            dto.setCreatorUserName(quiz.getCreatedBy().getName());
//        }else{
//            dto.setCreatorUserId(null);
//            dto.setCreatorUserName(null);
//        }
//
//        List<Long> questionIds = new ArrayList<>();
//        if (quiz.getQuestions() != null){
//            questionIds = quiz.getQuestions().stream().map(Questions::getId).collect(Collectors.toList());
//        }
//        dto.setQuestionsQuizIds(questionIds);
//
//        List<String> participantNames = new ArrayList<>();
//        if (quiz.getParticipants() == null){
//            dto.setParticipantUserName(participantNames);
//        }else {
//            participantNames = quiz.getParticipants().stream().map(Users::getName).collect(Collectors.toList());
//            dto.setParticipantUserName(participantNames);
//        }
//
//        return dto;
//    }

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
