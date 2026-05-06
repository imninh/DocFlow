package com.group13.reportsystem.model;

import jakarta.validation.constraints.NotBlank;

public class LoginForm {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String classCode;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }
}
