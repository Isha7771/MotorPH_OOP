/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.common;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ValidationResult {

    private final Map<String, String> errors = new LinkedHashMap<>();

    public void addError(String field, String message) {
        if (field == null) field = "general";
        if (message == null) message = "Invalid value.";
        errors.put(field, message);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public Map<String, String> getErrors() {
        return Collections.unmodifiableMap(errors);
    }

    public String summary() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : errors.entrySet()) {
            sb.append("• ").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }
        return sb.toString().trim();
    }
}
