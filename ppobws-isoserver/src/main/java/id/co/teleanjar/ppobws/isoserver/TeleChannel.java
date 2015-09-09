/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.teleanjar.ppobws.isoserver;

import java.io.IOException;
import java.net.ServerSocket;
import org.jpos.iso.BaseChannel;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author moe
 */
public class TeleChannel extends BaseChannel {
    private static final Logger logger = LoggerFactory.getLogger(TeleChannel.class);
    private static final int MSG_TRAILER = 255; 
    //cara otomatis bikin custroctor ALT+INS
    
    public TeleChannel() {
    }

    public TeleChannel(String host, int port, ISOPackager p) {
        super(host, port, p);
    }

    public TeleChannel(ISOPackager p) throws IOException {
        super(p);
    }

    public TeleChannel(ISOPackager p, ServerSocket serverSocket) throws IOException {
        super(p, serverSocket);
    }

    //cara otomatis bikin custroctor ALT+INS + pilih overide metho
    @Override
    protected int getMessageLength() throws IOException, ISOException {
        return super.getMessageLength(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void sendMessageTrailler(ISOMsg m, byte[] b) throws IOException {
        super.sendMessageTrailler(m, b); //To change body of generated methods, choose Tools | Templates.
        logger.debug("Send Message Trailer : {} ", MSG_TRAILER);
        serverOut.write(MSG_TRAILER);
    }

    @Override
    protected void sendMessageLength(int len) throws IOException {
        super.sendMessageLength(len); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
