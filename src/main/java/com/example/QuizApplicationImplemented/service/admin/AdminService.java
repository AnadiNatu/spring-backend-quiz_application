package com.example.QuizApplicationImplemented.service.admin;

import com.example.QuizApplicationImplemented.dto.applicationDTO.*;
import com.example.QuizApplicationImplemented.dto.authenticationDTO.UsersDto;
import com.example.QuizApplicationImplemented.entity.*;
import com.example.QuizApplicationImplemented.enums.UserRoles;
import com.example.QuizApplicationImplemented.exceptions.QuestionException.QuestionCreationException;
import com.example.QuizApplicationImplemented.exceptions.QuestionException.QuestionDataInvalidException;
import com.example.QuizApplicationImplemented.exceptions.QuestionNotCreatedException;
import com.example.QuizApplicationImplemented.exceptions.QuizExcetion.QuizNotFoundException;
import com.example.QuizApplicationImplemented.exceptions.QuizExcetion.QuizProcessingErrorException;
import com.example.QuizApplicationImplemented.exceptions.ResponseNotReceivedException;
import com.example.QuizApplicationImplemented.exceptions.UserException.UserRoleIncorrectException;
import com.example.QuizApplicationImplemented.mapper.Mapper;
import com.example.QuizApplicationImplemented.repository.*;
import com.example.QuizApplicationImplemented.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.apache.catalina.User;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private UserStatsRepository userStatsRepository;

    @Autowired
    private Mapper mapper;

    @Autowired
    private JwtUtil jwtUtil;

//    Actual Functions

//    Adding a question (ADMIN OR CREATOR) - CORRECT
    public QuestionDto addQuestion(CreateQuestionDto dto) {
        Users users = jwtUtil.getLoggedInUser();
        if (userRolesCheckForAdminAndCreator(users)){
            try{
                Questions saveQuestion = mapper.toCreateQuestionDto(dto);
                Questions savedQuestion = questionRepository.save(saveQuestion);
                return mapper.toQuestionDto(savedQuestion);
            }catch (QuestionCreationException ex){
                throw new QuestionCreationException("Question Was Not Created Properly");
            }
        }else
            throw  new QuestionNotCreatedException("Question was not created");
    }

//    Getting the Creating Quiz (ADMIN OR CREATOR) -
    @Transactional
    public QuizDto createQuiz(CreateQuizDto dto){

        Users creator = jwtUtil.getLoggedInUser();

        if (creator == null){
            throw new RuntimeException("Unauthorized or expired token");
        }

        if (!userRolesCheckForAdminAndCreator(creator)){
            throw new UserRoleIncorrectException("User is not authorization to create quizzes");
        }

        try{
            Optional<Quiz> existingQuiz = quizRepository.findByTitle(dto.getQuizTitle());
            if (existingQuiz.isPresent()){
                throw new QuizProcessingErrorException("Quiz with this title already exists");
            }

            List<Questions> availableQuestions = questionRepository.findByCategoryAndDifficultyLevel(dto.getCategory(), dto.getDifficultyLevel());

            if (availableQuestions.size() < dto.getNoOfQuestions()){
                throw new IllegalArgumentException(
                        String.format("Not enough questions available . Required : %d , Available : %d" , dto.getNoOfQuestions() , availableQuestions.size())
                );
            }

            Quiz quiz = new Quiz();
            quiz.setTitle(dto.getQuizTitle());
            quiz.setCategory(dto.getCategory());
            quiz.setDifficultyLevel(dto.getDifficultyLevel());
            quiz.setCreatedBy(creator);
            quiz.setTotalQuestions(dto.getNoOfQuestions());

            quiz = quizRepository.saveAndFlush(quiz);
            Collections.shuffle(availableQuestions);

            List<Questions> selectedQuestions = availableQuestions.subList(0 , dto.getNoOfQuestions());

            quiz.addQuestions(selectedQuestions);
            quiz = quizRepository.saveAndFlush(quiz);

            creator.setTotalQuizCreated(creator.getTotalQuizCreated() + 1);
            userRepository.save(creator);

            return mapper.toQuizDto(quiz);
        }catch (Exception ex){
            throw new QuizProcessingErrorException("Failed to create quiz: " + ex.getMessage());
        }
//        if (userRolesCheckForAdminAndCreator(users)){
//            try{
//                List<Questions> availableQuestions = questionRepository.findByCategoryAndDifficultyLevel(dto.getCategory(), dto.getDifficultyLevel());
//
//                if (availableQuestions == null || availableQuestions.isEmpty()) {
//                    throw new QuizProcessingErrorException("No questions found for category: " + dto.getCategory() + " and difficulty: " + dto.getDifficultyLevel());
//                }
//
//                if (availableQuestions.size() < dto.getNoOfQuestions()) {
//                    throw new QuizProcessingErrorException("Not enough questions available. Requested: " + dto.getNoOfQuestions() + ", Available: " + availableQuestions.size());
//                }
//
//                List<Questions> selectedQuestions = selectRandomQuestions(availableQuestions , dto.getNoOfQuestions());
//
//                Quiz quiz = mapper.toCreateQuiz(dto, users, selectedQuestions);
//
//                Quiz savedQuiz = quizRepository.save(quiz);
//
//                return mapper.toQuizDto(savedQuiz);
//
//            }catch(QuizProcessingErrorException ex){
//                throw new QuizProcessingErrorException("Quiz Was Not Created");
//            }
//        }else
//            throw new UserRoleIncorrectException("User Is Not Authorized To Do This");
    }

//  Getting the Created Quiz (ADMIN OR CREATOR)
public CreatedQuizDto getCreatedQuiz(String quizTitle) {
    Users users = jwtUtil.getLoggedInUser();

    if (!userRolesCheckForAdminAndCreator(users)){
        throw new UserRoleIncorrectException("User is not authorized to access this");
    }

    try{
        Quiz quiz = quizRepository.findByTitle(quizTitle).orElseThrow(() -> new QuizNotFoundException("Quiz with title '" + quizTitle + "' not found"));

        List<Questions> questionsList = quiz.getQuestions();
        return mapper.toCreatedQuizDto(quiz , questionsList);
    }catch(Exception ex){
        throw new QuizProcessingErrorException("Failed to retrieve quiz: " + ex.getMessage());
    }
//    if (userRolesCheckForAdminAndCreator(users)){
//        try{
//            Optional<Quiz> optionalQuiz = quizRepository.findQuizByTitle(quizTitle);
//            Quiz quiz = optionalQuiz.get();
//
//            List<Questions> questionsList = quiz.getQuestions();
//
//            CreatedQuizDto createdQuizDto = mapper.toCreatedQuizDto(quiz, questionsList);
//
//            if (quiz.getCreatedBy() != null && quiz.getCreatedBy().getName() != null) {
//                createdQuizDto.setCreatorName(quiz.getCreatedBy().getName());
//            } else {
//                createdQuizDto.setCreatorName("Unknown");
//            }
//
//            return createdQuizDto;
//        }catch (QuizProcessingErrorException ex){
//            throw new QuizProcessingErrorException("Quiz Was Not Created");
//        }
//    }else
//        throw new UserRoleIncorrectException("User Is Not Authorized To Do This");
}

// Getting all questions of a category (ADMIN OR CREATOR)
    public List<QuestionDto> getAllQuestionByCategory(String category){
        Users users = jwtUtil.getLoggedInUser();
//        if (userRolesCheckForAdminAndCreator(users)){

        if (!userRolesCheckForAdminAndCreator(users)) {
            throw new UserRoleIncorrectException("User is not authorized to access this");
        }

        try {
            List<Questions> questionList = questionRepository.findByCategory(category);

            if (questionList.isEmpty()) {
                throw new QuestionDataInvalidException("No questions found for category: " + category);
            }

            return questionList.stream().map(mapper::toQuestionDto).collect(Collectors.toList());
        }catch (QuestionDataInvalidException ex){
                throw new QuestionDataInvalidException("Question With Specified Category Were Not Found");
            }
    }


//    Getting all the question of a quiz (ADMIN OR CREATOR)
    public CreatorUserDto getAllQuestionsOfQuiz(String quizTitle){
        Users users = jwtUtil.getLoggedInUser();

        if (!userRolesCheckForAdminAndCreator(users)) {
            throw new UserRoleIncorrectException("User is not authorized to access this");
        }

        try{
            Quiz quiz = quizRepository.findByTitle(quizTitle)
                    .orElseThrow(() -> new QuizNotFoundException("Quiz with title '" + quizTitle + "' not found"));

            CreatorUserDto userDTO = new CreatorUserDto();

            if (quiz.getCreatedBy().getUsername().equalsIgnoreCase(users.getUsername()) || users.getUserRoles() == UserRoles.ADMIN){
                userDTO.setCreatorName(users.getName() != null ? users.getName() : "Admin");
                userDTO.setUserRoles(users.getUserRoles());
                userDTO.setQuestionTitleList(getQuestionTitlesOfQuiz(quiz));
            }else {
                throw new UserRoleIncorrectException("User is not authorized to view this quiz");
            }
            return userDTO;
        }catch (Exception ex){
            throw new QuizProcessingErrorException("Failed to retrieve quiz questions: " + ex.getMessage());
        }
//        if (userRolesCheckForAdminAndCreator(users)){
//            try{
//                Quiz quiz = quizRepository.findQuizByTitle(quizTitle).orElseThrow(() -> new QuizNotFoundException("Quiz With This Title Was Not Found"));
//                CreatorUserDto userDTO = new CreatorUserDto();
//                if (quiz.getCreatedBy().getName().equalsIgnoreCase(users.getName())){
//                    userDTO.setCreatorName(users.getName());
//                    userDTO.setUserRoles(UserRoles.CREATOR);
//                    userDTO.setQuestionTitleList(getQuestionTitlesOfQuiz(quiz));
//                }
//                return userDTO;
//            }catch(QuizProcessingErrorException ex){
//                throw new QuizProcessingErrorException("Quiz  With The Given Title Was Not Processed");
//            }
//        }else
//            throw new UserRoleIncorrectException("User Is Not Authorized To Do This");
    }

//    Getting all quiz by creator name (ADMIN OR CREATOR)
    public List<QuizDto> getAllTheQuizByCreator(){
        Users users = jwtUtil.getLoggedInUser();

        if (!userRolesCheckForAdminAndCreator(users)){
            throw new UserRoleIncorrectException("User is not authorized to access this");
        }

        try{
            List<Quiz> quizList;

            if (users.getUserRoles() == UserRoles.ADMIN){
                quizList = quizRepository.findAll();
            }else{
                quizList = quizRepository.findQuizzesCreatedByUsername(users.getUsername());
            }

            return quizList.stream().map(mapper::toQuizDto).collect(Collectors.toList());
        }catch (Exception ex){
            throw new QuizProcessingErrorException("Failed to retrieve quizzes: " + ex.getMessage());
        }

//        if (userRolesCheckForAdminAndCreator(users)){
//            try{
//                List<String> quizTitleList = getQuizTitlesByCreatorName(users.getName());
//                List<QuizDto> quizDtoList = new ArrayList<>();
//                for (String quizTitle : quizTitleList){
//                    Quiz quiz = quizRepository.findQuizByTitle(quizTitle).orElseThrow(() -> new QuizNotFoundException("Quiz With This Title Was Not Found"));
//                    quizDtoList.add(mapper.toQuizDto(quiz));
//                }
//                return quizDtoList;
//            }catch (QuizProcessingErrorException ex){
//                throw new QuizProcessingErrorException("Quiz  With The Given Title Was Not Processed");
//            }
//        }else
//            throw new UserRoleIncorrectException("User Is Not Authorized To Do This");
    }

//    Getting all quizzes (ADMIN & CREATOR) - IMPROVED
    public List<QuizDto> getAllQuiz(){
        Users users = jwtUtil.getLoggedInUser();

        if (!userRolesCheckForAdminAndCreator(users)){
            throw new UserRoleIncorrectException("User is not authorized to access this");
        }

        try{
            List<Quiz> quizList = quizRepository.findAll();

            return quizList.stream()
                    .map(mapper::toQuizDto)
                    .collect(Collectors.toList());
        }catch (Exception ex){
            throw new QuizProcessingErrorException("Failed to retrieve quizzes: " + ex.getMessage());
        }
    }

//    Getting a created quiz (ADMIN or PARTICIPANT)
    @Transactional
    public List<QuestionWrapper> gettingCreatedQuizForParticipant(String quizTitle){
        Users user = jwtUtil.getLoggedInUser();

        if (!userRolesCheckForAdminAndParticipant(user)){
            throw new UserRoleIncorrectException("User Is Not Authorized To Do This");
        }

        try{
                Quiz quiz = quizRepository.findByTitle(quizTitle).orElseThrow(() -> new QuizNotFoundException("Quiz With This Title Was Not Found"));

                boolean userAlreadyParticipated = quiz
                        .getParticipants()
                        .stream()
                        .anyMatch(participant -> participant.getId().equals(user.getId()));

                if (userAlreadyParticipated){
                    throw new QuizProcessingErrorException("User has already taken this quiz");
                }

                List<Questions> quizQuestions = quiz.getQuestions();
                List<QuestionWrapper> wrapper = quizQuestions.stream().map(mapper::toQuestionWrapper).toList();

                quiz.addParticipant(user);
                quizRepository.saveAndFlush(quiz);

                user.setTotalQuizTaken(user.getTotalQuizTaken() + 1);
                userRepository.save(user);

                return wrapper;
        }catch (Exception ex){
            throw new QuizProcessingErrorException("Failed to start quiz : " + ex.getMessage());
        }
    }



//    Getting the response from the user after taking quiz and storing it (ADMIN or PARTICIPANT)
    @Transactional
    public List<ResponseEvaluationDto> savingResponse1(QuizTakenResponse response){
        Users user = jwtUtil.getLoggedInUser();

        if (!userRolesCheckForAdminAndParticipant(user)) {
            throw new UserRoleIncorrectException("User is not authorized to submit responses");
        }

        try{
            Quiz quiz = quizRepository.findByTitle(response.getQuizTitle()).orElseThrow(() -> new QuizNotFoundException("Quiz not found :" + response.getQuizTitle()));

            boolean userIsParticipant = quiz.getParticipants().stream().anyMatch(p -> p.getId().equals(user.getId()));

            if (!userIsParticipant){
                throw new ResponseNotReceivedException("User is not a participant of this quiz");
            }

            Optional<QuizResult> existingResult = quizResultRepository.findByUserIdAndQuizId(user.getId(), quiz.getId());
            if (existingResult.isPresent()){
                throw new ResponseNotReceivedException("User has already submitted responses for this quiz");
            }

            Map<String , Questions> questionsMap = quiz.getQuestions().stream().collect(Collectors.toMap(Questions::getQuestionTitle , q -> q));
            List<String> questionTitleList = new ArrayList<>();
            List<String> selectedAnswers = new ArrayList<>();
            int correctAnswers = 0;
            int wrongAnswers = 0;

            List<ResponseEvaluationDto> evaluationList = new ArrayList<>();

            for (ResponseDto responseDto : response.getResponseList()){
                String questionTitle = responseDto.getQuestionTitle();
                String selectedAnswer = responseDto.getSelectedAnswer();

                Questions question = questionsMap.get(questionTitle);
                if (question != null){
                    questionTitleList.add(questionTitle);
                    selectedAnswers.add(selectedAnswer != null ? selectedAnswer : "");

                    String correctAnswer = question.getRightAnswer();
                    boolean isCorrect = selectedAnswer != null && selectedAnswer.equalsIgnoreCase(correctAnswer);

                    if (isCorrect){
                        correctAnswers++;
                    }else{
                        wrongAnswers++;
                    }

                    ResponseEvaluationDto evaluationDto = new ResponseEvaluationDto();
                    evaluationDto.setQuestionTitle(question.getQuestionTitle());
                    evaluationDto.setCorrectAnswer(correctAnswer);
                    evaluationDto.setParticipantAnswer(selectedAnswer != null ? selectedAnswer : "Not Answer");
                    evaluationList.add(evaluationDto);
                }
            }

            double scorePercentage = quiz.getTotalQuestions() > 0 ? ((double) correctAnswers/quiz.getTotalQuestions()) * 100 : 0.0;

            QuizResult quizResult = mapper.toQuizResult(response , quiz , user , questionTitleList , selectedAnswers , correctAnswers , wrongAnswers , scorePercentage);
            quizResultRepository.save(quizResult);

            updateUserStats(user , quiz.getCategory() , correctAnswers , wrongAnswers , scorePercentage);

            updateQuizAverageScore(quiz);

            updateUserOverallPercentage(user);

            return evaluationList;
        }catch (Exception ex){
            throw new RuntimeException("Error while saving and evaluating quiz response: " + ex.getMessage(), ex);
        }
    }



    public List<ResponseEvaluationDto> savingResponse(QuizTakenResponse response){
      Users user = jwtUtil.getLoggedInUser();
      if (!userRolesCheckForAdminAndParticipant(user)){
          throw new UserRoleIncorrectException("User is not authorized to do this");
      }

      try{
          Quiz quiz = quizRepository.findByTitle(response.getQuizTitle()).orElseThrow(() -> new QuizNotFoundException("Quiz with this title was not found"));

          boolean userIsParticipant = quiz.getParticipants().stream().anyMatch(p -> p != null && p.getId().equals(user.getId()));
          if (!userIsParticipant){
              throw new ResponseNotReceivedException("User is not a participant of the quiz");
          }

          Responses responses = mapper.toResponseEntity(response , quiz , user);
          responseRepository.save(responses);

          Map<Long , String> correctAnswers = getRightAnswerByQuizTitle(response.getQuizTitle());
          Map<Long , String> userAnswer = getSelectedAnswerByUserAndQuizTitle(user.getName(), response.getQuizTitle());
          Map<Long , String> questionTitles = quiz.getQuestions().stream()
                  .collect(Collectors.toMap(Questions::getId , Questions::getQuestionTitle));

          List<ResponseEvaluationDto> evaluationList = new ArrayList<>();

          for (Map.Entry<Long , String> entry : userAnswer.entrySet()){
              Long qId = entry.getKey();
              String participantAnswer = entry.getValue();
              String correctAnswer = correctAnswers.get(qId);
              String questionTitle = questionTitles.get(qId);

              ResponseEvaluationDto dto = new ResponseEvaluationDto();
              dto.setQuestionTitle(questionTitle);
              dto.setCorrectAnswer(correctAnswer);
              dto.setParticipantAnswer(participantAnswer);
              evaluationList.add(dto);
          }

          return evaluationList;

      }catch (QuizNotFoundException | ResponseNotReceivedException ex){
          throw ex;
      }catch (Exception ex){
          throw new RuntimeException("Error while saving and evaluating quiz response " , ex);
      }
    }

//  Getting all the quiz a Participant Has Taken (ADMIN or CREATOR)
    public List<String> getAllTheQuizForParticipant(){
        Users users = jwtUtil.getLoggedInUser();
        if (!userRolesCheckForAdminAndCreator(users)){
            throw new UserRoleIncorrectException("User is not authorized to do this");
        }

        List<Quiz> quizTaken = quizRepository.findQuizzesByParticipantId(users.getId());
//        List<String> quizTitleList = new ArrayList<>();
//
//        for (Quiz list : quizTakenTitle){
//            quizTitleList.add(list.getTitle());
//        }
//        return quizTitleList;

        return quizTaken.stream()
                .map(Quiz::getTitle)
                .collect(Collectors.toList());
    }



// Getting proper result for the LoggedIn User (ADMIN or PARTICIPANT);

    public ResultDto getResultForAUser(String quizTitle){
        Users user = jwtUtil.getLoggedInUser();
        if (!userRolesCheckForAdminAndParticipant(user)) {
            throw new UserRoleIncorrectException("User is not authorized to access results");
        }

        Quiz quiz = quizRepository.findByTitle(quizTitle)
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found: " + quizTitle));

        QuizResult result = quizResultRepository.findByUserIdAndQuizId(user.getId(), quiz.getId())
                .orElseThrow(() -> new RuntimeException("No result found for user in this quiz"));

        return mapper.toResultDto(result);
    }

//    public ResultDto getResultForAUser(String quizTitle){
//        Users user = jwtUtil.getLoggedInUser();
//        if (!userRolesCheckForAdminAndParticipant(user)){
//            throw new UserRoleIncorrectException("User is not authorized to do this");
//        }
//        Optional<Quiz> quizOptional = quizRepository.findQuizByTitle(quizTitle);
//        if (quizOptional.isEmpty()) {
//            throw new QuizNotFoundException("Quiz with title '" + quizTitle + "' not found");
//        }
//
//        Quiz quiz = quizOptional.get();
//        boolean isParticipant = quiz.getParticipants().stream().anyMatch(p -> p.getId().equals(user.getId()));
//
//        if (!isParticipant){
//            throw new RuntimeException("User did not participate in this quiz");
//        }
//
//        Map<String , Integer> evaluation = evaluateUserAnswers(quizTitle , user.getName());
//
//        int correct = evaluation.getOrDefault("correct" , 0);
//        int wrong = evaluation.getOrDefault("wrong" , 0);
//        int unanswered = evaluation.getOrDefault("unanswered" , 0);
//
//        int totalQuestions = quiz.getQuestions() != null ? quiz.getQuestions().size() : 0;
//        double percentage = totalQuestions == 0 ? 0.0 : ((double) correct/totalQuestions) * 100;
//
//        ResultDto result = new ResultDto();
//        result.setQuizId(quiz.getId());
//        result.setQuizTitle(quiz.getTitle());
//        result.setUserId(user.getId());
//        result.setUserName(user.getName());
//        result.setTotalQuestions(totalQuestions);
//        result.setCorrectAnswer(correct);
//        result.setIncorrectAnswer(wrong + unanswered);
//        result.setPercentage(percentage);
//
//        return result;
//    }

//    Getting the result of all the participants that have taken the quiz (ADMIN or CREATOR)
    public List<ResultDto> getResultsForAllUsers(String quizTitle){

        Users user = jwtUtil.getLoggedInUser();
        if (!userRolesCheckForAdminAndCreator(user)){
            throw new UserRoleIncorrectException("User is not authorized to view all results");
        }

        Quiz quiz = quizRepository.findByTitle(quizTitle).orElseThrow(() -> new UserRoleIncorrectException("User is not authorized to view all results"));

        List<QuizResult> results = quizResultRepository.findByQuizId(quiz.getId());
        if (results.isEmpty()){
            throw new RuntimeException("No participants have attempt the quiz yet");
        }

        return results.stream().map(mapper::toResultDto).collect(Collectors.toList());
//        Users user = jwtUtil.getLoggedInUser();
//        if (!userRolesCheckForAdminAndCreator(user)){
//            throw new UserRoleIncorrectException("User is not authorized to do this");
//        }
//
//        Quiz quiz = quizRepository.findQuizByTitle(quizTitle)
//                .orElseThrow(() -> new QuizNotFoundException("Quiz with title '" + quizTitle + "' not found"));
//
//        List<Responses> allResponses = responseRepository.findByQuizId(quiz.getId());
//
//        if (allResponses.isEmpty()){
//            throw new RuntimeException("No participant has attempted the quiz yet.");
//        }
//
//        Map<Long , String> correctAnswerMap = quiz.getQuestions().stream()
//                .collect(Collectors.toMap(Questions::getId , Questions::getRightAnswer));
//
//        int totalQuestions = correctAnswerMap.size();
//
//        List<ResultDto> resultList = new ArrayList<>();
//        for (Responses responses : allResponses){
//            Users users = responses.getUser();
//            List<Long> questionIds = responses.getQuestionId();
//            List<String> selectedAnswers = responses.getSelectedAnswer();
//            int correct = 0;
//            int incorrect = 0;
//
//            for (int i = 0 ; i < questionIds.size() ; i++) {
//                Long qId = questionIds.get(i);
//                String selected = selectedAnswers.size() > i ? selectedAnswers.get(i) : null;
//                String correctAnswer = correctAnswerMap.get(qId);
//
//                if (selected != null && selected.equalsIgnoreCase(correctAnswer)) {
//                    correct++;
//                } else {
//                    incorrect++;
//                }
//            }
//                double percentage = totalQuestions == 0 ? 0.0 : ((double) correct / totalQuestions) * 100;
//
//                ResultDto result = new ResultDto();
//                result.setQuizId(quiz.getId());
//                result.setQuizTitle(quiz.getTitle());
//                result.setUserId(user.getId());
//                result.setUserName(user.getName());
//                result.setTotalQuestions(totalQuestions);
//                result.setCorrectAnswer(correct);
//                result.setIncorrectAnswer(incorrect);
//                result.setPercentage(percentage);
//
//                resultList.add(result);
//            }
//            return resultList;


    }


//   ==================== NEW FUNCTIONALITIES ====================

//    Get all participants
    public List<ParticipantDto> getAllParticipants(){
        Users user = jwtUtil.getLoggedInUser();
        if (!userRolesCheckForAdminAndCreator(user)){
            throw new UserRoleIncorrectException("User is not authorized to view participants");
        }

        List<Users> participant = userRepository.findByUserRoles(UserRoles.PARTICIPANT);

        return participant.stream().map(mapper::toParticipantDto).collect(Collectors.toList());
    }

//    Get all creators
    public List<CreatorDto> getAllCreators(){
        Users user = jwtUtil.getLoggedInUser();

        if (!userRolesCheckForAdminAndCreator(user)) {
            throw new UserRoleIncorrectException("Only admins can view all creators");
        }

        List<Users> creators = userRepository.findByUserRoles(UserRoles.CREATOR);

        return creators
                .stream()
                .map(creator -> {
                        Double avgRating = quizRepository.findQuizzesCreatedByUserId(creator.getId())
                                .stream()
                                .mapToDouble(Quiz::getRating)
                                .average()
                                .orElse(0.0);
                        return mapper.toCreatorDto(creator , avgRating);
                }).collect(Collectors.toList());
    }

    /**
     * 3. Get all questions
     */
    public List<QuestionDto> getAllQuestions() {
        Users user = jwtUtil.getLoggedInUser();
        if (!userRolesCheckForAdminAndCreator(user)) {
            throw new UserRoleIncorrectException("User is not authorized to view all questions");
        }

        List<Questions> questions = questionRepository.findAll();
        return questions.stream()
                .map(mapper::toQuestionDto)
                .collect(Collectors.toList());
    }

    /**
     * 4. Get all questions by category (Enhanced version)
     */
    public List<QuestionDto> getAllQuestionsByCategory(String category) {
        Users user = jwtUtil.getLoggedInUser();
        if (!userRolesCheckForAdminAndCreator(user)) {
            throw new UserRoleIncorrectException("User is not authorized to access questions");
        }

        List<Questions> questions = questionRepository.findByCategory(category);
        if (questions.isEmpty()) {
            throw new QuestionDataInvalidException("No questions found for category: " + category);
        }

        return questions.stream()
                .map(mapper::toQuestionDto)
                .collect(Collectors.toList());
    }

    /**
     * 5. Get all questions by category & difficulty
     */
    public List<QuestionDto> getAllQuestionsByCategoryAndDifficulty(String category, String difficultyLevel) {
        Users user = jwtUtil.getLoggedInUser();
        if (!userRolesCheckForAdminAndCreator(user)) {
            throw new UserRoleIncorrectException("User is not authorized to access questions");
        }

        List<Questions> questions = questionRepository.findByCategoryAndDifficultyLevel(category, difficultyLevel);
        if (questions.isEmpty()) {
            throw new QuestionDataInvalidException(
                    "No questions found for category: " + category + " and difficulty: " + difficultyLevel);
        }

        return questions.stream()
                .map(mapper::toQuestionDto)
                .collect(Collectors.toList());
    }

    public List<ResultDto> getResultsOfAllUsersAllTests(){
        Users user = jwtUtil.getLoggedInUser();

        if (!userRolesCheckForAdminAndCreator(user)) {
            throw new UserRoleIncorrectException("Only admins can view all test results");
        }

        List<QuizResult> allResults = quizResultRepository.findAll();
        return allResults.stream().map(mapper :: toResultDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteQuiz(Long quizId){

        Users user = jwtUtil.getLoggedInUser();
        if (!userRolesCheckForAdminAndCreator(user)){
            throw new UserRoleIncorrectException("User is not authorized to delete quizzes");
        }

        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new QuizNotFoundException("Quiz not found with ID: " + quizId));

        if (user.getUserRoles() != UserRoles.ADMIN && !quiz.getCreatedBy().getId().equals(user.getId())){
            throw new UserRoleIncorrectException("You can only delete your own quizzes");
        }

        try{
            List<QuizResult> results = quizResultRepository.findByQuizId(quizId);
            quizResultRepository.deleteAll();

            Users creator = quiz.getCreatedBy();
            creator.setTotalQuizCreated(Math.max(0 , creator.getTotalQuizCreated() - 1));
            userRepository.save(creator);

            quizRepository.delete(quiz);
        }catch (Exception ex){
            throw new QuizProcessingErrorException("Failed to delete quiz: " + ex.getMessage());
        }
    }

    @Transactional
    public void deleteQuestion(Long questionId){
        Users user = jwtUtil.getLoggedInUser();

        if (!userRolesCheckForAdminAndCreator(user)) {
            throw new UserRoleIncorrectException("User is not authorized to delete questions");
        }

        Questions question = questionRepository.findById(questionId).orElseThrow(() -> new QuestionDataInvalidException("Question not found with ID :" + questionId));

        try{
            List<Quiz> associatedQuizzes = question.getQuizzes();
            for (Quiz quiz : associatedQuizzes){
                quiz.getQuestions().remove(question);
                quiz.setTotalQuestions(quiz.getQuestions().size());
                quizRepository.save(quiz);

            }

            questionRepository.delete(question);
        }catch (Exception ex){
            throw new QuestionDataInvalidException("Failed to delete question: " + ex.getMessage());

        }
    }

    // ==================== ADDITIONAL FUNCTIONALITIES (2 per entity) ====================

//    Get user ranking
    public List<UserProfileDto> getUserRankings(){

        Users user = jwtUtil.getLoggedInUser();
        if (!userRolesCheckForAdminAndCreator(user)) {
            throw new UserRoleIncorrectException("User is not authorized to view rankings");
        }

        List<Users> rankedUsers = userRepository.findAllOrderByRankingAsc();

        return rankedUsers.stream()
                .map(mapper::toUserProfileDto)
                .collect(Collectors.toList());
    }

//    Update user ranking system
    @Transactional
    public void updateAllUserRankings(){
        Users user = jwtUtil.getLoggedInUser();

        if (!userRolesCheckForAdminAndCreator(user)) {
            throw new UserRoleIncorrectException("User is not authorized to view rankings");
        }

        List<Users> users  = userRepository.findAllOrderByOverallPercentageDesc();
        for (int i = 0 ; i < users.size() ; i++){
            users.get(i).setRanking(i+1);
        }
        userRepository.saveAll(users);
    }

//    Get top rated quizzes
    public List<QuizStatsDto> getTopRatedQuizzes(int limit){
        List<Quiz> topQuizzes = quizRepository.findAllOrderByRatingDesc()
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
        return topQuizzes.stream().map(mapper::toQuizStatsDto).collect(Collectors.toList());
    }

//    Get quiz statistics by category
    public List<CategoryStatDto> getQuizStatsByCategory(){
        Users user = jwtUtil.getLoggedInUser();

        if (!userRolesCheckForAdminAndCreator(user)) {
            throw new UserRoleIncorrectException("User is not authorized to view statistics");
        }

        List<String> categories = quizRepository.findAllCategories();

        return categories
                .stream()
                .map(this::calculateCategoryStats)
                .collect(Collectors.toList());

    }

//    Get question statics by difficulty
    public Map<String , Long> getQuestionStatsByDifficulty(){
        Users user = jwtUtil.getLoggedInUser();
        if (!userRolesCheckForAdminAndCreator(user)) {
            throw new UserRoleIncorrectException("User is not authorized to view statistics");
        }

        List<String> difficulties = questionRepository.findAllDifficultyLevel();

        return difficulties
                .stream()
                .collect(Collectors.toMap(
                        d -> d ,
                        d -> (long) questionRepository.findByDifficultyLevel(d).size()
                ));
    }

//    Bulk import questions
    @Transactional
    public List<QuestionDto> bulkImportQuestions(List<CreateQuestionDto> questions){
        Users user = jwtUtil.getLoggedInUser();
        if (!userRolesCheckForAdminAndCreator(user)) {
            throw new UserRoleIncorrectException("User is not authorized to import questions");
        }

        List<QuestionDto> importedQuestion = new ArrayList<>();
        for (CreateQuestionDto questionDto : questions){
            try{
                QuestionDto imported = addQuestion(questionDto);
                importedQuestion.add(imported);
            }catch (Exception ex){
                System.err.println("Failed to import question: " + questionDto.getQuestionTitle() +
                        ". Error: " + ex.getMessage());
            }
        }

        return importedQuestion;
    }

//    Get user performance analytics
    public List<UserStatsDto> getUserPerformanceAnalytics(Long userId){

        Users user = jwtUtil.getLoggedInUser();
        if (!userRolesCheckForAdminAndCreator(user) && !user.getId().equals(userId)) {
            throw new UserRoleIncorrectException("User can only view their own analytics");
        }

        List<UserStats> userStat = userStatsRepository.findByUserId(userId);

        return userStat.stream()
                .map(mapper::toUserStatsDto)
                .collect(Collectors.toList());
    }

//    Get quiz performance trends
    public Map<String , Object> getQuizPerformanceTrends(Long quizIds){
        Users user = jwtUtil.getLoggedInUser();
        if (!userRolesCheckForAdminAndCreator(user)) {
            throw new UserRoleIncorrectException("User is not authorized to view performance trends");
        }

        List<QuizResult> results = quizResultRepository.findByQuizIdOrderByScoreDesc(quizIds);
        Map<String , Object> trends = new HashMap<>();

        trends.put("totalAttempts" , results.size());
        trends.put("averageScore" , results.stream().mapToDouble(QuizResult::getScorePercentage).average().orElse(0.0));
        trends.put("highestScore" , results.stream().mapToDouble(QuizResult::getScorePercentage).max().orElse(0.0));
        trends.put("lowestScore" , results.stream().mapToDouble(QuizResult::getScorePercentage).min().orElse(0.0));

        return trends;
    }

//    UserStats Entity Additional Functions
    public Map<String , Object> generateUserReport(Long userId){

        Users currentUser = jwtUtil.getLoggedInUser();
        if (!userRolesCheckForAdminAndCreator(currentUser) && !currentUser.getId().equals(userId)) {
            throw new UserRoleIncorrectException("User can only view their own report");
        }

        Users targetUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Map<String , Object> report = new HashMap<>();
        report.put("userProfile" , mapper.toUserProfileDto(targetUser));
        report.put("categoryStats" , userStatsRepository.findByUserId(userId).stream().map(mapper::toUserDto).collect(Collectors.toList()));
        report.put("recentResults" , quizResultRepository.findByUserIdOrderByCompletedAtDesc(userId).stream().limit(10).map(mapper::toResultDto).collect(Collectors.toList()));

        return report;
    }

//    Get leaderboard by category
    public List<UserStatsDto> getCategoryLeaderboard(String category, int limit) {
        List<UserStats> topPerformers = userStatsRepository.findByCategoryOrderByPercentageDesc(category)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());

        return topPerformers.stream()
                .map(mapper::toUserStatsDto)
                .collect(Collectors.toList());
    }

//    Update user stats after quiz completion
    @Transactional
    private void updateUserStats(Users user , String category , int correctAnswer , int wrongAnswer , double scorePercentage){

        Optional<UserStats> existingStats = userStatsRepository.findByUserIdAndCategory(user.getId() , category);

        UserStats stats;
        if (existingStats.isPresent()){
            stats = existingStats.get();
            stats.setTotalQuizzesTaken(stats.getTotalQuizzesTaken() + 1);
            stats.setTotalCorrectAnswers(stats.getTotalCorrectAnswers() + correctAnswer);
            stats.setTotalWrongAnswers(stats.getTotalWrongAnswers() + wrongAnswer);
        } else {
          stats = new UserStats();
          stats.setUser(user);
          stats.setCategory(category);
          stats.setTotalCorrectAnswers(correctAnswer);
          stats.setTotalWrongAnswers(wrongAnswer);
        }

        int totalQuestions = stats.getTotalCorrectAnswers() + stats.getTotalWrongAnswers();
        if (totalQuestions > 0){
            stats.setCategoryPercentage((double) stats.getTotalCorrectAnswers()/totalQuestions * 100);
        }

        Double avgScore = quizResultRepository.findByUserId(user.getId()).stream()
                .filter(result -> result.getQuiz().getCategory().equalsIgnoreCase(category))
                .mapToDouble(QuizResult::getScorePercentage)
                .average()
                .orElse(0.0);

        stats.setAverageScore(avgScore);
        userStatsRepository.save(stats);
    }

//    Update quiz average score
    private void updateQuizAverageScore(Quiz quiz){
        Double averageScore = quizResultRepository.findAverageScoreByQuizId(quiz.getId());
        quiz.setAverageScore(averageScore != null ? averageScore : 0.0);
        quizRepository.save(quiz);
    }

//    Update user overall percentage
    @Transactional
    private void updateUserOverallPercentage(Users user){
        Double overallPercentage = quizResultRepository.findAverageScoreByUserId(user.getId());
        user.setOverallPercentage(overallPercentage != null ? overallPercentage : 0.0);
        userRepository.save(user);
    }

//
    private CategoryStatsDto calculateCategoryStats(String category){
        Integer totalQuestions = questionRepository.findByCategory(category).size();
        Integer totalQuizzes = quizRepository.findByCategory(category).size();

        List<Quiz> categoryQuizzes = quizRepository.findByCategory(category);
        Integer totalParticipants = categoryQuizzes.stream().mapToInt(q -> q.getParticipants().size()).sum();

        Double averageScore = categoryQuizzes.stream().mapToDouble(Quiz::getAverageScore).average().orElse(0.0);

        return mapper.toCategoryStatsDto(category , totalQuestions , totalQuizzes , totalParticipants , averageScore);
    }

// ==================== EXISTING HELPER FUNCTIONS (IMPROVED) ====================

    public List<String> getQuestionTitlesOfQuiz(Quiz quiz) {
        if (quiz.getQuestions() == null || quiz.getQuestions().isEmpty()) {
            return Collections.emptyList();
        }
        return quiz.getQuestions().stream()
                .map(Questions::getQuestionTitle)
                .collect(Collectors.toList());
    }

    public List<Long> getQuestionIdsOfQuiz(Quiz quiz) {
        if (quiz.getQuestions() == null) return Collections.emptyList();
        return quiz.getQuestions().stream()
                .map(Questions::getId)
                .collect(Collectors.toList());
    }


    // ==================== FRONTEND HELPER FUNCTIONS ====================

    public QuizDto getQuizByQuizTitle(String quizTitle) {
        Quiz quiz = quizRepository.findByTitle(quizTitle)
                .orElseThrow(() -> new QuizNotFoundException("Quiz with title " + quizTitle + " was not found"));
        return mapper.toQuizDto(quiz);
    }

    public List<String> getAllQuizTitles() {
        return quizRepository.findAll().stream()
                .map(Quiz::getTitle)
                .collect(Collectors.toList());
    }

    public int getQuestionCountByCategory(String category) {
        List<Questions> questions = questionRepository.findByCategory(category);
        return questions.size();
    }

    public int getQuestionByCategoryAndDifficulty(String category, String difficultyLevel) {
        return questionRepository.countByCategoryAndDifficultyLevel(category, difficultyLevel).intValue();
    }

    public List<String> getAllCategories() {
        return questionRepository.findAllCategories();
    }

    public UsersDto getUserDetails() {
        return mapper.mapFromUserToUserDTO(jwtUtil.getLoggedInUser());
    }



// =====================================================================================================================================================================================================
//    Helper Functions

    public List<Questions> selectRandomQuestions(List<Questions> availableQuestions, int noOfQuestions) {
        List<Questions> questionsCopy = new ArrayList<>(availableQuestions);
        List<Questions> selectedQuestions = new ArrayList<>();

        Random random = new Random();

        for (int i = 0; i < noOfQuestions; i++) {
            int randomIndex = random.nextInt(questionsCopy.size());
            selectedQuestions.add(questionsCopy.get(randomIndex));
            questionsCopy.remove(randomIndex);
        }
        return selectedQuestions;
    }

//    public List<String> getQuestionTitlesOfQuiz(Quiz quiz) {
//        if (quiz.getQuestions() == null || quiz.getQuestions().isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        return quiz.getQuestions().stream()
//                .map(Questions::getQuestionTitle)
//                .collect(Collectors.toList());
//    }
//
//    public List<Long> getQuestionIdsOfQuiz(Quiz quiz) {
//        if (quiz.getQuestions() == null) return Collections.emptyList();
//
//        return quiz.getQuestions().stream()
//                .map(Questions::getId)
//                .collect(Collectors.toList());
//    }

    public List<String> getQuizTitlesByCreatorName(String creatorName){
        List<Quiz> quizzes = quizRepository.findAllQuizCreatedByUser(creatorName);

        return quizzes.stream().map(Quiz :: getTitle).collect(Collectors.toList());
    }

    public List<String> getParticipantNameByQuizTitle(String quizTitle){
        Optional<Quiz> quizOptional = quizRepository.findQuizByTitle(quizTitle);

        if (quizOptional.isEmpty() || quizOptional.get().getParticipants() == null){
            return Collections.emptyList();
        }

        return quizOptional.get().getParticipants().stream().map(Users::getName).collect(Collectors.toList());
    }

    public List<Long> getParticipantIdsByQuizTitle(String quizTitle){
        Optional<Quiz> quizOptional = quizRepository.findQuizByTitle(quizTitle);

        if (quizOptional.isEmpty() || quizOptional.get().getParticipants() == null){
            return Collections.emptyList();
        }

        return quizOptional.get().getParticipants().stream().map(Users::getId).collect(Collectors.toList());
    }

    public Map<Long , String> getRightAnswerByQuizTitle(String title){
        return quizRepository.findQuizByTitle(title)
                .map(quiz -> quiz
                        .getQuestions()
                        .stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(Questions :: getId , Questions :: getRightAnswer)))
                .orElse(Collections.emptyMap());
    }

    public Map<Long , String> getSelectedAnswerByUserAndQuizTitle(String userName , String quizTitle){
        List<Responses> responses = responseRepository.findResponseByUserAndQuizTitle(userName , quizTitle);
        Map<Long , String> selectedMap = new LinkedHashMap<>();
        for (Responses response : responses){
            List<Long> questionIds = response.getQuestionId();
            List<String> selectedAnswers = response.getSelectedAnswer();

            for (int i = 0 ; i < questionIds.size() ; i++){
                selectedMap.put(questionIds.get(i) , selectedAnswers.get(i));
            }
        }

        return selectedMap;
    }

    public List<String> getAnsweredQuestionTitleFromQuiz(String userName , String quizTitle){
        Optional<Quiz> quizOpt = quizRepository.findQuizByTitle(quizTitle);
        if (quizOpt.isEmpty()) return Collections.emptyList();

        Quiz quiz = quizOpt.get();

        List<Long> quizQuestionIds = quiz.getQuestions().stream().map(Questions::getId).toList();

        List<Responses> responses = responseRepository.findResponseByUserAndQuizTitle(userName, quizTitle);

        Set<Long> answerQuestions = responses.stream()
                .flatMap(r -> r.getQuestionId().stream())
                .filter(quizQuestionIds::contains)
                .collect(Collectors.toSet());

        return quiz.getQuestions()
                .stream()
                .filter(q -> answerQuestions.contains(q.getId()))
                .map(Questions::getQuestionTitle)
                .collect(Collectors.toList());
    }

    public List<QuestionDto> getAnsweredQuestionDtoFromQuiz(String userName, String quizTitle) {
        Optional<Quiz> quizOpt = quizRepository.findQuizByTitle(quizTitle);
        if (quizOpt.isEmpty()) return Collections.emptyList();

        Quiz quiz = quizOpt.get();

        List<Long> quizQuestionIds = quiz.getQuestions().stream()
                .map(Questions::getId)
                .collect(Collectors.toList());

        List<Responses> responses = responseRepository.findResponseByUserAndQuizTitle(userName, quizTitle);

        Set<Long> answerQuestionIds = responses.stream()
                .flatMap(r -> r.getQuestionId().stream())
                .filter(quizQuestionIds :: contains)
                .collect(Collectors.toSet());

        return quiz.getQuestions().stream()
                .filter(q -> answerQuestionIds.contains(q.getId()))
                .map(q ->mapper.toQuestionDto(q))
                .collect(Collectors.toList());
    }

    public Map<String , Integer> evaluateUserAnswers(String quizTitle , String userName){
        Optional<Quiz> quizOptional = quizRepository.findQuizByTitle(quizTitle);

        if (quizOptional.isEmpty()) throw new QuizNotFoundException("Quiz With This Title :" + quizTitle + "Not Found");

        Quiz quiz = quizOptional.get();

        List<Responses> responses = responseRepository.findResponseByUserAndQuizTitle(quizTitle, userName);
        if (responses.isEmpty()) {
            throw new RuntimeException("No responses found for user: " + userName + " on quiz: " + quizTitle);
        }

        List<Long> userQuestionIds = new ArrayList<>();
        List<String> userSelectedAnswers = new ArrayList<>();
        for (Responses response : responses){
            userQuestionIds.addAll(response.getQuestionId());
            userSelectedAnswers.addAll(response.getSelectedAnswer());
        }

        Map<Long , String> correctAnswerMap = quiz
                .getQuestions()
                .stream()
                .collect(Collectors.toMap(Questions::getId , Questions::getRightAnswer));

        int correct = 0;
        int wrong = 0;
        int unanswered = 0;

        for (int i = 0 ; i < userQuestionIds.size() ; i++){
            Long qId = userQuestionIds.get(i);
            String userAnswer = i < userSelectedAnswers.size() ? userSelectedAnswers.get(i) : null;
            String correctAnswer = correctAnswerMap.get(qId);

            if (userAnswer == null || userAnswer.trim().isEmpty()){
                unanswered++;
            } else if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                correct++;
            }else {
                wrong++;
            }
        }

        Map<String , Integer> result = new HashMap<>();
        result.put("correct" , correct);
        result.put("wrong" , wrong);
        result.put("unanswered" , unanswered);

        return result;

        // Accessing the values from the set
//int correctAnswers = result.getOrDefault("correct", 0);
//int wrongAnswers = result.getOrDefault("wrong", 0);
//int unanswered = result.getOrDefault("unanswered", 0);
    }

    public boolean userRolesCheckForAdminAndCreator(Users users){
        return users.getUserRoles().equals(UserRoles.ADMIN) || users.getUserRoles().equals(UserRoles.CREATOR);
    }

    public boolean userRolesCheckForAdminAndParticipant(Users users){
        return users.getUserRoles().equals(UserRoles.ADMIN) || users.getUserRoles().equals(UserRoles.CREATOR);
    }
}
