package com.jwtproject.demo.Controller;

import com.jwtproject.demo.PayLoad.Request.AuthRequest;
import com.jwtproject.demo.PayLoad.Request.RegisterRequest;
import com.jwtproject.demo.Entity.UserInfo;
import com.jwtproject.demo.PayLoad.Reponse.GenerateTokenReponse;
import com.jwtproject.demo.PayLoad.Reponse.ReponseObject;
import com.jwtproject.demo.Repository.UserInfoRepository;
import com.jwtproject.demo.Service.JwtService;
import com.jwtproject.demo.Service.UserInfoService;
import com.jwtproject.demo.Utils.Message;
import com.jwtproject.demo.Utils.Util;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private UserInfoService service;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @PostMapping("auth/register")
    public ResponseEntity<ReponseObject> register(@Valid @RequestBody RegisterRequest registerRequest) {
        // Create ReposeObject with default value
        ReponseObject responseObject = ReponseObject.builder()
                .statusCode(HttpStatus.OK.value())
                .build();

        try {
            // Construct User object from RegisterRequest
            UserInfo newUser = new UserInfo();
            newUser.setEmail(registerRequest.getEmail());
            newUser.setPassword(registerRequest.getPassword());
            newUser.setName(registerRequest.getName());
            newUser.setPhoneNumber(registerRequest.getPhoneNumber());
            newUser.setRoles(new String("ROLE_USER"));

            // Handle user creation and update ResponseObject
            boolean isCreated = service.addUser(newUser);
            if (isCreated) {
                responseObject.setMessage(Message.SUCCESS);
                return new ResponseEntity<>(responseObject, HttpStatus.OK);
            } else {
                responseObject.setMessage(Message.FAIL);
                responseObject.setStatusCode(HttpStatus.BAD_REQUEST.value());
            }
        } catch (DataIntegrityViolationException exception) {
            responseObject = ReponseObject.builder()
                    .message(Message.VALIDATION_ERRORS)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .data(Map.of("email", Message.EMAIL_EXIST))
                    .build();
        }
        return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/user/userProfile")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String userProfile() {
        return "Welcome to User Profile";
    }

    @GetMapping("/admin/adminProfile")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminProfile() {
        return "Welcome to Admin Profile";
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ReponseObject> authenticateAndGetToken(@Valid @RequestBody AuthRequest authRequest) {
        Optional<UserInfo> user = this.userInfoRepository.findByEmail(authRequest.getEmail());
        ReponseObject reponseObject;
        reponseObject = ReponseObject.builder()
                .message("Fail")
                .statusCode(HttpStatus.FORBIDDEN.value())
                .data(null)
                .build();

        if (Util.notNull(user)) {
            this.doAuthenticate(authRequest.getEmail(), authRequest.getPassword());
            UserDetails userDetails = service.loadUserByUsername(authRequest.getEmail());
            String accessToken = this.jwtService.generateToken(userDetails.getUsername());
            GenerateTokenReponse response = GenerateTokenReponse
                    .builder()
                    .accessToken(accessToken)
                    .build();
            reponseObject = ReponseObject.builder()
                    .message("Success")
                    .statusCode(HttpStatus.OK.value())
                    .data(response)
                    .build();
            return new ResponseEntity<>(reponseObject, HttpStatus.OK);
        }

        return new ResponseEntity<>(reponseObject, HttpStatus.FORBIDDEN);
    }
    private void doAuthenticate(String email, String password) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
        try {
            authenticationManager.authenticate(authentication);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid Username or Password!");
        }
    }

@ExceptionHandler(BadCredentialsException.class)
public String exceptionHandler() {
    return "Credentials Invalid !!";
}

}
