package com.example.QuizApplicationImplemented.service.admin;

import com.example.QuizApplicationImplemented.dto.applicationDTO.*;
import com.example.QuizApplicationImplemented.dto.authenticationDTO.UsersDto;
import com.example.QuizApplicationImplemented.entity.Questions;
import com.example.QuizApplicationImplemented.entity.Quiz;
import com.example.QuizApplicationImplemented.entity.Responses;
import com.example.QuizApplicationImplemented.entity.Users;
import com.example.QuizApplicationImplemented.enums.UserRoles;
import com.example.QuizApplicationImplemented.exceptions.QuestionException.QuestionCreationException;
import com.example.QuizApplicationImplemented.exceptions.QuestionException.QuestionDataInvalidException;
import com.example.QuizApplicationImplemented.exceptions.QuestionNotCreatedException;
import com.example.QuizApplicationImplemented.exceptions.QuizExcetion.QuizNotFoundException;
import com.example.QuizApplicationImplemented.exceptions.QuizExcetion.QuizProcessingErrorException;
import com.example.QuizApplicationImplemented.exceptions.ResponseNotReceivedException;
import com.example.QuizApplicationImplemented.exceptions.UserException.UserRoleIncorrectException;
import com.example.QuizApplicationImplemented.mapper.Mapper;
import com.example.QuizApplicationImplemented.repository.QuestionRepository;
import com.example.QuizApplicationImplemented.repository.QuizRepository;
import com.example.QuizApplicationImplemented.repository.ResponseRepository;
import com.example.QuizApplicationImplemented.repository.UserRepository;
import com.example.QuizApplicationImplemented.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.apache.catalina.User;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
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
    private Mapper mapper;

    @Autowired
    private JwtUtil jwtUtil;

//    Actual Functions

//    Adding a question (ADMIN OR CREATOR)
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

//    Getting the Creating Quiz (ADMIN OR CREATOR)
    @Transactional
    public QuizDto createQuiz(CreateQuizDto dto){
        Users creator = jwtUtil.getLoggedInUser();
        if (creator == null) {
            throw new RuntimeException("Unauthorized or expired token.");
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(dto.getQuizTitle());
        quiz.setCategory(dto.getCategory());
        quiz.setDifficultyLevel(dto.getDifficultyLevel());
        quiz.setCreatedBy(creator);

        // Save Quiz to generate ID
        quiz = quizRepository.saveAndFlush(quiz);

        List<Questions> availableQuestions = questionRepository.findByCategoryAndDifficultyLevel(
                dto.getCategory(), dto.getDifficultyLevel());

        if (availableQuestions.size() < dto.getNoOfQuestions()) {
            throw new IllegalArgumentException("Not enough questions available");
        }

        Collections.shuffle(availableQuestions);
        List<Questions> selectedQuestions = availableQuestions.subList(0, dto.getNoOfQuestions());

        // 🚨 Use only owning side
        quiz.setQuestions(selectedQuestions);

        // Save again with questions
        quiz = quizRepository.saveAndFlush(quiz);

        return mapper.toQuizDto(quiz);
    }

//  Getting the Created Quiz (ADMIN OR CREATOR)
public CreatedQuizDto getCreatedQuiz(String quizTitle) {
    Users users = jwtUtil.getLoggedInUser();
    if (userRolesCheckForAdminAndCreator(users)){
        try{
            Optional<Quiz> optionalQuiz = quizRepository.findQuizByTitle(quizTitle);
            Quiz quiz = optionalQuiz.get();

            List<Questions> questionsList = quiz.getQuestions();

            CreatedQuizDto createdQuizDto = mapper.toCreatedQuizDto(quiz, questionsList);

            if (quiz.getCreatedBy() != null && quiz.getCreatedBy().getName() != null) {
                createdQuizDto.setCreatorName(quiz.getCreatedBy().getName());
            } else {
                createdQuizDto.setCreatorName("Unknown");
            }

            return createdQuizDto;
        }catch (QuizProcessingErrorException ex){
            throw new QuizProcessingErrorException("Quiz Was Not Created");
        }
    }else
        throw new UserRoleIncorrectException("User Is Not Authorized To Do This");
}

// Getting all questions of a category (ADMIN OR CREATOR)
    public List<QuestionDto> getAllQuestionByCategory(String category){
        Users users = jwtUtil.getLoggedInUser();
        if (userRolesCheckForAdminAndCreator(users)){
            try{
                List<Questions> questionList = questionRepository.findByCategory(category);
                List<QuestionDto> questionDtoList = new ArrayList<>();
                for (Questions question : questionList){
                    questionDtoList.add(mapper.toQuestionDto(question));
                }
                return questionDtoList;
            }catch (QuestionDataInvalidException ex){
                throw new QuestionDataInvalidException("Question With Specified Category Were Not Found");
            }
        }else
            throw new UserRoleIncorrectException("User Is Not Authorized To Do This");
    }

//    Getting all the question of a quiz (ADMIN OR CREATOR)
    public CreatorUserDto getAllQuestionsOfQuiz(String quizTitle){
        Users users = jwtUtil.getLoggedInUser();
        if (userRolesCheckForAdminAndCreator(users)){
            try{
                Quiz quiz = quizRepository.findQuizByTitle(quizTitle).orElseThrow(() -> new QuizNotFoundException("Quiz With This Title Was Not Found"));
                CreatorUserDto userDTO = new CreatorUserDto();
                if (quiz.getCreatedBy().getUsername().equalsIgnoreCase(users.getUsername())){
                    if (users.getName() != null) {
                        userDTO.setCreatorName(users.getName());
                    }else{
                        userDTO.setCreatorName("Admin");
                    }
                    if (users.getUserRoles()==UserRoles.CREATOR) {
                        userDTO.setUserRoles(UserRoles.CREATOR);
                    }else{
                        userDTO.setUserRoles(UserRoles.ADMIN);
                    }

                    userDTO.setQuestionTitleList(getQuestionTitlesOfQuiz(quiz));
                }
                return userDTO;
            }catch(QuizProcessingErrorException ex){
                throw new QuizProcessingErrorException("Quiz  With The Given Title Was Not Processed");
            }
        }else
            throw new UserRoleIncorrectException("User Is Not Authorized To Do This");
    }

//    Getting all quiz by creator name (ADMIN OR CREATOR)
    public List<QuizDto> getAllTheQuizByCreator(){
        Users users = jwtUtil.getLoggedInUser();
        if (userRolesCheckForAdminAndCreator(users)){
            try{
                List<Long> quizIdList = getQuizIdsByCreatorName(users.getUsername());
                List<QuizDto> quizDtoList = new ArrayList<>();
                for (Long quizId : quizIdList){
                    Quiz quiz = quizRepository.findQuizById(quizId).orElseThrow(() -> new QuizNotFoundException("Quiz With This Title Was Not Found"));
                    quizDtoList.add(mapper.toQuizDto(quiz));
                }
                return quizDtoList;
            }catch (QuizProcessingErrorException ex){
                throw new QuizProcessingErrorException("Quiz  With The Given Title Was Not Processed");
            }
        }else
            throw new UserRoleIncorrectException("User Is Not Authorized To Do This");
    }

//      Getting all quiz (ADMIN & CREATOR)
    public List<QuizDto> getAllQuiz(){
        Users users = jwtUtil.getLoggedInUser();
        if (userRolesCheckForAdminAndCreator(users)) {
            try {
                List<Quiz> quizList = quizRepository.findAll();
                List<QuizDto> quizDtoList = new ArrayList<>();
                for (Quiz quiz : quizList) {
                    quizDtoList.add(mapper.toQuizDto(quiz));
                }

                return quizDtoList;
            }catch (QuizProcessingErrorException ex){
                throw new QuizProcessingErrorException("Quiz were not collected");
            }
        }
        else
            throw new UserRoleIncorrectException("User Is Not Authorized To Do This");
    }

//    Getting a created quiz (ADMIN or PARTICIPANT)
    public List<QuestionWrapper> gettingCreatedQuizForParticipant(String quizTitle){
        Users users = jwtUtil.getLoggedInUser();
        if (userRolesCheckForAdminAndParticipant(users)){
            try{
                Quiz quiz = quizRepository.findQuizByTitle(quizTitle).orElseThrow(() -> new QuizNotFoundException("Quiz With This Title Was Not Found"));
                List<Questions> quizQuestions = quiz.getQuestions();
                List<QuestionWrapper> wrapper = new ArrayList<>();
                for (Questions quizQuestion : quizQuestions){
                    wrapper.add(mapper.toQuestionWrapper(quizQuestion));
                }

//                Adding the participant to quiz
//                List<Users> currentParticipants = quiz.getParticipants();
//                if (currentParticipants == null){
//                    currentParticipants = new ArrayList<>();
//                    quiz.setParticipants(currentParticipants);
//                }

                boolean userAlreadyParticipated = quiz.getParticipants().stream()
                        .anyMatch(participants -> participants != null && participants.getId() != null && participants.getId().equals(users.getId()));
                if (userAlreadyParticipated){
                    throw new QuizProcessingErrorException("User has already taken the quiz");
                }

//                currentParticipants.add(users);
//                quiz.setParticipants(currentParticipants);
                quiz.addParticipant(users);
                quizRepository.saveAndFlush(quiz);

//                quizRepository.save(quiz);
                return wrapper;
            }catch(QuizProcessingErrorException ex){
                throw new QuizProcessingErrorException("Quiz  With The Given Title Was Not Processed");
            }
        }else
            throw new UserRoleIncorrectException("User Is Not Authorized To Do This");
    }


//    Getting the response from the user after taking quiz and storing it (ADMIN or PARTICIPANT)
public List<ResponseEvaluationDto> savingResponseResponse(QuizTakenResponse response){
    Users user = jwtUtil.getLoggedInUser();
    if (!userRolesCheckForAdminAndParticipant(user)){
        throw new UserRoleIncorrectException("User is not authorized to do this");
    }

    try{
        Quiz quiz = quizRepository.findQuizByTitle(response.getQuizTitle()).orElseThrow(() -> new QuizNotFoundException("Quiz with this title was not found"));

        boolean userIsParticipant = quiz.getParticipants().stream().anyMatch(p -> p != null && p.getId().equals(user.getId()));
        if (!userIsParticipant){
            throw new ResponseNotReceivedException("User is not a participant of the quiz");
        }

        Responses responses = mapper.toResponseEntity(response , quiz , user);
        responseRepository.save(responses);

        Map<Long , String> correctAnswers = getRightAnswerByQuizTitle(response.getQuizTitle());
        Map<Long , String> userAnswer = getSelectedAnswerByUserAndQuizTitle(user.getUsername(), response.getQuizTitle());
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

        List<Quiz> quizTakenTitle = quizRepository.findQuizzesByParticipantName(users.getName());
        List<String> quizTitleList = new ArrayList<>();

        for (Quiz list : quizTakenTitle){
            quizTitleList.add(list.getTitle());
        }
        return quizTitleList;
    }

// Getting proper result for the LoggedIn User (ADMIN or PARTICIPANT);
    public ResultDto getResultForAUser(String quizTitle){
        Users user = jwtUtil.getLoggedInUser();
        if (!userRolesCheckForAdminAndParticipant(user)){
            throw new UserRoleIncorrectException("User is not authorized to do this");
        }
        Optional<Quiz> quizOptional = quizRepository.findQuizByTitle(quizTitle);
        if (quizOptional.isEmpty()) {
            throw new QuizNotFoundException("Quiz with title '" + quizTitle + "' not found");
        }

        Quiz quiz = quizOptional.get();
        boolean isParticipant = quiz.getParticipants().stream().anyMatch(p -> p.getId().equals(user.getId()));

        if (!isParticipant){
            throw new RuntimeException("User did not participate in this quiz");
        }

        Map<String , Integer> evaluation = evaluateUserAnswers(quizTitle , user.getUsername());

        int correct = evaluation.getOrDefault("correct" , 0);
        int wrong = evaluation.getOrDefault("wrong" , 0);
        int unanswered = evaluation.getOrDefault("unanswered" , 0);

        int totalQuestions = quiz.getQuestions() != null ? quiz.getQuestions().size() : 0;
        double percentage = totalQuestions == 0 ? 0.0 : ((double) correct/totalQuestions) * 100;

        ResultDto result = new ResultDto();
        result.setQuizId(quiz.getId());
        result.setQuizTitle(quiz.getTitle());
        result.setUserId(user.getId());
        result.setUserName(user.getName());
        result.setTotalQuestions(totalQuestions);
        result.setCorrectAnswer(correct);
        result.setIncorrectAnswer(wrong + unanswered);
        result.setPercentage(percentage);

        return result;
    }

//    Getting the result of all the participants that have taken the quiz (ADMIN or CREATOR)
    public List<ResultDto> getResultsForAllUsers(String quizTitle){

        Users user = jwtUtil.getLoggedInUser();
        if (!userRolesCheckForAdminAndCreator(user)){
            throw new UserRoleIncorrectException("User is not authorized to do this");
        }

        Quiz quiz = quizRepository.findQuizByTitle(quizTitle)
                .orElseThrow(() -> new QuizNotFoundException("Quiz with title '" + quizTitle + "' not found"));

        List<Responses> allResponses = responseRepository.findByQuizId(quiz.getId());

        if (allResponses.isEmpty()){
            throw new RuntimeException("No participant has attempted the quiz yet.");
        }

        Map<Long , String> correctAnswerMap = quiz.getQuestions().stream()
                .collect(Collectors.toMap(Questions::getId , Questions::getRightAnswer));

        int totalQuestions = correctAnswerMap.size();

        List<ResultDto> resultList = new ArrayList<>();
        for (Responses responses : allResponses){
            Users users = responses.getUser();
            List<Long> questionIds = responses.getQuestionId();
            List<String> selectedAnswers = responses.getSelectedAnswer();
            int correct = 0;
            int incorrect = 0;

            for (int i = 0 ; i < questionIds.size() ; i++) {
                Long qId = questionIds.get(i);
                String selected = selectedAnswers.size() > i ? selectedAnswers.get(i) : null;
                String correctAnswer = correctAnswerMap.get(qId);

                if (selected != null && selected.equalsIgnoreCase(correctAnswer)) {
                    correct++;
                } else {
                    incorrect++;
                }
            }
                double percentage = totalQuestions == 0 ? 0.0 : ((double) correct / totalQuestions) * 100;

                ResultDto result = new ResultDto();
                result.setQuizId(quiz.getId());
                result.setQuizTitle(quiz.getTitle());
                result.setUserId(user.getId());
                result.setUserName(user.getName());
                result.setTotalQuestions(totalQuestions);
                result.setCorrectAnswer(correct);
                result.setIncorrectAnswer(incorrect);
                result.setPercentage(percentage);

                resultList.add(result);
            }
            return resultList;
    }

// Frontend Helper Function
    public QuizDto getQuizByQuizTitle(String quizTitle){
        Quiz quiz = quizRepository.findQuizByTitle(quizTitle).orElseThrow(() -> new RuntimeException("Quiz with title " +quizTitle+ "was not found"));

        return mapper.toQuizDto(quiz);
    }

    public List<String> getAllQuizTitles(){
        List<String> quizTitles = quizRepository.findAll().stream().map(Quiz::getTitle).toList();

        return quizTitles;
    }


    public int getQuestionCountByCategory(String category){
        List<Questions> questions = questionRepository.findByCategory(category);
        return questions.size();
    }

    public int getQuestionByCategoryAndDifficulty(String category , String difficultyLevel){
        return questionRepository.countByCategoryAndDifficulty(category, difficultyLevel);
    }

    public List<String> getAllCategories(){
        List<Questions> allQuestions = questionRepository.findAll();
        return allQuestions.stream()
                .map(Questions::getCategory)
                .filter(category -> category != null && !category.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public UsersDto getUserDetails(){
        return mapper.mapFromUserToUserDTO(jwtUtil.getLoggedInUser());
    }

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

    public List<Long> getQuizIdsByCreatorName(String creatorName){
        List<Quiz> quizzes = quizRepository.findAllQuizCreatedByUser(creatorName);

        return quizzes.stream().map(Quiz :: getId).collect(Collectors.toList());
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
