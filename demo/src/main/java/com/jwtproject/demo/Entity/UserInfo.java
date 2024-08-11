package com.jwtproject.demo.Entity;

import com.jwtproject.demo.Rules.MailRFCConstraint;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Length(max = 100)
    private String name;

    @Column(name = "email", unique = true)
    @Length(max = 50)
    @MailRFCConstraint
    private String email;

    @Column(name = "password")
    @Length(max = 255)
    private String password;

    @Column(name = "phoneNumber")
    @Length(max = 15)
    private String phoneNumber;

    @Column(name = "roles", columnDefinition = "varchar(50) default \"ROLE_USER\"")
    private String roles;
}
