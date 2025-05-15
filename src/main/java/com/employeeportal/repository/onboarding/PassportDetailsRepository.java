package com.employeeportal.repository.onboarding;

import com.employeeportal.model.onboarding.PassportDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PassportDetailsRepository extends JpaRepository<PassportDetails, Long> {

    PassportDetails findByPassportNumber(String passportNumber);

    PassportDetails findByEmployeeEmployeeId(int employeeId);

    @Modifying
    @Transactional
    @Query(value = "delete from passport_details where employee_id = ?1", nativeQuery = true)
    void deleteAllByEmployeeId(int employeeId);
}