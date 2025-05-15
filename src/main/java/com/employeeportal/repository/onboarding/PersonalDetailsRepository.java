package com.employeeportal.repository.onboarding;

import com.employeeportal.model.onboarding.PersonalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalDetailsRepository extends JpaRepository<PersonalDetails, Integer> {
    PersonalDetails findByEmployeeEmployeeId(int employeeId);

    Boolean existsByPersonalEmail(String email);

    PersonalDetails findByPersonalEmail(String email);
}