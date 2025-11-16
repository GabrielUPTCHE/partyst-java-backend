package com.partyst.app.partystapp.entities;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
    private String cellphone;
    private String nickname;
    private String password;

    @ManyToMany
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name="user_id"),
        inverseJoinColumns = @JoinColumn(name="role_id"),
        uniqueConstraints = {@UniqueConstraint(columnNames= {"user_id", "role_id"})}
    )
    private List<Role> roles;



   /*  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Token> tokens; */
}
