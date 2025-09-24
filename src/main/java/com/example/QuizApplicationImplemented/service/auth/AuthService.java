package com.example.QuizApplicationImplemented.service.auth;


import com.example.QuizApplicationImplemented.dto.authenticationDTO.SignUpRequest;
import com.example.QuizApplicationImplemented.dto.authenticationDTO.UsersDto;
import com.example.QuizApplicationImplemented.entity.Users;
import com.example.QuizApplicationImplemented.enums.UserRoles;
import com.example.QuizApplicationImplemented.mapper.Mapper;
import com.example.QuizApplicationImplemented.repository.UserRepository;
import com.example.QuizApplicationImplemented.security.CusUserDetailService;
import com.example.QuizApplicationImplemented.security.JwtUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CusUserDetailService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MailService mailService;

    @Autowired
    private Mapper mapper;

//    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

//    @PostConstruct
//    public void createAdmin(){
//
//        Optional<Users> optionalUsers = userRepository.findByUserRoles(UserRoles.ADMIN);
//
//        if (optionalUsers.isEmpty()){
//
//            Users users = new Users();
//            users.setUsername("admin@test.com");
//            users.setPassword(new BCryptPasswordEncoder().encode("admin"));
//            users.setUserRoles(UserRoles.ADMIN);
//            users.setAge(100);
//
//            userRepository.save(users);
//
//            System.out.println("Admin is created successfully");
//        }
//        else {
//            System.out.println("Admin already created");
//        }
//    }
//
//
//    public UsersDto signupUser(SignUpRequest signUpRequest){
//
//        Users users = new Users();
//
//        users.setName(signUpRequest.getName());
//        users.setUsername(signUpRequest.getUsername());
//        users.setAge(signUpRequest.getAge());
//        users.setPassword(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()));
//        if (signUpRequest.getRoleNumber()==1){
//            users.setUserRoles(UserRoles.CREATOR);
//        }else if (signUpRequest.getRoleNumber()==2){
//            users.setUserRoles(UserRoles.PARTICIPANT);
//        }
//
//        Users createdUser = userRepository.save(users);
//
//        return mapper.mapFromUserToUserDTO(createdUser);
//    }
//
//    public boolean hasUserWithEmail(String email){return userRepository.findByUsername(email).isPresent();}
//
//
//    public String sendResetToken(String email){
//
//        Users users = userRepository.findByUsername(email).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
//
//        String resetToken = jwtUtil.generateToken(email);
//
//        users.setResetToken(resetToken);
//        userRepository.save(users);
//
//        String resetLink = "http://localhost:4200/reset-password?token="+resetToken;
//        mailService.sendEmail(email , "Password Reset" , "Click the link to reset the password : " +  resetLink);
//
//        return resetToken;
//    }
//
//    public boolean validateResetToken(String token , String email){
//
//        String extractedEmail = jwtUtil.extractUsername(token);
//
//        if (!extractedEmail.equals(email)){
//            return false;
//        }
//
//        UserDetails userDetails = userService.loadUserByUsername(email);
//
//        return jwtUtil.isTokenValid(token,userDetails);
//    }
//
//    public String resetPassword(String email , String token , String newPassword){
//
//        if (!validateResetToken(token, email)){
//            throw new IllegalArgumentException("Invalid or expired token");
//        }
//
//        Users users = userRepository.findByUsername(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        users.setPassword(new BCryptPasswordEncoder().encode(newPassword));
//        userRepository.save(users);
//
//        return "Password reset successfully";
//    }
//
//    public void uploadProfilePhoto(String userName, MultipartFile file) throws IOException {
//        Users user = userRepository.findByUsername(userName).orElseThrow(() -> new RuntimeException("User not found with Name: " + userName));
//
//        if (file != null && !file.isEmpty()){
//            user.setProfilePhoto(file.getBytes());
//            userRepository.save(user);
//        }else{
//            throw new RuntimeException("File is empty or null");
//        }
//    }
//
//    public byte[] getProfilePhoto(String userName){
//
//        Users user = userRepository.findByName(userName).orElseThrow(() -> new RuntimeException("User not found with Name: " + userName));
//
//        return user.getProfilePhoto();
//    }

    private final List<AdminConfig> adminConfigs = Arrays.asList(
            new AdminConfig("admin@test.com" , "admin"),
            new AdminConfig("admin1@test.com" , "admin1"),
            new AdminConfig("admin1@test.com" , "admin1")
    );

    private static class AdminConfig{
        final String email;
        final String password;

        AdminConfig(String email , String password){
            this.email = email;
            this.password = password;
        }
    }

    @PostConstruct
    public void createDefaultAdmins(){
        adminConfigs.forEach(config -> {
            Optional<Users> existingAdmin = userRepository.findByUserRolesAndUsername(config.email,UserRoles.ADMIN);

            if (existingAdmin.isEmpty()){
                Users admin = new Users();
                admin.setUsername(config.email);
                admin.setName("Administrator");
                admin.setPassword(new BCryptPasswordEncoder().encode(config.password));
                admin.setUserRoles(UserRoles.ADMIN);
                admin.setAge(25);

                userRepository.save(admin);
                System.out.println("Admin Created : " + config.email);
            }
        });
    }

    public UsersDto signupUser(SignUpRequest signUpRequest) {
        // Validate input
        if (signUpRequest.getUsername() == null || signUpRequest.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (signUpRequest.getPassword() == null || signUpRequest.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        Users user = new Users();
        user.setName(signUpRequest.getName());
        user.setUsername(signUpRequest.getUsername().toLowerCase().trim());
        user.setAge(signUpRequest.getAge());
        user.setPassword(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()));

        // Set user role based on roleNumber
        switch (signUpRequest.getRoleNumber()) {
            case 1 -> user.setUserRoles(UserRoles.CREATOR);
            case 2 -> user.setUserRoles(UserRoles.PARTICIPANT);
            default -> throw new IllegalArgumentException("Invalid role number");
        }

        Users createdUser = userRepository.save(user);
        return mapper.mapFromUserToUserDTO(createdUser);
    }

    public boolean hasUserWithEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return userRepository.findByUsername(email.toLowerCase().trim()).isPresent();
    }

    public String sendRequestToken(String email){
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        Users user = userRepository.findByUsername(email.toLowerCase().trim()).orElseThrow(() -> new UsernameNotFoundException("No user found with email: " + email));
        String resetToken = UUID.randomUUID().toString() + " " + System.currentTimeMillis();

        user.setResetToken(resetToken);
        userRepository.save(user);

        String resetLink = "http://localhost:4200/reset-password?token=" + resetToken+ "&email = " + email;


        String emailBody = String.format("""
            Hello %s,
            
            You have requested to reset your password. Please click the link below to reset your password:
            
            %s
            
            This link will expire in 1 hour for security reasons.
            
            If you did not request this password reset, please ignore this email.
            
            Best regards,
            Quiz Application Team
            """, user.getName() != null ? user.getName() : "User", resetLink);

        mailService.sendEmail(email, "Password Reset Request", emailBody);

        return "Password reset email sent successfully";

    }

    public boolean validateResetToken(String token , String email){
        if (token == null || email == null){
            return  false;
        }

        Users user = userRepository.findByUsername(email.toLowerCase().trim()).orElse(null);

        if (user == null || user.getResetToken() == null){
            return false;
        }

        try{
            String[] parts = token.split("_");
            if (parts.length >= 2){
                long tokenTime = Long.parseLong(parts[parts.length - 1]);
                long currentTime = System.currentTimeMillis();
                long hourInMs = 60 * 60 * 1000;

                return (currentTime -tokenTime) < hourInMs;
            }
        }catch (NumberFormatException e) {
            return false;
        }
        return false;
    }

    public String resetPassword(String email, String token, String newPassword) {
        // Validate inputs
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters long");
        }

        if (!validateResetToken(token, email)) {
            throw new IllegalArgumentException("Invalid or expired reset token");
        }

        Users user = userRepository.findByUsername(email.toLowerCase().trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Update password and clear reset token
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        user.setResetToken(null); // Clear the token after use
        userRepository.save(user);

        // Send confirmation email
        String confirmationBody = String.format("""
            Hello %s,
            
            Your password has been successfully reset.
            
            If you did not make this change, please contact our support team immediately.
            
            Best regards,
            Quiz Application Team
            """, user.getName() != null ? user.getName() : "User");

        mailService.sendEmail(email, "Password Reset Confirmation", confirmationBody);

        return "Password reset successfully";
    }

    public void uploadProfilePhoto(String userName, MultipartFile file) throws IOException {
        if (userName == null || userName.trim().isEmpty()) {
            throw new RuntimeException("Username cannot be empty");
        }

        Users user = userRepository.findByUsername(userName.toLowerCase().trim())
                .orElseThrow(() -> new RuntimeException("User not found with username: " + userName));

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty or null");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        // Validate file size (5MB limit)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("File size must be less than 5MB");
        }

        try {
            // Store image as byte array
            user.setProfilePhoto(file.getBytes());
            userRepository.save(user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process image file", e);
        }
    }

    public byte[] getProfilePhoto(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new RuntimeException("Username cannot be empty");
        }

        // First try to find by username, then by name
        Optional<Users> userOpt = userRepository.findByUsername(userName.toLowerCase().trim());
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByName(userName);
        }

        Users user = userOpt.orElseThrow(() ->
                new RuntimeException("User not found with identifier: " + userName));

        byte[] photo = user.getProfilePhoto();
        if (photo == null || photo.length == 0) {
            return null; // No photo available
        }

        return photo;
    }

    public UsersDto getUserProfile(String username) {
        Users user = userRepository.findByUsername(username.toLowerCase().trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return mapper.mapFromUserToUserDTO(user);
    }

    public void updateUserProfile(String username, UsersDto userDto) {
        Users user = userRepository.findByUsername(username.toLowerCase().trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (userDto.getName() != null && !userDto.getName().trim().isEmpty()) {
            user.setName(userDto.getName().trim());
        }

        if (userDto.getAge() > 0) {
            user.setAge(userDto.getAge());
        }

        userRepository.save(user);
    }


}
