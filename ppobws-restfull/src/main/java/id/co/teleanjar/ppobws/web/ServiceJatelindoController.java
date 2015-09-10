/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.teleanjar.ppobws.web;

import id.co.teleanjar.ppobws.domain.TagihanPostpaid;
import id.co.teleanjar.ppobws.domain.User;
import id.co.teleanjar.ppobws.jatelindo.ServiceJatelindo;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/produk")
public class ServiceJatelindoController {
    @Autowired private ServiceJatelindo serviceJatelindo;
    
    
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Object> inquiry(
            @RequestParam("idpel") String idpel, 
            @RequestParam("idloket") String idloket,
            @RequestParam("merchantcode") String merchantCode ) {
        
        Map<String, Object> resObj = new HashMap<String, Object>();
        
        try {
            
            TagihanPostpaid tagihan = serviceJatelindo.inquiryRequest(idpel, idloket, merchantCode);
            
            resObj.put("rc", "00");
            resObj.put("message", "Sukses");
            resObj.put("data", tagihan);
                    
        } catch (Exception ex) {
            String message[] = ex.getMessage().split(";");
            
            resObj.put("rc", message[0]);
            resObj.put("message", message.length>1 ? message[1] : "Unknown Error");
        }
        return resObj;
    }
}
