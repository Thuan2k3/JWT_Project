package com.jwtproject.demo.Controller;

import com.jwtproject.demo.Entity.AuthRequest;
import com.jwtproject.demo.Entity.UserInfo;
import com.jwtproject.demo.Reponse.GenerateTokenReponse;
import com.jwtproject.demo.Reponse.ReponseObject;
import com.jwtproject.demo.Repository.UserInfoRepository;
import com.jwtproject.demo.Service.JwtService;
import com.jwtproject.demo.Service.UserInfoService;
import com.jwtproject.demo.Utils.Util;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
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

    @PostMapping("/addNewUser")
    public String addNewUser(@RequestBody UserInfo userInfo) {
        return service.addUser(userInfo);
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

    @PostMapping("/generateToken")
    public ResponseEntity<ReponseObject> authenticateAndGetToken(@Valid @RequestBody AuthRequest authRequest) {
        Optional<UserInfo> user = this.userInfoRepository.findByEmail(authRequest.getUsername());
        ReponseObject reponseObject;
        reponseObject = ReponseObject.builder()
                .message("Fail")
                .statusCode(HttpStatus.FORBIDDEN.value())
                .data(null)
                .build();

        if (Util.notNull(user)) {
            this.doAuthenticate(authRequest.getUsername(), authRequest.getPassword());
            UserDetails userDetails = service.loadUserByUsername(authRequest.getUsername());
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
//    public GenerateTokenReponse authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
//        if (authentication.isAuthenticated()) {
//            return new GenerateTokenReponse(jwtService.generateToken(authRequest.getUsername()));
//        } else {
//            throw new UsernameNotFoundException("invalid user request !");
//        }
//    }
@ExceptionHandler(BadCredentialsException.class)
public String exceptionHandler() {
    return "Credentials Invalid !!";
}

}
