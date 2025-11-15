package com.partyst.app.partystapp.usuario;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", schema = "partyst_plastic")
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long userId;

    private String name;
    private String lastname;

    @Column(unique = true)
    private String email;
    private String celphone;
    private LocalDate birthdate;
    private String password;

   /*  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Token> tokens; */
}
