package id.co.teleanjar.ppobws.jatelindo.iso8583;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.BaseChannel;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOUtil;

public class JatelindoChannel extends BaseChannel {

    public static final String DEFAULT_DATE_FORMAT = "yyyyMMddHHmmss";
    private static final Log LOGGER = LogFactory.getLog(JatelindoChannel.class);
    private static final int MAX_MSG_LENGTH = 4096;
    private static final Integer MSG_TRAILER = 0x03;
    private boolean tandemMode = true;
    private byte[] trailler = new BigInteger("03", 16).toByteArray();


    @Override
    protected void sendMessageTrailler(ISOMsg m, int len) throws IOException {
        serverOut.write(MSG_TRAILER);
    }

    @Override
    protected int getMessageLength() throws IOException, ISOException {
        int l = 0;
        int msglength = 0;
        byte[] b = new byte[2];
        while (l == 0) {
            serverIn.readFully(b, 0, 2);
            msglength = (Integer.parseInt(ISOUtil.hexString(b, 0, 1), 16) * 256)
                    + (Integer.parseInt(ISOUtil.hexString(b, 1, 1), 16));
            msglength -= tandemMode ? 0 : 2;
            try {
                if ((l = msglength) == 0) {
                    serverOut.write(b);
                    serverOut.flush();
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                throw new ISOException("Invalid message length "
                        + new String(b) + "length=" + l);
            }
        }
        return l;
    }

    @Override
    protected void sendMessageLength(int len) throws IOException {
        len += tandemMode ? 0 : 2;
        len += trailler != null ? trailler.length : 0;
        int b0 = len / 256;
        int b1 = len % 256;
        byte[] b = new byte[2];
        b[0] = Hexadecimal.parseByte(Integer.toHexString(b0));
        b[1] = Hexadecimal.parseByte(Integer.toHexString(b1));

        serverOut.write(b);
    }

    @Override
    protected byte[] streamReceive() throws IOException {
        byte[] buf = new byte[MAX_MSG_LENGTH];
        int len = 0;
        for (len = 0; len < MAX_MSG_LENGTH; len++) {
            int data = serverIn.read();
            if (data == MSG_TRAILER) {
                break;
            }
            buf[len] = (byte) data;
        }
        if (len == MAX_MSG_LENGTH) {
            throw new IOException("packet too long");
        }

        byte[] d = new byte[len];
        System.arraycopy(buf, 0, d, 0, len);
        return d;
    }

    @Override
    public ISOMsg receive() throws IOException, ISOException {
        try {
            return super.receive();
        } catch (SocketTimeoutException err) {
            // tidak dapat response sampai timeout, gpp return null saja
            return null;
        }
    }

    // constructor default override
    public JatelindoChannel(ISOPackager p, ServerSocket serverSocket) throws IOException {
        super(p, serverSocket);
    }

    public JatelindoChannel(ISOPackager p) throws IOException {
        super(p);
    }

    public JatelindoChannel(String host, int port, ISOPackager p) {
        super(host, port, p);
    }

    public JatelindoChannel() {
    }
}
