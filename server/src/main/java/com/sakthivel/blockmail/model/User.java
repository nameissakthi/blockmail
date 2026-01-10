package com.sakthivel.blockmail.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "users")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
}