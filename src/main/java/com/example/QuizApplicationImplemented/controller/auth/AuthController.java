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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;


//    @PostMapping("/signup")
//    public ResponseEntity<?> signupUser(@RequestBody SignUpRequest request){
//
//        if (authService.hasUserWithEmail(request.getUsername())){
//            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("User already exists with this email" + request);
//        }
//
//        UsersDto usersDto = authService.signupUser(request);
//
//        if (usersDto == null){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not created");
//        }
//        return ResponseEntity.status(HttpStatus.CREATED).body(usersDto);
//    }


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CusUserDetailService userDetailService;

    @Autowired
    private AuthenticationManager authenticationManager;


//    @PostMapping("/login")
//    public ResponseEntity<LoginAuthResponse> login(@RequestBody LoginAuthRequest authRequest){
//
//        try{
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername() , authRequest.getPassword()));
//        }catch (BadCredentialsException ex){
//            throw new BadCredentialsException("Incorrect username or password");
//        }
//
//        UserDetails userDetails = userDetailService.loadUserByUsername(authRequest.getUsername());
//        Optional<Users> users = userRepository.findByUsername(authRequest.getUsername());
//
//        String jwtToken = jwtUtil.generateToken(users.get().getUsername());
//
//        LoginAuthResponse authResponse = new LoginAuthResponse();
//        if (users.isPresent()){
//            authResponse.setId(users.get().getId());
//            authResponse.setJwt(jwtToken);
//            authResponse.setUserRoles(users.get().getUserRoles());
//        }
//
//        return ResponseEntity.status(HttpStatus.ACCEPTED).body(authResponse);
//    }
//

//    @PostMapping("/forgot-password")
//    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest){
//
//        String token = authService.sendResetToken(forgotPasswordRequest.getEmail());
//        return ResponseEntity.ok(token);
//
//    }
//
//    @PostMapping("/reset-password")
//    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
//
//        authService.resetPassword(
//                resetPasswordRequest.getEmail(),
//                resetPasswordRequest.getToken(),
//                resetPasswordRequest.getNewPassword()
//        );
//        return ResponseEntity.ok("Password reset successful");
//    }
//
//    @PostMapping("/{userName}/upload-profile-photo")
//    public ResponseEntity<String> uploadProfilePhoto(@PathVariable String userName , @RequestParam("file")MultipartFile file){
//
//        try{
//            authService.uploadProfilePhoto(userName, file);
//            return ResponseEntity.ok("Profile Photo uploaded successfully");
//        } catch (IOException e) {
//           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload profile photo: " +e.getMessage());
//        }
//    }
//
//    @GetMapping("/{username}/profile-photo")
//    public ResponseEntity<byte[]> getProfilePhoto(@PathVariable(name = "username") String username) {
//        byte[] imageData = authService.getProfilePhoto(username);
//        if (imageData == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.IMAGE_JPEG); // adjust as needed
//        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
//    }


    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@RequestBody SignUpRequest request) {
        try {
            if (authService.hasUserWithEmail(request.getUsername())) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "User already exists with this email");
                errorResponse.put("field", "username");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            UsersDto usersDto = authService.signupUser(request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User created successfully");
            response.put("user", usersDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginAuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            UserDetails userDetails = userDetailService.loadUserByUsername(authRequest.getUsername());
            Optional<Users> users = userRepository.findByUsername(authRequest.getUsername());

            if (users.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            String jwtToken = jwtUtil.generateToken(users.get().getUsername());

            LoginAuthResponse authResponse = new LoginAuthResponse();
            authResponse.setId(users.get().getId());
            authResponse.setJwt(jwtToken);
            authResponse.setUserRoles(users.get().getUserRoles());

            return ResponseEntity.ok(authResponse);

        } catch (BadCredentialsException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            String result = authService.sendRequestToken(forgotPasswordRequest.getEmail());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Password reset email sent successfully");
            response.put("email", forgotPasswordRequest.getEmail());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to send reset email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            authService.resetPassword(
                    resetPasswordRequest.getEmail(),
                    resetPasswordRequest.getToken(),
                    resetPasswordRequest.getNewPassword()
            );

            Map<String, String> response = new HashMap<>();
            response.put("message", "Password reset successful");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Password reset failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/{userName}/upload-profile-photo")
    public ResponseEntity<?> uploadProfilePhoto(
            @PathVariable String userName,
            @RequestParam("file") MultipartFile file) {

        try {
            // Validate file
            if (file.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Please select a file to upload");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Only image files are allowed");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Validate file size (5MB limit)
            if (file.getSize() > 5 * 1024 * 1024) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "File size must be less than 5MB");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            authService.uploadProfilePhoto(userName, file);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Profile photo uploaded successfully");
            response.put("fileName", file.getOriginalFilename());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to upload profile photo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{username}/profile-photo")
    public ResponseEntity<byte[]> getProfilePhoto(@PathVariable String username) {
        try {
            byte[] imageData = authService.getProfilePhoto(username);

            if (imageData == null || imageData.length == 0) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(imageData.length);
            headers.setCacheControl("max-age=3600"); // Cache for 1 hour

            return new ResponseEntity<>(imageData, headers, HttpStatus.OK);

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid authorization header");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            UserDetails userDetails = userDetailService.loadUserByUsername(username);

            if (jwtUtil.isTokenValid(token, userDetails)) {
                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("username", username);
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Token validation failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

}
