/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.teleanjar.ppobws.isoserver;


import id.co.teleanjar.ppobws.restclient.RestClientService;
import id.co.teleanjar.ppobws.restclient.User;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOServer;
import org.jpos.iso.ISOSource;
import org.jpos.util.Log4JListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 *
 * @author moe
 */
public class IsoGateway implements ISORequestListener  {
    private static final Logger log = LoggerFactory.getLogger(IsoGateway.class);
            
    private Integer port=9078;
    private ISOServer isoServer=null;
    
    @Autowired private RestClientService restClient;
    
            
    @PostConstruct
    public void init() {
       try {
           log.info("Iso Gateway Running ...");
           org.jpos.util.Logger jposLogger = new org.jpos.util.Logger();
           Log4JListener log4JListener = new Log4JListener();
           log4JListener.setLevel("info");
           jposLogger.addListener(log4JListener);

           TeleChannel channel =  new TeleChannel(new TelePackager());
           channel.setLogger(jposLogger, "isolog.teleanjar.gateway");
           
           isoServer = new ISOServer(port, channel, null);
           isoServer.setLogger(jposLogger, "isolog.teleanjar.gateway");
           isoServer.addISORequestListener(this);
           
           new Thread(isoServer).start();
           log.info("TELE ISO Server started at Port : {} ", port);
           
       } catch (Exception e) {
           log.info("TELE ISO Server gagal running.");
           log.error(e.getMessage(), e);
       } 
                
    } 
            
    @Override
    public boolean process(ISOSource isoSource, ISOMsg isoMsg) {
        try {
            if(!isoMsg.hasFields()) return false;
            
            String mti = isoMsg.getMTI();
            
            if (MTIConstants.NETWORK_MANAGEMENT_REQUEST.equalsIgnoreCase(mti)) {
                return handleNetman(isoSource, isoMsg);
            }         
            
            if (MTIConstants.INQUIRY_REQUEST.equalsIgnoreCase(mti)) {
                return handleInquiry(isoSource, isoMsg);
            }
        } catch (Exception e) {
            log.info("process ISO Message gagal.");
            log.error(e.getMessage(), e);
        }
        return false;
    }

    private boolean handleNetman(ISOSource isoSource, ISOMsg isoMsg) throws ISOException, IOException {
        ISOMsg response = (ISOMsg) isoMsg.clone();
        try {
            
            String bit33 = isoMsg.getString(33);
            
            if (bit33.equals("1234567")) {
                response.setMTI(MTIConstants.NETWORK_MANAGEMENT_RESPONSE);
                response.set(39, "00");
            }
            else {
                response.setMTI(MTIConstants.NETWORK_MANAGEMENT_RESPONSE);
                response.set(39, "98");
            }
            
        } catch (ISOException ex) {
            response.setMTI(MTIConstants.NETWORK_MANAGEMENT_RESPONSE);
            response.set(39, "99");
        }
        isoSource.send(response);
        return true;
    }
    
    private boolean handleInquiry(ISOSource isoSource, ISOMsg isoMsg) throws ISOException, IOException {
        ISOMsg response = (ISOMsg) isoMsg.clone();
        try {
            
            String bit48 = isoMsg.getString(48);
            
            String idpel = isoMsg.getString(48).trim();
            String idloket = isoMsg.getString(41).trim();
            String merchantCode = isoMsg.getString(18).trim();
            
            if (StringUtils.hasText(bit48)) {
                
                //request user
//                User user = restClient.getUser(bit48);
//                
                
                Map<String, Object> obj = restClient.inquiry(idpel, idloket, merchantCode);
                
                if (!obj.get("rc").toString().equals("00")) {
                    response.setMTI(MTIConstants.INQUIRY_RESPONSE);                
                    response.set(39, obj.get("rc").toString());
                } else {
                    Map<String, Object> data = (Map<String, Object>) obj.get("data");

                    StringBuilder bit48Builder = new StringBuilder();
                    bit48Builder.append(data.get("idpel").toString());
                    bit48Builder.append(org.apache.commons.lang.StringUtils.rightPad(data.get("nama").toString(), 25, " ") );
                    
                    response.setMTI(MTIConstants.INQUIRY_RESPONSE);                
                    response.set(39, "00");
                    response.set(48, bit48Builder.toString());   
                }
            }
            else {
                response.setMTI(MTIConstants.INQUIRY_RESPONSE);
                response.set(39, "90");
            }
            
        } catch (Exception ex) {
            response.setMTI(MTIConstants.INQUIRY_RESPONSE);
            response.set(39, "99");
        }
        isoSource.send(response);
        return true;
    }
    
}
