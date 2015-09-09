/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.teleanjar.ppobws;

import id.co.teleanjar.ppobws.PpobwsApp;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 *
 * @author moe
 */

@Configuration
@Import({PpobwsApp.class})
public class RestfullServer {
    public static void main(String[] args) {
        SpringApplication.run(RestfullServer.class, args);
        
    }
    
}
