package com.mycompany.oopmotorph.ticket.model;

import java.time.LocalDateTime;

public class Ticket {

    private final String id;
    private final String employeeNo;
    private final String employeeLastName;
    private final String employeeFirstName;
    private final String title;
    private final String description;
    private String status; // OPEN, IN_PROGRESS, RESOLVED
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String handledByEmployeeNo; // IT staff
    private String handledByName;

    public Ticket(String id,
                  String employeeNo,
                  String employeeLastName,
                  String employeeFirstName,
                  String title,
                  String description,
                  String status,
                  LocalDateTime createdAt,
                  LocalDateTime updatedAt,
                  String handledByEmployeeNo,
                  String handledByName) {
        this.id = id;
        this.employeeNo = employeeNo;
        this.employeeLastName = employeeLastName;
        this.employeeFirstName = employeeFirstName;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.handledByEmployeeNo = handledByEmployeeNo;
        this.handledByName = handledByName;
    }

    public String getId() {
        return id;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public String getEmployeeLastName() {
        return employeeLastName;
    }

    public String getEmployeeFirstName() {
        return employeeFirstName;
    }

    public String getEmployeeFullName() {
        return employeeFirstName + " " + employeeLastName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getHandledByEmployeeNo() {
        return handledByEmployeeNo;
    }

    public void setHandledByEmployeeNo(String handledByEmployeeNo) {
        this.handledByEmployeeNo = handledByEmployeeNo;
    }

    public String getHandledByName() {
        return handledByName;
    }

    public void setHandledByName(String handledByName) {
        this.handledByName = handledByName;
    }
}
