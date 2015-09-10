/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.teleanjar.ppobws.restclient;

import java.nio.charset.Charset;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author moe
 */
@Repository
public class RestClientService {

    public HttpHeaders createHeaders(final String username, final String password) {
        return new HttpHeaders() {
            {
                String auth = username + ":" + password;
                byte[] encodedAuth = Base64.encodeBase64(
                        auth.getBytes(Charset.forName("US-ASCII")));
                String authHeader = "Basic " + new String(encodedAuth);
                set("Authorization", authHeader);
            }
        };
    }

    public User getUser(String username) {
        RestTemplate restTemplate = new RestTemplate();

        //User user = restTemplate.getForObject("http://localhost:8080/ppobws/api/user?username=" + username, User.class);
        
        String uri = "http://localhost:8080/ppobws/api/user?username=" + username;

        ResponseEntity<User> respEntity = restTemplate.exchange
                        (uri, HttpMethod.GET, new HttpEntity<String>(createHeaders("superuser", "passwordku")), User.class);
        
        return respEntity.getBody();
    }
}
