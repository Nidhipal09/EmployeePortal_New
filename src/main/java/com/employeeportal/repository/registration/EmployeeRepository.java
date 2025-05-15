package com.employeeportal.repository.registration;

import com.employeeportal.model.GeneralResponses;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.employeeportal.model.registration.Employee;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

  Employee findByEmployeeId(int employeeId);

  @Query(value = "select primary_id,role_name from employee  where email = ?1", nativeQuery = true)
  GeneralResponses getIdByEmployeename(String employeeName);

  @Query(value = "SELECT * FROM employee WHERE email = ?1", nativeQuery = true)
  Employee findByEmail(String email);

  @Query(value = "SELECT * FROM employee WHERE mobile_number = ?1", nativeQuery = true)
  Optional<Employee> findByMobileNumber(String mobileNumber);

  @Transactional
  @Modifying
  @Query(value = "UPDATE employee SET password = ?2 WHERE email = ?1", nativeQuery = true)
  void updatePasswordByEmail(String email, String newPassword);

  boolean existsByEmail(String email);

  @Query(value = "SELECT password FROM employee WHERE email = :email", nativeQuery = true)
  String findPasswordByEmail(String email);

  Page<Employee> findAllByFirstName(String firstName, PageRequest pageRequest);

  @Query(value = "SELECT * FROM employee WHERE first_name = :firstName AND middle_name = :middleName", nativeQuery = true)
  Page<Employee> findAllByFirstAndMiddleName(String firstName, String middleName, PageRequest pageRequest);

  // List<PrimaryDetails> findByRoleName(String roleName);

}
