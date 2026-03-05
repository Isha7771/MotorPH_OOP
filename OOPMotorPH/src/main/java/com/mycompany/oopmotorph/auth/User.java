/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.auth;

public class User {
    private final String username;
    private final String password;
    private final Role role;

    private final String employeeNo;
    private final String lastName;
    private final String firstName;

    // From EmployeeData.csv
    private final String position;
    // Regular / Probational
    private final String category;

    public User(String username, String password, Role role,
                String employeeNo, String lastName, String firstName,
                String position, String category) {
        this.username = username == null ? "" : username.trim();
        this.password = password == null ? "" : password.trim();
        this.role = role == null ? Role.EMPLOYEE : role;
        this.employeeNo = employeeNo == null ? "" : employeeNo.trim();
        this.lastName = lastName == null ? "" : lastName.trim();
        this.firstName = firstName == null ? "" : firstName.trim();

        this.position = position == null ? "" : position.trim();
        this.category = category == null ? "" : category.trim();
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public String getEmployeeNo() { return employeeNo; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getPosition() { return position; }
    public String getCategory() { return category; }

    public String getFullName() {
        String a = (lastName + " " + firstName).trim();
        return a.isEmpty() ? username : a;
    }
}
