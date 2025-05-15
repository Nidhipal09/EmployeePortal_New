package com.employeeportal.repository.onboarding;

import com.employeeportal.model.onboarding.EmployeeOrganizationDetails;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeOrganizationDetailsRepository extends JpaRepository<EmployeeOrganizationDetails, Integer> {

    @Query(value = "SELECT * FROM employee_organization_details WHERE employee_id = ?1", nativeQuery = true)
    Optional<EmployeeOrganizationDetails> findByEmployeeId(int employeeId);

    @Query(value = "SELECT employee_id FROM employee_organization_details WHERE employee_code = ?1", nativeQuery = true)
    Optional<Integer> findByEmployeeCode(String employeeCode);
}