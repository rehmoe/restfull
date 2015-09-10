/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.teleanjar.ppobws.jatelindo;

import id.co.teleanjar.ppobws.domain.TagihanPostpaid;
import id.co.teleanjar.ppobws.jatelindo.iso8583.JatelindoChannel;
import id.co.teleanjar.ppobws.jatelindo.iso8583.JatelindoPackager;
import java.util.Random;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMUX;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequest;
import org.jpos.util.Log4JListener;
import org.jpos.util.LogSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

/**
 *
 * @author moe
 */

public class ServiceJatelindo {
    static ISOMUX isoMux;
    private String host;
    private int port;
    static Logger LOGGER = LoggerFactory.getLogger(ServiceJatelindo.class);
    public static final DateTimeFormatter formatMMDDhhmmss = DateTimeFormatter.ofPattern("MMddHHmmss").withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter formathhmmss = DateTimeFormatter.ofPattern("HHmmss").withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter formatMMDD = DateTimeFormatter.ofPattern("MMdd").withZone(ZoneId.systemDefault());
    
    public Integer random6Digit() {
        Random rnd = new Random();
        Integer n = rnd.nextInt(999999);
        return n;
    }
    
    public ServiceJatelindo(String host, int port) {
        this.host = host;
        this.port = port;
        
        org.jpos.util.Logger logger = new org.jpos.util.Logger();
        Log4JListener log4JListener = new Log4JListener();
        log4JListener.setLevel("info");
        logger.addListener(log4JListener);
        System.out.println("Starting ISO-8583 connection");
        
        isoMux = new ISOMUX(new JatelindoChannel(this.host, this.port, new JatelindoPackager())) {
            protected String getKey(ISOMsg m) throws ISOException {
                if (m.getString(11) == null) {
                    return m.getString(12);
                }
                return m.getString(11);
            }
        };
        isoMux.setLogger(logger, "mux");
        new Thread(isoMux).start();
        ((LogSource) isoMux.getISOChannel()).setLogger(logger, "iso8583.jatelindo.channel");
    }
    
    private ISOMsg createInquiryRequest(String idpel, String idloket, String merchantCategory) throws ISOException {
        ISOMsg msg = new ISOMsg();
        msg.setMTI("0200");
        msg.set(2, idpel.substring(0,2) + "501");
        msg.set(3, "380000");
        msg.set(7, formatMMDDhhmmss.format(Instant.now()));
        msg.set(11, random6Digit().toString());
        msg.set(12, formathhmmss.format(Instant.now()));
        msg.set(13, formatMMDD.format(Instant.now()));
        msg.set(15, formatMMDD.format(LocalDateTime.now().plusDays(1)));
        msg.set(18, merchantCategory);
        msg.set(32, "36000");
        msg.set(37, "I54FA0011111");
        msg.set(41, idloket);
        msg.set(42, "111111112222222");
        msg.set(48, idpel);
        msg.set(49, "360");
        
        return msg;
    }
    
    public TagihanPostpaid inquiryRequest(String idpel, String idloket, String merchantCategory) throws Exception {
        Instant sekarang = Instant.now();

        ISOMsg msg = createInquiryRequest(idpel, idloket, merchantCategory);
        ISORequest request = new ISORequest(msg);
        isoMux.queue(request);
        
        ISOMsg response = request.getResponse(40000);
        
        if (response==null) {
            throw new Exception("14;request timeout");
        }
        
        response.setPackager(new JatelindoPackager());
        
        LOGGER.info("msg recv : " + new String(response.pack()));

        
        return tagihanPostpaid(response);
    }
    
    private TagihanPostpaid tagihanPostpaid(ISOMsg response) {
        TagihanPostpaid tagihan = new TagihanPostpaid();
        
        String bit48 = response.getString(48);
        
        tagihan.setIdpel(bit48.substring(0, 12));
        tagihan.setNama(bit48.substring(47, 72));
        
        return tagihan;
    }
}
