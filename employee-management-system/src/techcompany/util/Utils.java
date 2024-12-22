//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package techcompany.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    public static final byte[] SELECT_APPLET = new byte[]{17, 34, 51, 68, 85, 1};

    public Utils() {
    }

    public static Response connectCardAndGetID() {
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();
            if (terminals.isEmpty()) {
                return new Response(Constant.UNKNOWN_ERROR, "No card terminal found!");
            } else {
                CardTerminal terminal = (CardTerminal)terminals.get(0);
                Card card = terminal.connect("T=0");
                CardChannel channel = card.getBasicChannel();
                if (channel == null) {
                    return new Response(Constant.CHANEL_NULL, "Channel is null");
                } else {
                    ResponseAPDU responseAPDU = channel.transmit(new CommandAPDU(0, 164, 4, 0, SELECT_APPLET));
                    String check = Integer.toHexString(responseAPDU.getSW());
                    if (check.equals("9000")) {
                        byte[] GET_ID_COMMAND = new byte[]{0, 0, 0, 0};
                        ResponseAPDU idResponse = channel.transmit(new CommandAPDU(GET_ID_COMMAND));
                        if (idResponse.getSW() == 36864) {
                            byte[] idData = idResponse.getData();
                            return new Response(Constant.SUCCESS, bytesToHex(idData));
                        } else {
                            return new Response(Constant.UNKNOWN_ERROR, "Failed to get ID, SW=" + Integer.toHexString(idResponse.getSW()));
                        }
                    } else {
                        return check.equals("6400") ? new Response(Constant.INVALID_CARD, "Invalid Card") : new Response(Constant.UNKNOWN_ERROR, "Unknown error during SELECT, SW=" + check);
                    }
                }
            }
        } catch (Exception var10) {
            Exception exception = var10;
            exception.printStackTrace();
            return new Response(Constant.UNKNOWN_ERROR, "Exception occurred: " + exception.getMessage());
        }
    }

    public static Response sendData(byte ins, byte lc, byte[] data) {
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();
            if (terminals.isEmpty()) {
                return new Response(Constant.UNKNOWN_ERROR, "No card terminal found!");
            } else {
                CardTerminal terminal = (CardTerminal)terminals.get(0);
                Card card = terminal.connect("T=0");
                CardChannel channel = card.getBasicChannel();
                if (channel == null) {
                    return new Response(Constant.CHANEL_NULL, "Channel is null");
                } else {
                    ResponseAPDU responseAPDU = channel.transmit(new CommandAPDU(0, 164, 4, 0, SELECT_APPLET));
                    String check = Integer.toHexString(responseAPDU.getSW());
                    if (check.equals("9000")) {
                        byte[] GET_ID_COMMAND = new byte[]{0, ins, 0, 0, lc};
                        byte[] combined = new byte[GET_ID_COMMAND.length + data.length];

                        for(int i = 0; i < combined.length; ++i) {
                            combined[i] = i < GET_ID_COMMAND.length ? GET_ID_COMMAND[i] : data[i - GET_ID_COMMAND.length];
                        }

                        ResponseAPDU idResponse = channel.transmit(new CommandAPDU(GET_ID_COMMAND));
                        if (idResponse.getSW() == 36864) {
                            byte[] idData = idResponse.getData();
                            return new Response(Constant.SUCCESS, bytesToHex(idData));
                        } else {
                            return new Response(Constant.UNKNOWN_ERROR, "Failed to get ID, SW=" + Integer.toHexString(idResponse.getSW()));
                        }
                    } else {
                        return check.equals("6400") ? new Response(Constant.INVALID_CARD, "Invalid Card") : new Response(Constant.UNKNOWN_ERROR, "Unknown error during SELECT, SW=" + check);
                    }
                }
            }
        } catch (Exception var14) {
            Exception exception = var14;
            exception.printStackTrace();
            return new Response(Constant.UNKNOWN_ERROR, "Exception occurred: " + exception.getMessage());
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        byte[] var2 = bytes;
        int var3 = bytes.length;

        for(int var4 = 0; var4 < var3; ++var4) {
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
