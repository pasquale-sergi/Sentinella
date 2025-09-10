package com.sentinella.Sentinella.auth;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class LoginRequest {

    private String username;

    private String password;

    public void setPassword(String password) { this.password = password; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public String getUsername() { return username; }
}