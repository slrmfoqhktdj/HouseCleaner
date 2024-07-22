package com.example.housecleaner02.serialport;

public class SerialFatDataUtil {
    private static final byte START_BIT = (byte) 0xAA;
    private static final byte RECV_START_BIT_2 = (byte) 0x98;
    private static final byte SEND_START_BIT_2 = (byte) 0x78;


    private static final byte END_BIT = (byte) 0xCC;
    private static final byte END_BIT_2 = 0x33;
    private static final byte END_BIT_3 = (byte) 0xC3;
    private static final byte END_BIT_4 = 0x3C;

    private static final int SEND_DATA_LENGTH = 8;

    private static byte[] cacheData = null;
    private static int CACHE_DATA_LENGTH = 0;
    private static int index = 0;

    private static volatile boolean isFinishCheck = false;

    /**
     * 串口数据校验
     *
     * @param buf 串口数据
     * @return byte[] 校验成功的串口数据
     */
    public synchronized static byte[] recvFatData(byte[] buf) {
        if (buf == null) {
            return null;
        }

        if (buf[0] == START_BIT && buf[1] == RECV_START_BIT_2) {
            int length = buf.length;
            if (length >= SEND_DATA_LENGTH
                    && buf[length - 1] == END_BIT_4
                    && buf[length - 2] == END_BIT_3
                    && buf[length - 3] == END_BIT_2
                    && buf[length - 4] == END_BIT) {
                return buf.clone();
            }
        }
        return null;
    }

    /**
     * 串口数据校验
     *
     * @param buf 串口数据
     * @return byte[] 校验成功的串口数据
     */
    public synchronized static byte[] recvFatImageData(byte[] buf) {
        if (buf == null) {
            return null;
        }

        if (isFinishCheck) {
            return null;
        }

        if ((buf.length == 1) || (buf[0] != START_BIT && buf[1] != RECV_START_BIT_2 && buf[1] != 0x14)) {
            int length = buf.length;

            if (cacheData == null) {
                index = 0;
                cacheData = new byte[83 * 330];
                CACHE_DATA_LENGTH = cacheData.length;
            }

            if (index + length < CACHE_DATA_LENGTH) {
                System.arraycopy(buf, 0, cacheData, index, length);
            } else {
                if (index < CACHE_DATA_LENGTH) {
                    System.arraycopy(buf, 0, cacheData, index, CACHE_DATA_LENGTH - index);
                }
                isFinishCheck = true;
            }

            index += length;
        }
        return index >= CACHE_DATA_LENGTH ? cacheData : null;
    }

    public static void resetFatData() {
        index = 0;
        cacheData = null;
        CACHE_DATA_LENGTH = 0;
        isFinishCheck = false;
    }

    /**
     * 发送串口数据
     *
     * @param cmd 命令
     * @return byte[] 串口数据
     */
    public static byte[] sendData(String cmd) {
        byte[] data = new byte[SEND_DATA_LENGTH];
        data[0] = START_BIT;
        data[1] = SEND_START_BIT_2;

        byte[] addrByte = hexString2byte(cmd);
        data[2] = addrByte[0];
        data[3] = addrByte[1];

        data[4] = END_BIT;
        data[5] = END_BIT_2;
        data[6] = END_BIT_3;
        data[7] = END_BIT_4;
        return data;
    }

    /**
     * 计算CRC16校验码
     *
     * @param data 字节数组
     * @return {@link String} 校验码
     */
    private static byte[] crc16(byte[] data) {
        byte[] buf = new byte[data.length];// 存储需要产生校验码的数据
        for (int i = 0; i < data.length; i++) {
            buf[i] = data[i];
        }
        int len = buf.length;
        int crc = 0xFFFF;//16位
        for (int pos = 0; pos < len; pos++) {
            if (buf[pos] < 0) {
                crc ^= (int) buf[pos] + 256; // XOR byte into least sig. byte of
                // crc
            } else {
                crc ^= (int) buf[pos]; // XOR byte into least sig. byte of crc
            }
            for (int i = 8; i != 0; i--) { // Loop over each bit
                if ((crc & 0x0001) != 0) { // If the LSB is set
                    crc >>= 1; // Shift right and XOR 0xA001
                    crc ^= 0xA001;
                } else
                    // Else LSB is not set
                    crc >>= 1; // Just shift right
            }
        }
        String c = Integer.toHexString(crc);
        if (c.length() == 3) {
            c = "0" + c;
        } else if (c.length() == 2) {
            c = "00" + c;
        } else if (c.length() == 1) {
            c = "000" + c;
        }
        c = c.substring(2, 4) + c.substring(0, 2);
        return hexString2byte(c);
    }

    private static byte[] hexString2byte(String hexString) {
        if (null == hexString || hexString.length() % 2 != 0 || hexString.contains("null")) {
            return null;
        }
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = (byte) (Integer.parseInt(hexString.substring(i, i + 2), 16) & 0xff);
        }
        return bytes;
    }

    /**
     * 字节数组转16进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}

