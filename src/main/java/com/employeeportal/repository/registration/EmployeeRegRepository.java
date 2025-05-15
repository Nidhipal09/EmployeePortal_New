package com.employeeportal.repository.registration;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employeeportal.model.registration.EmployeeReg;

@Repository
public interface EmployeeRegRepository extends JpaRepository<EmployeeReg, Integer> {

    EmployeeReg findByEmail(String email);

    @Query("SELECT e FROM EmployeeReg e WHERE e.employee.employeeId = :employeeId")
    EmployeeReg findByEmployeeId(@Param("employeeId") int employeeId);

    boolean existsByEmail(String email);

    @Query(value = "SELECT r.* FROM employee_reg r JOIN employee e ON r.employee_id = e.employee_id WHERE e.mobile_number = ?1", nativeQuery = true)
    EmployeeReg findByMobileNumberFromEmployee(String mobileNumber);

    @Query(value = "SELECT r.* FROM employee_reg r JOIN personal_details p ON r.employee_id = p.employee_id WHERE p.secondary_mobile = ?1", nativeQuery = true)
    EmployeeReg findBySecondaryMobileNumberFromEmployee(String secondary_mobile);

}
