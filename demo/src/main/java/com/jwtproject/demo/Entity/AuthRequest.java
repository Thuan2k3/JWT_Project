package com.jwtproject.demo.Entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String username;
    @NotBlank(message = "Password is required")
    @Size(max = 20, message = "Password must be at most 20 characters long")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

}
