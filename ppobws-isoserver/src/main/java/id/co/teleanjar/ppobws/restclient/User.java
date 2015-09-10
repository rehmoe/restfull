/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.teleanjar.ppobws.restclient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author moe
 * belajar lebih lanjut : hibernate orm
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class User  {
    
    private String username;
    private String password;
    private Boolean active = Boolean.TRUE;

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

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
