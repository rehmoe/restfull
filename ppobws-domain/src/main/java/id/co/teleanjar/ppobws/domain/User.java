/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.teleanjar.ppobws.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author moe
 * belajar lebih lanjut : hibernate orm
 */

@Entity @Table(name="t_user")
public class User extends BaseEntity {
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    
    
    @Column(name = "pwd", nullable = false)
    private String password;
    
    @Column(name = "active", nullable = false)
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
