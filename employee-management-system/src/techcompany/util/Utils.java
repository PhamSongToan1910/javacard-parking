
package techcompany.util;

import java.awt.image.BufferedImage;
import java.io.*;

import javafx.scene.image.Image;
import techcompany.entities.Response;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
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
import javax.crypto.Cipher;

public class Utils {

    public static final byte[] SELECT_APPLET = new byte[] { 0x11, 0x22, 0x33, 0x44, 0x55, 0x01 };

     public static PublicKey publicKey;

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

    public static Response saveImage(byte ins, byte lc, byte[] data) {
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();
            if (terminals.isEmpty()) {
                return new Response(Constant.UNKNOWN_ERROR, "No card terminal found!");
            } else {
                CardTerminal terminal = terminals.get(0);
                Card card = terminal.connect("T=1");
                CardChannel cardChannel1 = card.getBasicChannel();
                System.out.println("Connected with protocol: " + card.getProtocol());
                if (cardChannel1 == null) {
                    return new Response(Constant.UNKNOWN_ERROR, "No card channel available!");
                }
                CommandAPDU commandAPDU = new CommandAPDU(0x00, ins, 0x00, 0x00, data, 256);
                ResponseAPDU responseAPDU = cardChannel1.transmit(commandAPDU);

                if (responseAPDU.getSW() == 0x9000) {
                    byte[] responseData = responseAPDU.getData();
                    return new Response(Constant.SUCCESS, hexToString(bytesToHex(responseData)));
                } else {
                    return new Response(Constant.UNKNOWN_ERROR,
                            "Failed to send data, SW=" + Integer.toHexString(responseAPDU.getSW()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(Constant.UNKNOWN_ERROR, "Exception during SEND DATA: " + e.getMessage());
        }
    }

    public static Response login(byte ins, byte lc, byte[] data) {
        try {
            if (cardChannel == null) {
                return new Response(Constant.UNKNOWN_ERROR, "No card channel available!");
            }
            CommandAPDU commandAPDU = new CommandAPDU(0x00, ins, 0x00, 0x00, data);
            ResponseAPDU responseAPDU = cardChannel.transmit(commandAPDU);
            if (responseAPDU.getSW() == 0x9000) {
                byte[] responseData = responseAPDU.getData();

                Signature signature = Signature.getInstance("SHA1withRSA"); // Hoặc "SHA256withRSA" nếu dùng SHA-256
                signature.initVerify(publicKey);
                signature.update("SUCCESS".getBytes());
                if(signature.verify(responseData)){
                     commandAPDU = new CommandAPDU(0x00, 0x0A, 0x00, 0x00);
                     responseAPDU = cardChannel.transmit(commandAPDU);

                    if (responseAPDU.getSW() == 0x9000) {
                        responseData = responseAPDU.getData();
                        return new Response(Constant.SUCCESS, hexToString(bytesToHex(responseData)));
                    } else {
                        return new Response(Constant.UNKNOWN_ERROR,
                                "Failed to send data, SW=" + Integer.toHexString(responseAPDU.getSW()));
                    }
                }else {
                    return new Response(Constant.UNKNOWN_ERROR,
                            "Failed to send data, SW=" + Integer.toHexString(responseAPDU.getSW()));
                }
            } else {
                return new Response(Constant.UNKNOWN_ERROR,
                        "Failed to send data, SW=" + Integer.toHexString(responseAPDU.getSW()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(Constant.UNKNOWN_ERROR, "Exception during SEND DATA: " + e.getMessage());
        }
    }

    public static Response saveAndGetRSA(byte ins, byte lc, byte[] data) {
        try {
            if (cardChannel == null) {
                return new Response(Constant.UNKNOWN_ERROR, "No card channel available!");
            }
            CommandAPDU commandAPDU = new CommandAPDU(0x00, ins, 0x00, 0x00, data);
            ResponseAPDU responseAPDU = cardChannel.transmit(commandAPDU);

            if (responseAPDU.getSW() == 0x9000) {
                byte[] responseData = responseAPDU.getData();
                processAPDUResponse(responseData);
                System.out.println("Public Key: " + publicKey);
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

    public static void generatePublicKey(byte[] modulus, byte[] exponent) throws Exception {
        BigInteger mod = new BigInteger(1, modulus);
        BigInteger exp = new BigInteger(1, exponent);
        RSAPublicKeySpec spec = new RSAPublicKeySpec(mod, exp);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        publicKey = factory.generatePublic(spec);
    }

    public static void processAPDUResponse(byte[] apduResponse) throws Exception {
        int modLen = 128;
        int expLen = apduResponse.length - modLen;

        byte[] modulus = new byte[modLen];
        System.arraycopy(apduResponse, 0, modulus, 0, modLen);

        byte[] exponent = new byte[expLen];
        System.arraycopy(apduResponse, modLen, exponent, 0, expLen);
        generatePublicKey(modulus, exponent);
    }

    public static byte[] encryptData(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data.getBytes("UTF-8"));
    }

    public static Response changePassword(byte ins, byte[] data) {
        try {
            if (cardChannel == null) {
                return new Response(Constant.UNKNOWN_ERROR, "No card channel available!");
            }
            CommandAPDU commandAPDU = new CommandAPDU(0x00, ins, 0x00, 0x00, data);
            ResponseAPDU responseAPDU = cardChannel.transmit(commandAPDU);

            if (responseAPDU.getSW() == 0x9000) {
                return new Response(Constant.SUCCESS, "SUCCESS");
            } else {
                return new Response(Constant.UNKNOWN_ERROR,
                        "Failed to send data, SW=" + Integer.toHexString(responseAPDU.getSW()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(Constant.UNKNOWN_ERROR, "Exception during SEND DATA: " + e.getMessage());
        }
    }

    public static Response saveAndGetMonney(byte ins, byte lc, byte[] data) {
        try {
            if (cardChannel == null) {
                return new Response(Constant.UNKNOWN_ERROR, "No card channel available!");
            }

            CommandAPDU commandAPDU = new CommandAPDU(0x00, ins, 0x00, 0x00, data);
            ResponseAPDU responseAPDU = cardChannel.transmit(commandAPDU);

            if (responseAPDU.getSW() == 0x9000) {
                byte[] responseData = responseAPDU.getData();
                if (responseData.length >= 4) {
                    byte[] balanceBytes = Arrays.copyOfRange(responseData, 0, 4); // Lấy 4 byte đầu (số dư)
                    int balance = ByteBuffer.wrap(balanceBytes).getInt(); // Chuyển 4 byte thành số nguyên
                    return new Response(Constant.SUCCESS, String.valueOf(balance));
                } else {
                    return new Response(Constant.UNKNOWN_ERROR, "Invalid response data length.");
                }
            } else {
                return new Response(Constant.UNKNOWN_ERROR,
                        "Failed to send data, SW=" + Integer.toHexString(responseAPDU.getSW()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(Constant.UNKNOWN_ERROR, "Exception during SEND DATA: " + e.getMessage());
        }
    }

    public static Response getMonney(byte ins) {
        try {
            if (cardChannel == null) {
                return new Response(Constant.UNKNOWN_ERROR, "No card channel available!");
            }

            CommandAPDU commandAPDU = new CommandAPDU(0x00, ins, 0x00, 0x00);
            ResponseAPDU responseAPDU = cardChannel.transmit(commandAPDU);

            if (responseAPDU.getSW() == 0x9000) {
                byte[] responseData = responseAPDU.getData();
                String hexString = bytesToHex(responseData);
                String responseDataString = hexToString(hexString);

                if (responseData.length >= 4) {
                    byte[] check = Arrays.copyOfRange(responseData,0,1);
                    System.out.println(check[0]);
                    int balance;
                    if(check[0] == 64){
                        balance = 0;
                    }
                    else{
                        byte[] balanceBytes = Arrays.copyOfRange(responseData, 0, 4); // Lấy 4 byte đầu (số dư)
                        balance = ByteBuffer.wrap(balanceBytes).getInt(); // Chuyển 4 byte thành số nguyên
                    }
                    return new Response(Constant.SUCCESS, String.valueOf(balance));
                } else {
                    return new Response(Constant.UNKNOWN_ERROR, "Invalid response data length.");
                }
            } else {
                return new Response(Constant.UNKNOWN_ERROR,
                        "Failed to send data, SW=" + Integer.toHexString(responseAPDU.getSW()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(Constant.UNKNOWN_ERROR, "Exception during SEND DATA: " + e.getMessage());
        }
    }

    public static Response sendImageInChunks(byte[] imageBytes) {
        int chunkSize = 256;
        int totalChunks = (int) Math.ceil(imageBytes.length / (double) chunkSize);

        byte ins = (byte) 0x07;
        byte lc;

        for (int i = 0; i < totalChunks; i++) {
            int start = i * chunkSize;
            int end = Math.min((i + 1) * chunkSize, imageBytes.length);

            byte[] chunkData = Arrays.copyOfRange(imageBytes, start, end);
            lc = (byte) chunkData.length;

            byte[] data = new byte[chunkData.length/2 + 2];
            data[0] = (byte) i;
            data[1] = (byte) totalChunks;
            System.arraycopy(chunkData, 0, data, 2, chunkData.length/2);

            Response response = Utils.saveAndGetData(ins, lc, data);
            if (response.errorCode != Constant.SUCCESS) {
                return new Response(Constant.UNKNOWN_ERROR, "Error sending chunk " + i);
            }
        }

        return new Response(Constant.SUCCESS, "Image sent in chunks successfully.");
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
            quality = 0.5f;
        }

        writer.dispose();

        // Trả về byte[] nếu đạt mục tiêu, ngược lại trả về null
        return (imageBytes != null && imageBytes.length <= targetSizeInBytes) ? imageBytes : null;
    }

    public static Image convertByteToImage(byte[] imageBytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
        return new Image(bais);
    }
}
