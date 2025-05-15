package com.employeeportal.repository.onboarding;

import com.employeeportal.model.onboarding.IdentificationDetails;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface IdentificationDetailsRepository extends JpaRepository<IdentificationDetails, Long> {
    List<IdentificationDetails> findByEmployeeEmployeeId(int employeeId);

    @Modifying
    @Transactional
    @Query(value = "delete from identification_details where employee_id = ?1", nativeQuery = true)
    void deleteAllByEmployeeId(int employeeId);

    @Query(value = "select * from identification_details where employee_id = ?1 AND identity_type = ?2 ", nativeQuery = true)
    IdentificationDetails findByEmployeeAndType(int employeeId, String identityType);

    @Modifying
    @Transactional
    @Query(value = "delete from identification_details where employee_id = ?1 AND identity_type = ?2", nativeQuery = true)
    void deleteByEmployeeIdAndType(int employeeId, String identityType);
}