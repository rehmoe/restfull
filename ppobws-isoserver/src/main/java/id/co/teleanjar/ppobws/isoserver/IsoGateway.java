/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.teleanjar.ppobws.isoserver;


import java.io.IOException;
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

/**
 *
 * @author moe
 */
public class IsoGateway implements ISORequestListener  {
    private static final Logger log = LoggerFactory.getLogger(IsoGateway.class);
            
    private Integer port=9078;
    private ISOServer isoServer=null;
    
    @PostConstruct
    public void init() {
       try {
           log.info("Iso Gateway Running ...");
           org.jpos.util.Logger jposLogger = new org.jpos.util.Logger();
           Log4JListener log4JListener = new Log4JListener();
           log4JListener.setLevel("info");
           jposLogger.addListener(log4JListener);

           TeleChannel channel =  new TeleChannel(new TelePackager());
           channel.setLogger(jposLogger, "TELE Channel");
           
           isoServer = new ISOServer(port, channel, null);
           isoServer.setLogger(jposLogger, "TELE ISO Server");
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
        } catch (Exception e) {
            log.info("process ISO Message gagal.");
            log.error(e.getMessage(), e);
        }
        return false;
    }

    private boolean handleNetman(ISOSource isoSource, ISOMsg isoMsg) throws ISOException, IOException {
        ISOMsg response = (ISOMsg) isoMsg.clone();
        try {
            response.setMTI(MTIConstants.NETWORK_MANAGEMENT_RESPONSE);
            response.set(39, "00");
        } catch (ISOException ex) {
            response.setMTI(MTIConstants.NETWORK_MANAGEMENT_RESPONSE);
            response.set(39, "99");
        }
        isoSource.send(response);
        return true;
    }
    
}
