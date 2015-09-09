/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.teleanjar.ppobws.dao;

import id.co.teleanjar.ppobws.PpobwsApp;
import id.co.teleanjar.ppobws.domain.User;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author moe
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PpobwsApp.class)
public class UserDaoTest {
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Test
    public void insertJob() {
        User u = new User();
        
        u.setUsername("emon");
        u.setPassword( passwordEncoder.encode("passwordku") );
        
        userDao.save(u);
    }
    
    public void updateJob() {
        User u = userDao.findByUsername("rehmon");
        
        u.setPassword("updated");
        
        userDao.save(u);
    }
    
    
    public void deleteJob() {
        User u = userDao.findByUsername("rehmon");
        
        
        userDao.delete(u); 
    }
    
    public void selectJob() {
        List<User> listUser = userDao.findAll();
        
        int i=0;
        
        for (User user : listUser) {
            i++;
            System.out.println("user ke- "+ i +"  : " + user.getUsername());
        }
        
    }
}