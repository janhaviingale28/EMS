package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.entity.Employee;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Employee create(Employee employee) {
        employee.setId(null);
        if (employee.getTasks() != null) {
            employee.getTasks().forEach(task -> task.setId(null));
        }
        if (employee.getProfile() != null) employee.getProfile().setId(null);
        return repository.save(employee); // Cascade PERSIST saves profile and tasks too.
    }

    public List<Employee> getAll() {
        return repository.findAll();
    }

    public Employee getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));
    }

    @Transactional
    public Employee update(Long id, Employee input) {
        Employee existing = getById(id);
        existing.setName(input.getName());
        existing.setEmail(input.getEmail());
        existing.setProfile(input.getProfile());
        existing.setTasks(input.getTasks());
        // Existing entity is managed; Cascade MERGE applies to related entities when merged/saved.
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new IllegalArgumentException("Employee not found: " + id);
        repository.deleteById(id);
    }
}
