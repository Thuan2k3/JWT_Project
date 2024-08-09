package com.jwtproject.demo.Entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {


    @Email
    private String username;
    @Size(max = 20, message = "Password must be at most 20 characters long")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

}
