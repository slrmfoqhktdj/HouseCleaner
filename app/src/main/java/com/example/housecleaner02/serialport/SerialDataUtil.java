package com.example.housecleaner02.serialport;

import android.util.Log;

public class SerialDataUtil {
    private static final byte RECV_START_BIT = 0x5A;
    private static final byte RECV_START_BIT_2 = (byte) 0xA5;

    private static final int DATA_LENGTH = 8;

    private static long mRecvTime = 0L;
    private final static long mInternalTime = 300;
    private static byte[] cacheData;
    private static int curCacheRelLength = 0;

    /**
     * 串口数据校验
     *
     * @param buf 串口数据
     * @return byte[] 校验成功的串口数据
     */
    public synchronized static byte[] recvDataAnalysis(byte[] buf) {
        if (buf == null) {
            return null;
        }

        if (buf[0] == RECV_START_BIT && buf[1] == RECV_START_BIT_2) {
            // 数据是以 RECV_START_BIT开始的,且必定是第一包数据
            mRecvTime = System.currentTimeMillis();
            cacheData = null;
            int length = buf.length;
            if (length < DATA_LENGTH) {
                // 数据长度不够,缓存到cacheData
                cacheData = new byte[DATA_LENGTH];
                System.arraycopy(buf, 0, cacheData, 0, length);
                curCacheRelLength = length;
                Log.d("daibin", "第一包数据长度不够,缓存到cacheData");
            } else {
                // 第一包数据长度符合，校验数据
                // 收到的数据长度大于或等于DATA_LENGTH
                byte[] realData = new byte[DATA_LENGTH];
                System.arraycopy(buf, 0, realData, 0, realData.length);

                // 计算crc-16校验码
                byte[] calcCrcData = new byte[DATA_LENGTH - 2];
                System.arraycopy(buf, 0, calcCrcData, 0, calcCrcData.length);
                byte[] crc16 = crc16(calcCrcData);

                if (realData[DATA_LENGTH - 1] == crc16[1] && realData[DATA_LENGTH - 2] == crc16[0]) {
                    mRecvTime = 0L;
                    cacheData = null;
                    curCacheRelLength = 0;
                    return realData;
                } else {
                    Log.d("daibin", "realData[DATA_LENGTH - 1]:" + realData[DATA_LENGTH - 1] + "  ,crc16[1]:" + crc16[1]);
                    Log.d("daibin", "realData[DATA_LENGTH - 2]:" + realData[DATA_LENGTH - 2] + "  ,crc16[1]:" + crc16[0]);
                    Log.d("daibin", "数据校验不正确");
                }
            }
        } else {
            // 数据不是以 RECV_START_BIT和RECV_START_BIT_2开始的
            if (cacheData == null) {
                // cacheData为空说明是第一包数据，直接返回null
                Log.d("daibin", "cacheData为空说明是第一包数据，直接返回null");
                return null;
            }

            if (System.currentTimeMillis() - mRecvTime > mInternalTime) {
                Log.d("daibin", "Serial port receive timeout:" + (System.currentTimeMillis() - mRecvTime) + "ms");
                // 和第一包数据超出间隔时间，丢弃
                mRecvTime = 0L;
                cacheData = null;
                curCacheRelLength = 0;
                return null;
            }

            int length = buf.length;
            if (curCacheRelLength + length <= DATA_LENGTH) {
                System.arraycopy(buf, 0, cacheData, curCacheRelLength, length);
            } else {
                System.arraycopy(buf, 0, cacheData, curCacheRelLength, Math.abs(DATA_LENGTH - curCacheRelLength));
            }
            curCacheRelLength += length;

            if (curCacheRelLength >= DATA_LENGTH) {
                // 第一包数据长度符合，校验数据
                // 收到的数据长度大于或等于DATA_LENGTH
                byte[] realData = new byte[DATA_LENGTH];
                System.arraycopy(cacheData, 0, realData, 0, realData.length);
                // 计算crc-16校验码
                byte[] calcCrcData = new byte[DATA_LENGTH - 2];
                System.arraycopy(cacheData, 0, calcCrcData, 0, calcCrcData.length);
                byte[] crc16 = crc16(calcCrcData);

                if (realData[DATA_LENGTH - 1] == crc16[1] && realData[DATA_LENGTH - 2] == crc16[0]) {
                    mRecvTime = 0L;
                    cacheData = null;
                    curCacheRelLength = 0;
                    return realData;
                } else {
                    Log.d("daibin", "realData[DATA_LENGTH - 1]:" + realData[DATA_LENGTH - 1] + "  ,crc16[1]:" + crc16[1]);
                    Log.d("daibin", "realData[DATA_LENGTH - 2]:" + realData[DATA_LENGTH - 2] + "  ,crc16[1]:" + crc16[0]);
                    Log.d("daibin", "数据校验不正确");
                }
            } else {
                Log.d("daibin", "多包拼接数据长度不够,缓存到cacheData");
            }
        }
        return null;
    }

    /**
     * 发送串口数据
     *
     * @param addr 地址
     * @param key  键值
     * @return byte[] 串口数据
     */
    public static byte[] sendData(String addr, String key) {
        byte[] data = new byte[DATA_LENGTH];
        data[0] = RECV_START_BIT;
        data[1] = RECV_START_BIT_2;

        byte[] addrByte = hexString2byte(addr);
        data[2] = addrByte[0];
        data[3] = addrByte[1];

        byte[] keyByte = hexString2byte(key);
        data[4] = keyByte[0];
        data[5] = keyByte[1];

        byte[] crcData = new byte[DATA_LENGTH - 2];
        System.arraycopy(data, 0, crcData, 0, crcData.length);
        byte[] crc16 = crc16(crcData);
        data[6] = crc16[0];
        data[7] = crc16[1];
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

