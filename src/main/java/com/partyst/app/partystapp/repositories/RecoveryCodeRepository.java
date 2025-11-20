package com.partyst.app.partystapp.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.partyst.app.partystapp.entities.RecoveryCode;


@Repository
public interface RecoveryCodeRepository extends JpaRepository<RecoveryCode, String> {

    Optional<RecoveryCode> findByEmail(String email);
}
