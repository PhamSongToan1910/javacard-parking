package techcompany.util;

import techcompany.entities.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.TerminalFactory;

public class Utils {

    public static final byte[] SELECT_APPLET= {(byte)0x11, (byte)0x22, (byte)0x33, (byte)0x44, (byte)0x55, (byte)0x01};

    public static Response connectCardAndGetID() {
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();
            if (terminals.isEmpty()) {
                return new Response(Constant.UNKNOWN_ERROR,"No card terminal found!");
            }

            CardTerminal terminal = terminals.get(0);
            Card card = terminal.connect("T=0"); // Kết nối với thẻ qua giao thức T=0
            CardChannel channel = card.getBasicChannel();
            if (channel == null) {
                return new Response(Constant.CHANEL_NULL, "Channel is null");
            }

            ResponseAPDU responseAPDU = channel.transmit(new CommandAPDU(0x00, 0xA4, 0x04, 0x00, SELECT_APPLET));
            String check = Integer.toHexString(responseAPDU.getSW());
            if (check.equals("9000")) {
                // Gửi lệnh GET_ID để lấy ID từ applet
                byte[] GET_ID_COMMAND = new byte[] {
                        (byte) 0x00, // CLA (Class)
                        (byte) 0x00, // INS (Instruction for "Get Data")
                        (byte) 0x00, // P1
                        (byte) 0x00// Le (Expected length of data)
                };
                ResponseAPDU idResponse = channel.transmit(new CommandAPDU(GET_ID_COMMAND));

                // Kiểm tra mã trạng thái trả về
                if (idResponse.getSW() == 0x9000) {
                    // Chuyển dữ liệu nhận được thành chuỗi hoặc số
                    byte[] idData = idResponse.getData();
                    return new Response(Constant.SUCCESS, bytesToHex(idData));
                } else {
                    return new Response(Constant.UNKNOWN_ERROR,"Failed to get ID, SW=" + Integer.toHexString(idResponse.getSW()));
                }
            } else if (check.equals("6400")) {
                return new Response(Constant.INVALID_CARD, "Invalid Card");
            } else {
                return new Response(Constant.UNKNOWN_ERROR, "Unknown error during SELECT, SW=" + check);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return new Response(Constant.UNKNOWN_ERROR, "Exception occurred: " + exception.getMessage());
        }
    }

    public static Response sendData(byte ins, byte lc, byte[] data) {
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();
            if (terminals.isEmpty()) {
                return new Response(Constant.UNKNOWN_ERROR,"No card terminal found!");
            }

            CardTerminal terminal = terminals.get(0);
            Card card = terminal.connect("T=0"); // Kết nối với thẻ qua giao thức T=0
            CardChannel channel = card.getBasicChannel();
            if (channel == null) {
                return new Response(Constant.CHANEL_NULL, "Channel is null");
            }

            ResponseAPDU responseAPDU = channel.transmit(new CommandAPDU(0x00, 0xA4, 0x04, 0x00, SELECT_APPLET));
            String check = Integer.toHexString(responseAPDU.getSW());
            if (check.equals("9000")) {
                // Gửi lệnh GET_ID để lấy ID từ applet
                byte[] GET_ID_COMMAND = new byte[] {
                        (byte) 0x00, // CLA (Class)
                        ins, // INS (Instruction for "Get Data")
                        (byte) 0x00, // P1
                        (byte) 0x00,
                        lc
                };

                byte[] combined = new byte[GET_ID_COMMAND.length + data.length];

                for (int i = 0; i < combined.length; ++i)
                {
                    combined[i] = i < GET_ID_COMMAND.length ? GET_ID_COMMAND[i]  : data[i - GET_ID_COMMAND.length];
                }
                ResponseAPDU idResponse = channel.transmit(new CommandAPDU(GET_ID_COMMAND));

                // Kiểm tra mã trạng thái trả về
                if (idResponse.getSW() == 0x9000) {
                    // Chuyển dữ liệu nhận được thành chuỗi hoặc số
                    byte[] idData = idResponse.getData();
                    return new Response(Constant.SUCCESS, bytesToHex(idData));
                } else {
                    return new Response(Constant.UNKNOWN_ERROR,"Failed to get ID, SW=" + Integer.toHexString(idResponse.getSW()));
                }
            } else if (check.equals("6400")) {
                return new Response(Constant.INVALID_CARD, "Invalid Card");
            } else {
                return new Response(Constant.UNKNOWN_ERROR, "Unknown error during SELECT, SW=" + check);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return new Response(Constant.UNKNOWN_ERROR, "Exception occurred: " + exception.getMessage());
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

}
