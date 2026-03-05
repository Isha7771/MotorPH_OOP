/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.overtime.model;

import com.mycompany.oopmotorph.requests.model.AbstractRequest;

public class OvertimeRequest extends AbstractRequest {

    private final String date;
    private final String hours;
    private OvertimeStatus status;
    private final String reason;

    public OvertimeRequest(String requestId,
                           String employeeName,
                           String date,
                           String hours,
                           OvertimeStatus status,
                           String reason) {

        super(requestId, employeeName);

        this.date = date;
        this.hours = hours;
        this.status = status;
        this.reason = reason;
    }

    public String getDate() { return date; }

    public String getHours() { return hours; }

    public OvertimeStatus getStatus() { return status; }

    public String getReason() { return reason; }

    public void setStatus(OvertimeStatus status) {
        if (status != null) this.status = status;
    }
}