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
import org.jpos.iso.ISOUtil;
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
        
        int l = 0;
        int msgLength = 0;
        
        byte[] b = new byte[2];
        while (l==0) {
            serverIn.readFully(b,0,2);
            msgLength = ( Integer.parseInt(ISOUtil.hexString(b, 0, 1), 16) * 256 ) + 
                        ( Integer.parseInt(ISOUtil.hexString(b, 1, 1), 16) );
            try {
                if ((l=msgLength) == 0) {
                    serverOut.write(b);
                    serverOut.flush();
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                throw new ISOException("Invalid message length " + new String(b) + "length : "+l);
            }
        }
        
        return l;
    }

    @Override
    protected void sendMessageTrailler(ISOMsg m, byte[] b) throws IOException {
        logger.debug("Send Message Trailer : {} ", MSG_TRAILER);
        serverOut.write(MSG_TRAILER);
    }

    @Override
    protected void sendMessageLength(int len) throws IOException {
        int b0 = (len / 256);
        int b1 = (len % 256);
        
        byte[] b = new byte[2];
        
        b[0] = Hexadecimal.parseByte(Integer.toHexString(b0));
        b[1] = Hexadecimal.parseByte(Integer.toHexString(b1));
        
        logger.debug("Send Message header : {} ", new String(b));
        serverOut.write(b);
    }
    
    
}
