/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.teleanjar.ppobws.isoserver;


import javax.annotation.PostConstruct;
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
       log.info("Iso Gateway Running ...");
       org.jpos.util.Logger jposLogger = new org.jpos.util.Logger();
        Log4JListener log4JListener = new Log4JListener();
        log4JListener.setLevel("info");
        jposLogger.addListener(log4JListener);
    } 
            
    @Override
    public boolean process(ISOSource isos, ISOMsg isomsg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
