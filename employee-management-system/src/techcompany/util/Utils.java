
package techcompany.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import techcompany.entities.Response;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;
import techcompany.entities.Response;

public class Utils {

    public static final byte[] SELECT_APPLET = new byte[] { 0x11, 0x22, 0x33, 0x44, 0x55, 0x01 };

    // public PublicKey publicKey =

    private static CardChannel cardChannel = null; // Duy trì CardChannel toàn cục

    public Utils() {
    }

    public static Response connectCardAndGetID() {
        try {
            if (cardChannel == null) {
                TerminalFactory factory = TerminalFactory.getDefault();
                List<CardTerminal> terminals = factory.terminals().list();
                if (terminals.isEmpty()) {
                    return new Response(Constant.UNKNOWN_ERROR, "No card terminal found!");
                } else {
                    CardTerminal terminal = terminals.get(0);
                    Card card = terminal.connect("T=0");
                    cardChannel = card.getBasicChannel(); // Duy trì CardChannel
                    if (cardChannel == null) {
                        return new Response(Constant.CHANEL_NULL, "Channel is null");
                    }
                    Response selectAppletResponse = selectApplet(cardChannel);
                    if (selectAppletResponse.getErrorCode() != Constant.SUCCESS) {
                        return selectAppletResponse;
                    }
                }
            }
            return sendCommand(cardChannel);
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

            if (idResponse.getSW() == 0x9000) {
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

    public static Response saveAndGetData(byte ins, byte lc, byte[] data) {
        try {
            if (cardChannel == null) {
                return new Response(Constant.UNKNOWN_ERROR, "No card channel available!");
            }
            CommandAPDU commandAPDU = new CommandAPDU(0x00, ins, 0x00, 0x00, data);
            ResponseAPDU responseAPDU = cardChannel.transmit(commandAPDU);

            if (responseAPDU.getSW() == 0x9000) {
                byte[] responseData = responseAPDU.getData();
                return new Response(Constant.SUCCESS, hexToString(bytesToHex(responseData)));
            } else {
                return new Response(Constant.UNKNOWN_ERROR,
                        "Failed to send data, SW=" + Integer.toHexString(responseAPDU.getSW()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(Constant.UNKNOWN_ERROR, "Exception during SEND DATA: " + e.getMessage());
        }
    }

    public static Response getData(byte ins) {
        try {
            if (cardChannel == null) {
                return new Response(Constant.UNKNOWN_ERROR, "No card channel available!");
            }
            CommandAPDU commandAPDU = new CommandAPDU(0x00, ins, 0x00, 0x00);
            ResponseAPDU responseAPDU = cardChannel.transmit(commandAPDU);

            if (responseAPDU.getSW() == 0x9000) {
                byte[] responseData = responseAPDU.getData();
                return new Response(Constant.SUCCESS, hexToString(bytesToHex(responseData)));
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
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static String hexToString(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return new String(bytes, StandardCharsets.UTF_8);
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

    public static byte[] compressImageToTargetSize(BufferedImage image, int targetSizeInBytes) throws IOException {
        // Lấy ImageWriter cho định dạng JPG
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("Không tìm thấy ImageWriter cho định dạng JPG.");
        }
        ImageWriter writer = writers.next();

        // Bắt đầu thử với chất lượng nén cao nhất và giảm dần
        float quality = 1.0f;
        byte[] imageBytes = null;

        while (quality > 0.0f) {
            System.out.println("quality: " + quality);
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ImageOutputStream ios = ImageIO.createImageOutputStream(byteArrayOutputStream)) {
                // Thiết lập đầu ra cho ImageWriter
                writer.setOutput(ios);

                // Cấu hình nén
                ImageWriteParam param = writer.getDefaultWriteParam();
                if (param.canWriteCompressed()) {
                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    param.setCompressionQuality(quality); // Giảm chất lượng
                }

                // Ghi ảnh vào byte array
                writer.write(null, new javax.imageio.IIOImage(image, null, null), param);

                // Lấy dữ liệu ảnh nén
                imageBytes = byteArrayOutputStream.toByteArray();
                System.out.println("imageBytes: " + imageBytes.length);

                // Kiểm tra kích thước byte[]
                if (imageBytes.length <= targetSizeInBytes) {
                    break; // Nếu đạt mục tiêu, thoát khỏi vòng lặp
                }
            }

            // Giảm chất lượng nén thêm (ví dụ: giảm 10%)
            quality -= quality * 0.5f;
        }

        writer.dispose();

        // Trả về byte[] nếu đạt mục tiêu, ngược lại trả về null
        return (imageBytes != null && imageBytes.length <= targetSizeInBytes) ? imageBytes : null;
    }
}
