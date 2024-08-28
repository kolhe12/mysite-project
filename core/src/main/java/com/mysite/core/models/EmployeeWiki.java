package com.mysite.core.models;

public class EmployeeWiki {
    private String name;
    private String email;
    private int id;
    private String department;

    public EmployeeWiki(String name, String email, int id, String department) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    public String getDepartment() {
        return department;
    }
}
