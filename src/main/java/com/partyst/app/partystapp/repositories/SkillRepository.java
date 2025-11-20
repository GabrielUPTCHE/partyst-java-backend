package com.partyst.app.partystapp.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.partyst.app.partystapp.entities.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer>{
    List<Skill> findBySkillIdIn(Set<Integer> skillIds);
}
