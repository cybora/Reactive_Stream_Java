package com.bora.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.bora.model.Employee;

public interface EmployeeRepository extends ReactiveCrudRepository<Employee, String>{

}
