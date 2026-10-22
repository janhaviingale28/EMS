package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.entity.Employee;
import com.example.employeemanagementsystem.entity.EmployeeTask;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository repository;

    public EmployeeServiceImpl(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Employee create(Employee employee) {
        employee.setId(null);
        if (employee.getTasks() != null) {
            employee.getTasks().forEach(task -> task.setId(null));
        }
        if (employee.getProfile() != null) {
            employee.getProfile().setId(null);
        }
        // CascadeType.PERSIST saves the new profile and tasks with the employee.
        return repository.save(employee);
    }

    @Override
    public List<Employee> getAll() {
        return repository.findAll();
    }

    @Override
    public Employee getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Employee not found: " + id));
    }

    @Override
    @Transactional
    public Employee update(Long id, Employee input) {
        Employee existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Employee not found: " + id));

        existing.setName(input.getName());
        existing.setEmail(input.getEmail());

        // Keep the managed collection and update/remove its children explicitly.
        if (input.getProfile() != null) {
            if (existing.getProfile() == null) {
                existing.setProfile(input.getProfile());
            } else {
                existing.getProfile().setPhone(input.getProfile().getPhone());
                existing.getProfile().setAddress(input.getProfile().getAddress());
            }
        }

        if (input.getTasks() != null) {
            // Remove tasks absent from the incoming list: orphanRemoval deletes their rows.
            List<Long> incomingIds = input.getTasks().stream()
                    .map(EmployeeTask::getId)
                    .filter(taskId -> taskId != null)
                    .toList();

            for (EmployeeTask oldTask : List.copyOf(existing.getTasks())) {
                if (!incomingIds.contains(oldTask.getId())) {
                    existing.removeTask(oldTask);
                }
            }

            for (EmployeeTask inputTask : input.getTasks()) {
                if (inputTask.getId() == null) {
                    EmployeeTask newTask = new EmployeeTask();
                    newTask.setTitle(inputTask.getTitle());
                    newTask.setStatus(inputTask.getStatus());
                    existing.addTask(newTask);
                } else {
                    existing.getTasks().stream()
                            .filter(t -> t.getId().equals(inputTask.getId()))
                            .findFirst()
                            .ifPresent(oldTask -> {
                                oldTask.setTitle(inputTask.getTitle());
                                oldTask.setStatus(inputTask.getStatus());
                            });
                }
            }
        }

        // existing is managed inside the transaction; MERGE is configured in entity mapping.
        return repository.save(existing);
    }

    @Override
    @Transactional
    public void removeTask(Long employeeId, Long taskId) {
        Employee employee = repository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Employee not found: " + employeeId));

        EmployeeTask task = employee.getTasks().stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Task not found for employee: " + taskId));

        employee.removeTask(task);
        // orphanRemoval=true deletes this task row when the transaction commits.
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Employee not found: " + id));
        repository.delete(employee);
    }
}
