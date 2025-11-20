package com.partyst.app.partystapp.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.partyst.app.partystapp.entities.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByNicknameContainingIgnoreCase(String nickname);
    
    @Query("SELECT DISTINCT u FROM User u JOIN u.skills s WHERE LOWER(s.name) IN :skillNames")
    List<User> findBySkillNames(@Param("skillNames") List<String> skillNames);
    
} 
