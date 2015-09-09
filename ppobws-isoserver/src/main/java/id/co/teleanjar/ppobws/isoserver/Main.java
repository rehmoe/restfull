/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.teleanjar.ppobws.isoserver;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author moe
 */
@SpringBootApplication
public class Main {
    @Bean
    public IsoGateway isoGateway(){
        return new IsoGateway();
    }
}
