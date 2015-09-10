/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.teleanjar.ppobws.restclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author moe
 */
@Repository
public class RestClientService {
    private final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    
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
    
    public Map<String, Object> inquiry(String idpel, String idloket, String merchantCode) throws IOException {
        String uri = "http://localhost:8080/ppobws/api/produk";
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = createHeaders("superuser", "passwordku");
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri)
                .queryParam("idpel", idpel)
                .queryParam("idloket", idloket)
                .queryParam("merchantcode", merchantCode);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> httpResp = restTemplate.exchange
                        (builder.build().encode().toUri(), HttpMethod.GET, entity, String.class);
        String json = httpResp.getBody();
        LOGGER.info("JSON [{}]", json);
        LOGGER.info("STATUS [{}]", httpResp.getStatusCode().toString());
        
        Map<String, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        map = mapper.readValue(json, new TypeReference<HashMap<String, Object>>(){});
        return map;
    }
    
}
