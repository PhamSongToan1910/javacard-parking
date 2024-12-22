
package techcompany.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import techcompany.entities.Response;

import java.security.PublicKey;
import java.security.Signature;
import java.util.List;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;
import techcompany.entities.Response;

public class Utils {
    public static final byte[] SELECT_APPLET = new byte[] { 0x11, 0x22, 0x33, 0x44, 0x55, 0x01 };

    public Utils() {
    }

    // public PublicKey publicKey =

    // public PublicKey publicKey =

    public static Response connectCardAndGetID() {
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();
            if (terminals.isEmpty()) {
                return new Response(Constant.UNKNOWN_ERROR, "No card terminal found!");
            } else {
                CardTerminal terminal = terminals.get(0);
                Card card = terminal.connect("T=0");
                CardChannel channel = card.getBasicChannel();

                if (channel == null) {
                    return new Response(Constant.CHANEL_NULL, "Channel is null");
                }
                Response selectAppletResponse = selectApplet(channel);
                if (selectAppletResponse.getErrorCode() != Constant.SUCCESS) {
                    return selectAppletResponse;
                }
                return sendCommand(channel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(Constant.UNKNOWN_ERROR, "Exception occurred: " + e.getMessage());
        }
    }

    private static Response selectApplet(CardChannel channel) {
        try {
            ResponseAPDU responseAPDU = channel.transmit(new CommandAPDU(0x00, 0xA4, 0x04, 0x00, SELECT_APPLET));
            String statusWord = Integer.toHexString(responseAPDU.getSW());

            if ("9000".equals(statusWord)) {
                return new Response(Constant.SUCCESS, "Applet selected successfully");
            } else if ("6400".equals(statusWord)) {
                return new Response(Constant.INVALID_CARD, "Invalid Card");
            } else {
                return new Response(Constant.UNKNOWN_ERROR, "Unknown error during SELECT, SW=" + statusWord);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(Constant.UNKNOWN_ERROR, "Exception during SELECT: " + e.getMessage());
        }
    }

    public static Response sendCommand(CardChannel channel) {
        try {
            byte[] GET_ID_COMMAND = new byte[] { 0, 0, 0, 0 };
            ResponseAPDU idResponse = channel.transmit(new CommandAPDU(GET_ID_COMMAND));

            if (idResponse.getSW() == 0x9000) { // Success
                byte[] idData = idResponse.getData();
                return new Response(Constant.SUCCESS, bytesToHex(idData));
            } else {
                return new Response(Constant.UNKNOWN_ERROR,
                        "Failed to get ID, SW=" + Integer.toHexString(idResponse.getSW()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(Constant.UNKNOWN_ERROR, "Exception during GET ID: " + e.getMessage());
        }
    }

    public static Response sendData(byte ins, byte lc, byte[] data) {
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();

            if (terminals.isEmpty()) {
                return new Response(Constant.UNKNOWN_ERROR, "No card terminal found!");
            }

            CardTerminal terminal = terminals.get(0);
            Card card = terminal.connect("T=0");
            CardChannel channel = card.getBasicChannel();

            byte[] commandData = new byte[1 + data.length];
            commandData[0] = lc;
            System.arraycopy(data, 0, commandData, 1, data.length);
            CommandAPDU commandAPDU = new CommandAPDU(0x00, ins, 0x00, 0x00, commandData);
            ResponseAPDU responseAPDU = channel.transmit(commandAPDU);

            if (responseAPDU.getSW() == 0x9000) { // Success
                byte[] responseData = responseAPDU.getData();
                return new Response(Constant.SUCCESS, bytesToHex(responseData));
            } else {
                return new Response(Constant.UNKNOWN_ERROR,
                        "Failed to send data, SW=" + Integer.toHexString(responseAPDU.getSW()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(Constant.UNKNOWN_ERROR, "Exception during SEND DATA: " + e.getMessage());
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        byte[] var2 = bytes;
        int var3 = bytes.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            byte b = var2[var4];
            sb.append(String.format("%02X", b));
        }

        return sb.toString();
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            return byteArrayOutputStream.toByteArray();
        }
    }

    public boolean verifySignature(byte[] message, byte[] signature, PublicKey publicKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(message);
        return sig.verify(signature);
    }


}
