package com.employeeportal.repository.onboarding;

import com.employeeportal.model.onboarding.Address;
import com.employeeportal.model.onboarding.PreviewDetails;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreviewRepository extends JpaRepository<PreviewDetails, Integer> {
    PreviewDetails findByEmployeeEmployeeId(int employeeId);

}