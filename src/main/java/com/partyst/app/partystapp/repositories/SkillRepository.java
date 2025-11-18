package com.partyst.app.partystapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.partyst.app.partystapp.entities.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer>{

}
