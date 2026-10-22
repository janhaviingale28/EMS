# Employee Management System (Spring Boot + PostgreSQL)

Simple example of:
- One-to-one: `Employee` -> `EmployeeProfile`
- One-to-many: `Employee` -> `EmployeeTask`
- `cascade = {CascadeType.PERSIST, CascadeType.MERGE}` (not `CascadeType.ALL`)
- `orphanRemoval = true`

## Tables
Hibernate creates **3 tables**:
1. `employees` — employee data and `profile_id` foreign key
2. `employee_profiles` — phone/address
3. `employee_tasks` — task data and `employee_id` foreign key

`orphanRemoval=true` does not create an extra table or column. It changes delete behavior: when an existing task is removed from an employee's `tasks` collection and the transaction is saved/flushed, that task row is deleted. If the employee is deleted, its associated tasks are deleted too. It does not delete a task just because an API request omits it unless your update logic actually removes it from the managed collection.

## Setup
1. Install Java 17+ and PostgreSQL.
2. In pgAdmin, create database `employee_db`.
3. Open `src/main/resources/application.properties` and replace `YOUR_POSTGRES_PASSWORD`.
4. Run `EmployeeManagementSystemApplication.java` in Eclipse/STS, or run `mvn spring-boot:run`.
5. Hibernate `ddl-auto=update` creates/updates tables automatically.

## POST example
POST `http://localhost:8080/api/employees`
Content-Type: application/json

```json
{
  "name": "Janhavi Ingale",
  "email": "janhavi@example.com",
  "profile": {
    "phone": "9876543210",
    "address": "Ahmedabad"
  },
  "tasks": [
    {"title": "Build REST API", "status": "IN_PROGRESS"},
    {"title": "Write JUnit tests", "status": "PENDING"}
  ]
}
```

## Endpoints
- `POST /api/employees` — create employee with profile and tasks
- `GET /api/employees` — list employees
- `GET /api/employees/{id}` — get one employee
- `PUT /api/employees/{id}` — update employee
- `DELETE /api/employees/{id}` — delete employee

## Important mapping notes
- `Employee` owns the one-to-one relation because it contains `profile_id`.
- `EmployeeTask` owns the many-to-one side because it contains `employee_id`.
- `Employee.tasks` is the inverse side (`mappedBy = "employee"`).
- `PERSIST` saves new related objects when the parent is saved.
- `MERGE` propagates merge/update operations to related objects.
- `orphanRemoval=true` deletes a child row when it is removed from the parent's relationship.
- `CascadeType.ALL` is intentionally not used.
- This is a learning example. For production, use DTOs and safer update logic so omitted child objects are not accidentally treated as removals.
