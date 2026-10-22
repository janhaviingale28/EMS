package com.example.employeemanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    // One Employee has one Profile. No CascadeType.ALL.
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @JoinColumn(name = "profile_id", unique = true)
    private EmployeeProfile profile;

    // One Employee can have many tasks. Removing a task from this list deletes its row.
    @OneToMany(mappedBy = "employee",
               cascade = {CascadeType.PERSIST, CascadeType.MERGE},
               orphanRemoval = true)
    @JsonManagedReference
    private List<EmployeeTask> tasks = new ArrayList<>();

    public void addTask(EmployeeTask task) {
        if (task == null) return;
        tasks.add(task);
        task.setEmployee(this);
    }

    public void removeTask(EmployeeTask task) {
        if (task == null) return;
        tasks.remove(task);
        task.setEmployee(null);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public EmployeeProfile getProfile() { return profile; }
    public void setProfile(EmployeeProfile profile) { this.profile = profile; }
    public List<EmployeeTask> getTasks() { return tasks; }
    public void setTasks(List<EmployeeTask> tasks) {
        this.tasks.clear();
        if (tasks != null) for (EmployeeTask task : tasks) addTask(task);
    }
}
