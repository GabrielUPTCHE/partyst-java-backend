package com.partyst.app.partystapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.partyst.app.partystapp.entities.Role;

@Repository
public interface RoleRepository  extends JpaRepository<Role, Integer>{

}
