/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.teleanjar.ppobws.dao;

import id.co.teleanjar.ppobws.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author moe
 */
public interface UserDao extends JpaRepository<User, String> {
    
    public User findByUsername(String username);
    
    public User findByUsernameAndPassword (String username, String password);
}
