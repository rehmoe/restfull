/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */ 

//simulator iso-client (oriented method)

package id.co.teleanjar.ppobws.isoserver;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;
import java.util.logging.Level;
import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMUX;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISORequest;
import org.jpos.util.Log4JListener;
import org.jpos.util.LogSource;
import static org.junit.Assert.assertNotNull;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

/**
 *
 * @author adi
 */
public class EchoTestConnLess {
    static Logger LOGGER = LoggerFactory.getLogger(EchoTestConnLess.class);
    public static final DateTimeFormatter formatterBit7 = DateTimeFormatter.ofPattern("MMddHHmmss").withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter formatterBit11 = DateTimeFormatter.ofPattern("yyMMdd").withZone(ZoneId.systemDefault());

    
    public Integer random10Digit() {
        Random rnd = new Random();
        Integer n = rnd.nextInt(999999);
        return n;
    }
    
    
    public TeleChannel createChannel() throws Exception {

        org.jpos.util.Logger logger = new org.jpos.util.Logger();
        Log4JListener log4JListener = new Log4JListener();
        log4JListener.setLevel("info");
        logger.addListener(log4JListener);
        System.out.println("Starting ISO-8583 connection");
        
        
        TeleChannel teleChannel = new TeleChannel("localhost", 9078, new TelePackager());
        teleChannel.setLogger(logger, "iso8583.tele.channel.connless");
        teleChannel.setTimeout(30000);
        return teleChannel;
    }
    
    @Test
    public void testSignOn() throws Exception {
        Instant sekarang = Instant.now();

        ISOMsg msg = createSignOnRequest(sekarang);
        ISOMsg response = sendMessage(msg, createChannel());
        
        assertNotNull(response);
        
        System.out.println("response messge");
        response.setPackager(new TelePackager());
        System.out.println("msg recv : " + new String(response.pack()));

    }

    private ISOMsg sendMessage(ISOMsg request, TeleChannel channel) {
        try {
            channel.connect();
            request.setPackager(new TelePackager());
            LOGGER.info("stream request : {} ", new String(request.pack()));
            channel.send(request);
            
            ISOMsg response = channel.receive();
            
            if (response == null) {
                LOGGER.error("REQ : TIMEOUT");
            } else {
                LOGGER.info("stream response : {} ", new String(response.pack()));
                LOGGER.info("RC : {}", response.getString(39));
            }
            channel.disconnect();
            
            return response;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        
        return null;
    }
    
    private ISOMsg createSignOnRequest(Instant sekarang) throws ISOException {
        ISOPackager packager = new TelePackager();

        String bit11 = formatterBit11.format(sekarang).concat(StringUtils.leftPad(random10Digit().toString(), 6, "0"));
        System.out.println("Bit11 " + bit11);
        
        ISOMsg msg = new ISOMsg();
        msg.setMTI("2800");
        msg.set(7, formatterBit7.format(sekarang));
        msg.set(11, bit11);
        msg.set(33, "1234577");
        msg.set(70, "001");

        msg.setPackager(packager);
        System.out.println("msg send : " + new String(msg.pack()));
        System.out.println("queue msg sending");
        return msg;
    }
}
