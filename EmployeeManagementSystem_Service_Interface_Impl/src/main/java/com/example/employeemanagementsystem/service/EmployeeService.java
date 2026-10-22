package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.entity.Employee;
import java.util.List;

public interface EmployeeService {
    Employee create(Employee employee);
    List<Employee> getAll();
    Employee getById(Long id);
    Employee update(Long id, Employee input);
    void delete(Long id);
    void removeTask(Long employeeId, Long taskId);
}
