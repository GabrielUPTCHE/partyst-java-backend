package com.partyst.app.partystapp.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "password_recovery", schema = "partyst_plastic")
public class RecoveryCode {

    @Id
    @Column(name = "email")
    private String email;
    
    @Column(name = "token_recovery")
    private String tokenRecovery;

}
