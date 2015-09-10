/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.teleanjar.ppobws.web;

import id.co.teleanjar.ppobws.dao.UserDao;
import id.co.teleanjar.ppobws.domain.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author moe
 */

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
            
    @ResponseBody
    @RequestMapping(value =  "/a", method = RequestMethod.GET)
    public List<User> listUserA(){
        return userDao.findAll();
    }
    
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public User findUser(@RequestParam("username") String username){
        return userDao.findByUsername(username);
    }
    
    @ResponseBody
    @RequestMapping(value =  "/b", method = RequestMethod.GET)
    public List<Map<String, Object>> listUserB(){
        
        List<User> data = userDao.findAll();
        
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        
        for (User user : data) {
            Map<String, Object> obj = new HashMap<String, Object>();
            obj.put("username", user.getUsername());
            obj.put("active", user.isActive());
            
            result.add(obj);
        }
        return result;
    }
    
    @ResponseBody
    @RequestMapping(value =  "/save", method = RequestMethod.POST)
    public Map<String, Object> saveUser(@RequestBody Map<String, Object> reqObj) {
        
        Map<String, Object> resObj = new HashMap<String, Object>();
        
        try {
            
            User u = new User();

            u.setUsername(reqObj.get("username").toString());
            u.setPassword( passwordEncoder.encode(reqObj.get("password").toString()) );

            userDao.save(u);
            
            resObj.put("rc", "00");
            resObj.put("message", "sukses");
        } catch(Exception e) {
            resObj.put("rc", "99");
            resObj.put("message", "proses simpan gagal");
            
            logger.error(e.getMessage(), e);
        }
        
        return resObj;
    }
    
    @ResponseBody
    @RequestMapping(value =  "/simpan", method = RequestMethod.POST)
    public Map<String, Object> simpanUser(@RequestParam("user") String username, @RequestParam("pwd") String password) {
        
        Map<String, Object> resObj = new HashMap<String, Object>();
        
        try {
            User u = new User();

            u.setUsername(username);
            u.setPassword(password);

            userDao.save(u);
            
            resObj.put("rc", "00");
            resObj.put("message", "sukses");
        } catch(Exception e) {
            resObj.put("rc", "99");
            resObj.put("message", "proses simpan gagal");
            
            logger.error(e.getMessage(), e);
        }
        
        return resObj;
    }
}
