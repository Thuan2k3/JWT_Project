package com.jwtproject.demo.Reponse;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReponseObject {
    private String message;
    private int statusCode;
    private Object data;
}
