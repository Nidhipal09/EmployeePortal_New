package com.employeeportal.repository.onboarding;

import com.employeeportal.model.onboarding.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByRoleId(int roleId);
    Role findByRoleName(String name);
}