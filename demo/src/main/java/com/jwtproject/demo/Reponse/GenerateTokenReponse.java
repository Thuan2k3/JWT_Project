package com.jwtproject.demo.Reponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenerateTokenReponse {
    private String accessToken;


    public GenerateTokenReponse(String accessToken) {
        this.accessToken = accessToken;
    }
}