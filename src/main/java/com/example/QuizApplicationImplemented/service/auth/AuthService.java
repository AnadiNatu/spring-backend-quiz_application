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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Value("${file.user-upload-dir}")
    private String uploadDir;

    @PostConstruct
    public void createAdmin(){

        Optional<Users> optionalUsers = userRepository.findAdminsByRoleAndUsername(UserRoles.ADMIN , "admin@test.com");

        if (optionalUsers.isEmpty()) {

            Users users = new Users();
            users.setUsername("admin@test.com");
            users.setPassword(new BCryptPasswordEncoder().encode("admin"));
            users.setUserRoles(UserRoles.ADMIN);
            users.setAge(100);
            users.setProfilePhoto(null);

            userRepository.save(users);

            System.out.println("Admin is created successfully");
        }else {
            System.out.println("Admin already created");
        }
    }

    @PostConstruct
    public void createAdmin1(){

        Optional<Users> optionalUsers = userRepository.findAdminsByRoleAndUsername(UserRoles.ADMIN , "admin1@test.com");

        if (optionalUsers.isEmpty()) {

            Users users = new Users();
            users.setUsername("admin1@test.com");
            users.setPassword(new BCryptPasswordEncoder().encode("admin1"));
            users.setUserRoles(UserRoles.ADMIN);
            users.setAge(100);
            users.setProfilePhoto(null);

            userRepository.save(users);

            System.out.println("Admin 1 is created successfully");
        }else {
            System.out.println("Admin 1 already created");
        }
    }

    @PostConstruct
    public void createAdmin2(){

        Optional<Users> optionalUsers = userRepository.findAdminsByRoleAndUsername(UserRoles.ADMIN , "admin2@test.com");

        if (optionalUsers.isEmpty()) {

            Users users = new Users();
            users.setUsername("admin2@test.com");
            users.setPassword(new BCryptPasswordEncoder().encode("admin2"));
            users.setUserRoles(UserRoles.ADMIN);
            users.setAge(100);
            users.setProfilePhoto(null);

            userRepository.save(users);

            System.out.println("Admin 2 is created successfully");
        }else {
            System.out.println("Admin 2 already created");
        }
    }




    public UsersDto signupUser(SignUpRequest signUpRequest){

        Users users = new Users();

        users.setName(signUpRequest.getName());
        users.setUsername(signUpRequest.getUsername());
        users.setAge(signUpRequest.getAge());
        users.setPassword(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()));
        if (signUpRequest.getRoleNumber()==1){
            users.setUserRoles(UserRoles.CREATOR);
        }else if (signUpRequest.getRoleNumber()==2){
            users.setUserRoles(UserRoles.PARTICIPANT);
        }

        Users createdUser = userRepository.save(users);

        return mapper.mapFromUserToUserDTO(createdUser);
    }

    public String updateProfilePicture(Long userId , MultipartFile file){
        try{
            Users users = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir , filename);

            Files.createDirectories(filePath.getParent());
            Files.write(filePath , file.getBytes());

            users.setProfilePhoto(filename.getBytes());
            userRepository.save(users);

            return filename;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean hasUserWithEmail(String email){return userRepository.findByUsername(email).isPresent();}


    public String sendResetToken(String email){

        Users users = userRepository.findByUsername(email).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        String resetToken = jwtUtil.generateToken(email);

        users.setResetToken(resetToken);
        userRepository.save(users);

        String resetLink = "http://localhost:4200/reset-password?token="+resetToken;
        mailService.sendEmail(email , "Password Reset" , "Click the link to reset the password : " +  resetLink);

        return resetToken;
    }

    public boolean validateResetToken(String token , String email){

        String extractedEmail = jwtUtil.extractUsername(token);

        if (!extractedEmail.equals(email)){
            return false;
        }

        UserDetails userDetails = userService.loadUserByUsername(email);

        return jwtUtil.isTokenValid(token,userDetails);
    }

    public String resetPassword(String email , String token , String newPassword){

        if (!validateResetToken(token, email)){
            throw new IllegalArgumentException("Invalid or expired token");
        }

        Users users = userRepository.findByUsername(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        users.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        userRepository.save(users);

        return "Password reset successfully";
    }

    public void uploadProfilePhoto(String userName, MultipartFile file) throws IOException {
        Users user = userRepository.findByUsername(userName).orElseThrow(() -> new RuntimeException("User not found with Name: " + userName));

        if (file != null && !file.isEmpty()){
            user.setProfilePhoto(file.getBytes());
            userRepository.save(user);
        }else{
            throw new RuntimeException("File is empty or null");
        }
    }

    public byte[] getProfilePhoto(String userName){

        Users user = userRepository.findByName(userName).orElseThrow(() -> new RuntimeException("User not found with Name: " + userName));

        return user.getProfilePhoto();
    }
}
