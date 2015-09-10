/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.teleanjar.ppobws.jatelindo;

import id.co.teleanjar.ppobws.domain.TagihanPostpaid;
import java.util.Map;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author moe
 */

public class ServiceJatelindoJDBC {
    private final DataSource datasource;
    
    static Logger LOGGER = LoggerFactory.getLogger(ServiceJatelindoJDBC.class);

    public ServiceJatelindoJDBC(String host, int port, String dbName, String username, String password) {
        String DB_CONNECTION = "jdbc:mysql://"+host+":"+String.valueOf(port) +"/"+dbName;
        
        this.datasource = new DataSource();
        
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName("com.mysql.jdbc.Driver");
        datasource.setUrl(DB_CONNECTION);        
        datasource.setInitialSize(1);
        datasource.setMinIdle(1);
        datasource.setMaxIdle(2);
        datasource.setMaxActive(2);
        datasource.setMaxWait(40000);
        datasource.setTestOnBorrow(true);
        datasource.setValidationQuery("SELECT 1");
    }
    
    private JdbcTemplate getJdbcTemplate () {
        return new JdbcTemplate(this.datasource);
    }
    
    
    public TagihanPostpaid inquiryRequest(String idpel, String idloket, String merchantCategory) throws Exception {
        
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM billing ");
        query.append("WHERE idpel = ?" );
        
        try {
          Map<String, Object> result = getJdbcTemplate().queryForMap(query.toString(), idpel);
          for(String s : result.keySet()){
              LOGGER.info("{} : {}", s, result.get(s));
          }
          TagihanPostpaid tagihan = new TagihanPostpaid();
          tagihan.setIdpel(result.get("idpel").toString());
          tagihan.setNama(result.get("nama").toString());
          return tagihan;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new Exception("14;request timeout");
            
        }
        
        
    }
}
