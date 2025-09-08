package com.example.QuizApplicationImplemented.controller.auth;

import com.example.QuizApplicationImplemented.dto.authenticationDTO.*;
import com.example.QuizApplicationImplemented.entity.Users;
import com.example.QuizApplicationImplemented.repository.UserRepository;
import com.example.QuizApplicationImplemented.security.CusUserDetailService;
import com.example.QuizApplicationImplemented.security.JwtUtil;
import com.example.QuizApplicationImplemented.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@RequestBody SignUpRequest request){

        if (authService.hasUserWithEmail(request.getUsername())){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("User already exists with this email" + request);
        }

        UsersDto usersDto = authService.signupUser(request);

        if (usersDto == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not created");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(usersDto);
    }

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CusUserDetailService userDetailService;

    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("/login")
    public ResponseEntity<LoginAuthResponse> login(@RequestBody LoginAuthRequest authRequest){

        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername() , authRequest.getPassword()));
        }catch (BadCredentialsException ex){
            throw new BadCredentialsException("Incorrect username or password");
        }

        UserDetails userDetails = userDetailService.loadUserByUsername(authRequest.getUsername());
        Optional<Users> users = userRepository.findByUsername(authRequest.getUsername());

        String jwtToken = jwtUtil.generateToken(users.get().getUsername());

        LoginAuthResponse authResponse = new LoginAuthResponse();
        if (users.isPresent()){
            authResponse.setId(users.get().getId());
            authResponse.setJwt(jwtToken);
            authResponse.setUserRoles(users.get().getUserRoles());
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(authResponse);
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest){

        String token = authService.sendResetToken(forgotPasswordRequest.getEmail());
        return ResponseEntity.ok(token);

    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){

        authService.resetPassword(
                resetPasswordRequest.getEmail(),
                resetPasswordRequest.getToken(),
                resetPasswordRequest.getNewPassword()
        );
        return ResponseEntity.ok("Password reset successful");
    }

    @PostMapping("/{userName}/upload-profile-photo")
    public ResponseEntity<String> uploadProfilePhoto(@PathVariable String userName , @RequestParam("file")MultipartFile file){

        try{
            authService.uploadProfilePhoto(userName, file);
            return ResponseEntity.ok("Profile Photo uploaded successfully");
        } catch (IOException e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload profile photo: " +e.getMessage());
        }
    }

    @GetMapping("/{username}/profile-photo")
    public ResponseEntity<byte[]> getProfilePhoto(@PathVariable(name = "username") String username) {
        byte[] imageData = authService.getProfilePhoto(username);
        if (imageData == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // adjust as needed
        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
    }
}
