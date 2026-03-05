/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.requests.model;

public abstract class AbstractRequest {

    protected final String requestId;
    protected final String employeeName;

    protected AbstractRequest(String requestId, String employeeName) {

        if (requestId == null || requestId.trim().isEmpty()) {
            throw new IllegalArgumentException("Request ID is required.");
        }

        if (employeeName == null || employeeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name is required.");
        }

        this.requestId = requestId.trim();
        this.employeeName = employeeName.trim();
    }

    public String getRequestId() {
        return requestId;
    }

    public String getEmployeeName() {
        return employeeName;
    }
}
