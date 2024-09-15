package com.ecommerce.project.security.response;

import java.util.List;

public class LoginResponse {

    private Long id;

    private String jwtToken;

    private String username;

    private List<String> roles;

    public LoginResponse(Long id,String username, List<String> roles, String jwtToken) {
        this.username = username;
        this.roles = roles;
        this.id = id;
        this.jwtToken = jwtToken;
    }

    public LoginResponse(Long id,String username, List<String> roles) {
        this.username = username;
        this.roles = roles;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
